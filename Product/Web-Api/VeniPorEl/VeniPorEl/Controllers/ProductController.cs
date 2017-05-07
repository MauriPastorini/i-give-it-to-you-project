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
        ProductService productService;

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
            else if(!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }else
            {
                Category productCategory = null;
                IHttpActionResult loadProductCategory = LoadProductCategory(ref productCategory,productModel);
                if (!(loadProductCategory is OkResult))
                    return loadProductCategory;
                ProductState productState = null;
                IHttpActionResult loadProductState = LoadProductState(ref productState, productModel);
                if (!(loadProductState is OkResult))
                    return loadProductState;
                try
                {
                    int productId = productService.CreateWithNameCategoryStateLocation(productModel.Name, productCategory, productState, productModel.Latitude, productModel.Longitude);
                    return Ok(productId);
                }
                catch (ArgumentException ex)
                {
                    return BadRequest(ex.Message);
                }
            }           
        }

        private IHttpActionResult LoadProductState(ref ProductState productState, ProductModel productModel)
        {
            
            IProductStateService productStateService = new ProductStateService();
            productState = productStateService.GetProductStateById(productModel.State);
            if (productState == null)
            {
                return NotFound();
            }
            return Ok();
        }

        private IHttpActionResult LoadProductCategory(ref Category productCategory, ProductModel productModel)
        {
            ICategoryService categoryService = new CategoryService();          
            productCategory = categoryService.GetCategoryById(productModel.CategoryId);
            if (productCategory == null)
            {
                return NotFound();
            }
            return Ok();
        }

    }
}