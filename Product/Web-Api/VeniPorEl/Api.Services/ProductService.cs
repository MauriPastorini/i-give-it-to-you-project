using Data;
using Data.Repository;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
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

        public int CreateWithNameCategoryStateLocation(string name, int productCategoryId, int productStateId, int productLatitude, int productLongitude)
        {
            try
            {
                Location productLocation = Location.CreateWithLatLon(productLatitude, productLongitude);
                Category productCategory = LoadProductCategory(productCategoryId);
                ProductState productState = LoadProductState(productStateId);
                Product productToCreate = Product.CreateWithNameCategoryStateLocation(name, productCategory, productState, productLocation);

                productToCreate.Category = unitOfWork.CategoryRepository.Get(productCategoryId);
                productToCreate.State = unitOfWork.ProductStateRepository.Get(productStateId);

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
            ICollection<Product> unmoderatedProducts = unitOfWork.ProductRepository.Find(p=>p.Moderated == false).ToList();
            return unmoderatedProducts;
        }

        public ICollection<Product> GetProductsByCategory(int categoryId)
        {
            ICollection<Product> productsByCategory = null;
            if (categoryId!=0)
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
            Product product = unitOfWork.ProductRepository.Find(p=>p.ProductId == productId).FirstOrDefault();
            return product;
        }
    }
}
