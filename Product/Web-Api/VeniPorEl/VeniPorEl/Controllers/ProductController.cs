using Api.Services;
using Data;
using System.Web.Http;
using System.Web.Http.Cors;
using System.Web.Http.Description;
using System.Web.Http.Results;
using VeniPorEl.Models;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace VeniPorEl.Controllers
{
    [RoutePrefix("api/Product")]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class ProductController : ApiController
    {
        IProductService productService;

        public ProductController()
        {
            productService = new ProductService();
        }

        [HttpPost]
        [ResponseType(typeof(ProductModel))]
        public IHttpActionResult RegisterProduct(ProductModel productModel)
        {
            if (productModel == null)
            {
                return BadRequest("No data sent.");
            }
            else if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            else
            {
                try
                {
                    int productId = productService.CreateWithNameCategoryStateLocation(productModel.Name, productModel.CategoryId, productModel.State, productModel.Latitude, productModel.Longitude);
                    return Ok(productId);
                }
                catch (ArgumentException ex)
                {
                    return BadRequest(ex.Message);
                }
            }
        }


        [HttpPost]
        [Route("{productId}")]
        [ResponseType(typeof(ImageModel))]
        public IHttpActionResult UploadProductImage(int productId, ImageModel imageModel)
        {
            if (imageModel == null || productId == 0)
            {
                return BadRequest("Error in data format.");
            }
            try
            {
                byte[] photo = Convert.FromBase64String(imageModel.ImageBase64);
                productService.AddImageToProduct(productId, imageModel.ImageName, photo);
                return Ok();
            }
            catch (FormatException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet]
        [Route("{productId}/photo")]
        [ResponseType(typeof(ICollection<ImageModel>))]
        public IHttpActionResult GetProductImage(int productId)
        {
            if (productId == 0)
            {
                return BadRequest("Error in data format.");
            }
            try
            {
                ICollection<ProductImage> photos = productService.GetImagesFromProductId(productId);
                ICollection<ImageModel> imageModels = new List<ImageModel>();
                foreach (var photo in photos)
                {
                    ImageModel image = new ImageModel();
                    var base64Image = Convert.ToBase64String(photo.Image);
                    image.ImageBase64 = base64Image;
                    image.ImageName = ProductImage.GetNameFromPath(photo.ImagePath);
                    imageModels.Add(image);
                }
                return Ok(imageModels);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet]
        [Route("{productId}")]
        [ResponseType(typeof(ICollection<ImageModel>))]
        public IHttpActionResult GetCompleteProductImage(int productId)
        {
            if (productId == 0)
            {
                return BadRequest("Error in data format.");
            }
            var product = productService.GetProduct(productId);
            if (product == null)
            {
                return NotFound();
            }
            return Ok(product);
        }
    }
}