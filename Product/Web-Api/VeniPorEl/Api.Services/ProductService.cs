using Data;
using Data.Repository;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Api.Services
{
    public class ProductService : IProductService
    {

        private readonly IUnitOfWork unitOfWork;

        public ProductService()
        {
            unitOfWork = new UnitOfWork();
        }

        public ProductService(IUnitOfWork unitOfWork)
        {
            this.unitOfWork = unitOfWork;
        }

        public int CreateWithNameCategoryStateLocation(string name, int productCategoryId, int productStateId, double productLatitude, double productLongitude, int userId)
        {
            try
            {
                Location productLocation = Location.CreateWithLatLon(productLatitude, productLongitude);
                Category productCategory = LoadProductCategory(productCategoryId);
                ProductState productState = LoadProductState(productStateId);
                Product productToCreate = Product.CreateWithNameCategoryStateLocation(name, productCategory, productState, productLocation);
                User userOwner = new UserService().GetById(userId);
                if (userOwner == null)
                    return 0;

                productToCreate.Category = unitOfWork.CategoryRepository.Get(productCategoryId);
                productToCreate.State = unitOfWork.ProductStateRepository.Get(productStateId);
                productToCreate.UserOwnProduct = userOwner;

                unitOfWork.UsersRepository.Atach(userOwner);
                unitOfWork.ProductRepository.Add(productToCreate);
                unitOfWork.Save();
                return productToCreate.ProductId;
            }
            catch (ArgumentException ex)
            {
                throw ex;
            }
        }

        private Category LoadProductCategory(int productCategoryId)
        {
            ICategoryService categoryService = new CategoryService();
            Category productCategoryToReturn = categoryService.GetCategoryById(productCategoryId);
            if (productCategoryToReturn == null)
            {
                throw new ArgumentException("Given Category doesn't exists.");
            }
            return productCategoryToReturn;
        }

        private ProductState LoadProductState(int productStateId)
        {

            IProductStateService productStateService = new ProductStateService();
            ProductState productStateToReturn = productStateService.GetProductStateById(productStateId);
            if (productStateToReturn == null)
            {
                throw new ArgumentException("Given Product State doesn't exists.");
            }

            return productStateToReturn;
        }

        public bool AddImageToProduct(int productId, string imageName, byte[] imageByteArray)
        {
            Product productToUpdate = unitOfWork.ProductRepository.Get(productId);
            ProductImage productImage = null;
            if (productToUpdate == null)
                throw new ArgumentException("Product Not Found");

            string filename = ProductImage.EncodePathForImage(productId, imageName);
            using (var imageToSave = Image.FromStream(new MemoryStream(imageByteArray)))
            {
                ImageFormat imageFormat;
                if (ImageFormat.Png.Equals(imageToSave.RawFormat))
                {
                    filename += "png";
                    imageFormat = ImageFormat.Png;
                }
                else
                {
                    filename += "jpg";
                    imageFormat = ImageFormat.Jpeg;
                }
                string path = filename;
                productImage = ProductImage.CreateWithPath(path);
                imageToSave.Save(path, imageFormat);
            }
            productToUpdate.ProductImages.Add(productImage);
            unitOfWork.Save();
            return true;
        }

        public ICollection<Product> GetUnmoderatedProducts()
        {
            ICollection<Product> unmoderatedProducts = unitOfWork.ProductRepository.Find(p => p.Moderated == false).ToList();
            return unmoderatedProducts;
        }

        public ICollection<Product> GetProductsByCategory(int categoryId)
        {
            ICollection<Product> productsByCategory = null;
            if (categoryId != 0)
            {
                productsByCategory = unitOfWork.ProductRepository.Find(p => p.Category.CategoryId == categoryId && p.Moderated == true).ToList();
            }
            else
            {
                productsByCategory = unitOfWork.ProductRepository.Find(p => p.Moderated == true).ToList();
            }

            return productsByCategory;
        }

        public int AcceptProduct(int productId)
        {
            Product productToUpdate = unitOfWork.ProductRepository.Get(productId);
            if (productToUpdate == null)
                throw new ArgumentException("Product Not Found");
            productToUpdate.Moderated = true;
            unitOfWork.Save();
            return productToUpdate.ProductId;
        }


        public void DeleteProduct(int productId)
        {
            Product productToUpdate = unitOfWork.ProductRepository.Get(productId);
            if (productToUpdate == null)
                throw new ArgumentException("Product Not Found");
            ICollection<ProductImage> productImages = unitOfWork.ProductImagesRepository.Find(image => image.Product.ProductId == productId).ToList();
            foreach (var productImage in productImages)
            {
                unitOfWork.ProductImagesRepository.Remove(productImage);
            }
            unitOfWork.ProductRepository.Remove(productToUpdate);
            unitOfWork.Save();
        }

        public ICollection<ProductImage> GetImagesFromProductId(int productId)
        {
            Product product = unitOfWork.ProductRepository.Find(c => c.ProductId == productId).FirstOrDefault();
            if (product == null)
            {
                return null;
            }
            ICollection<ProductImage> productImage = unitOfWork.ProductImagesRepository.Find(c => c.Product.ProductId == productId).ToList();
            return DownloadImages(productImage);
        }

        private ICollection<ProductImage> DownloadImages(ICollection<ProductImage> productImages)
        {
            foreach (var productImage in productImages)
            {
                var webClient = new WebClient();
                byte[] imageBytes = webClient.DownloadData(productImage.ImagePath);
                productImage.Image = imageBytes;
            }
            return productImages;
        }

        public void Dispose()
        {
            unitOfWork.Dispose();
        }

        public Product GetProduct(int productId)
        {
            Product product = unitOfWork.ProductRepository.Find(p => p.ProductId == productId).FirstOrDefault();
            return product;
        }

        public void CreateSolicitudeForProduct(int productId, int accountId)
        {
            Product product = unitOfWork.ProductRepository.Find(p => p.ProductId == productId).FirstOrDefault();
            if (product == null)
            {
                throw new KeyNotFoundException("Product doesnt exists");
            }
            if (product.UserSolicitudeProductId != null || product.UserSolicitudeProductId == 0)
            {
                throw new ProductAlreadySolicitatedException();
            }
            User userSolicitude = new UserService().GetById(accountId);
            if (userSolicitude == null)
            {
                throw new KeyNotFoundException("User that made the solicitude doesnt exists");
            }
            String emailUserWhoWantIt = userSolicitude.Email;
            User userOwnerOfProduct = new UserService().GetById(product.UserOwnProductId);
            if (userOwnerOfProduct == null)
            {
                throw new KeyNotFoundException("User owner doesnt exists");
            }
            String emailUserProductOwner = userOwnerOfProduct.Email;
            product.UserSolicitudeProduct = userSolicitude;
            unitOfWork.UsersRepository.Atach(userSolicitude);
            unitOfWork.ProductRepository.Update(product);
            unitOfWork.Save();

            SendEmail(product, emailUserProductOwner, "Quieren tu producto!", "Felicitaciones! Quieren tu producto: " + product.ToString() + userSolicitude.ToString());
            SendEmail(product, emailUserWhoWantIt, "Se ha enviado tu solicitud", "Felicitaciones! A la brevedad la persona se pondra en contacto contigo. Contacto: " + userOwnerOfProduct.ToString());
        }

        private static void SendEmail(Product product, String email, String subject, String message)
        {
            new Thread(() =>
            {
                Thread.CurrentThread.IsBackground = true;
                String emailVeni = "venisyestuyo@gmail.com";
                String emailPass = "venis1234";
                using (MailMessage mail = new MailMessage())
                {
                    mail.From = new MailAddress(emailVeni);
                    mail.To.Add(email);
                    mail.Subject = subject;
                    mail.Body = message;
                    mail.IsBodyHtml = true;
                    using (SmtpClient smtp = new SmtpClient("smtp.gmail.com", 587))
                    {
                        smtp.Credentials = new NetworkCredential(emailVeni, emailPass);
                        smtp.EnableSsl = true;
                        smtp.Send(mail);
                    }
                }
            }).Start();
        }
        public void DeleteSolicitudeForProduct(int productId, int accountId, string userMakingSolicitudeId)
        {
            Product product = unitOfWork.ProductRepository.Find(p => p.ProductId == productId).FirstOrDefault();
            if (product == null)
            {
                throw new KeyNotFoundException("Product doesnt exists");
            }
            User userSolicitude = new UserService().GetById(accountId);
            if (userSolicitude == null)
            {
                throw new KeyNotFoundException("User that made the solicitude doesnt exists");
            }
            User userConnected = new UserService().GetByUserName(userMakingSolicitudeId);
            if (userConnected == null)
            {
                throw new InvalidOperationException("User connected from token doesnt exists");
            }
            if (userSolicitude.UserId != userConnected.UserId)
            {
                throw new UnauthorizedAccessException("The solicitude is from a different user");
            }

            User userOwnerOfProduct = new UserService().GetById(product.UserOwnProductId);
            if (userOwnerOfProduct == null)
            {
                throw new KeyNotFoundException("User owner doesnt exists");
            }
            Product productWithUser = unitOfWork.ProductRepository.GetProductWithUserSolicitated(productId);

            productWithUser.UserSolicitudeProduct = null;
            unitOfWork.Save();

            String emailUserWhoWantIt = userSolicitude.Email;
            String emailUserProductOwner = userOwnerOfProduct.Email;
            SendEmail(product, emailUserProductOwner, "Ya no quieren tu producto", "Lamentamos informarle que ya no quieren su producto: " + product.ToString() + userSolicitude.ToString());
            SendEmail(product, emailUserWhoWantIt, "Se ha enviado la cancelación de su solicitud", "Lamentamos que ya no lo quiera mas! Contacto del propietario: " + userOwnerOfProduct.ToString());
        }

        public ICollection<Product> GetProductsSolicitatedByUser(int userId)
        {
            ICollection<Product> productsSolicitatedByUser = unitOfWork.ProductRepository.Find(p => p.UserSolicitudeProductId == userId && p.Moderated && p.Review == 0).ToList();
            foreach (var item in productsSolicitatedByUser)
            {
                if (item.State == null)
                {
                    item.State = unitOfWork.ProductStateRepository.Find(s => s.ProductStateId == item.StateId).FirstOrDefault();
                }
                if (item.Category == null)
                {
                    item.Category = unitOfWork.CategoryRepository.Find(s => s.CategoryId == item.CategoryId).FirstOrDefault();
                }
            }
            return productsSolicitatedByUser;
        }

        public void RateProductSolicitated(int productId, int rate, string userNameConnected)
        {
            Product product = unitOfWork.ProductRepository.Find(p => p.ProductId == productId).FirstOrDefault();
            if (product == null)
            {
                throw new KeyNotFoundException("Product doesnt exists");
            }
            if (product.UserSolicitudeProductId == null || product.UserSolicitudeProductId == 0)
            {
                throw new InvalidOperationException("Product was not solicitated");
            }
            int idUserThatSolicitatedProduct = (int)product.UserSolicitudeProductId;
            User userSolicitude = new UserService().GetById(idUserThatSolicitatedProduct);
            if (userSolicitude == null)
            {
                throw new KeyNotFoundException("User that made the solicitude doesnt exists");
            }
            User userConnected = new UserService().GetByUserName(userNameConnected);
            if (userConnected == null)
            {
                throw new InvalidOperationException("User connected from token doesnt exists");
            }
            if (userSolicitude.UserId != userConnected.UserId)
            {
                throw new UnauthorizedAccessException("The solicitude is from a different user");
            }

            User userOwnerOfProduct = new UserService().GetById(product.UserOwnProductId);
            if (userOwnerOfProduct == null)
            {
                throw new KeyNotFoundException("User owner doesnt exists");
            }

            product.Review = rate;
            unitOfWork.Save();

            String emailUserWhoWantIt = userSolicitude.Email;
            String emailUserProductOwner = userOwnerOfProduct.Email;

            SendEmail(product, emailUserProductOwner, "Felicitaciones te calificaron por un producto!", "Te calificaron con: " + rate + ". Producto: " + product.ToString() + userSolicitude.ToString());
            SendEmail(product, emailUserWhoWantIt, "Se ha enviado tu calificación", "Calificaste con: " + rate + ". Al usuario y producto " + product.ToString() + userSolicitude.ToString());
        }
    }
}
