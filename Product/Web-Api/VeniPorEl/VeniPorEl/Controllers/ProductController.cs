using Api.Services;
using Data;
using System.Web.Http;
using System.Web.Http.Cors;
using System.Web.Http.Description;
using System.Web.Http.Results;
using VeniPorEl.Models;
using System;

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
            try
            {
                productService.AddImageToProduct(productId, imageModel.ImageName, imageModel.ImageByteArray);
                return Ok();
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}