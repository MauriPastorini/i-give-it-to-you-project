using Api.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Cors;

namespace VeniPorEl.Controllers
{
    [RoutePrefix("api/Category")]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class CategoryController : ApiController
    {
        CategoryService categoryService;

        public CategoryController()
        {
            categoryService = new CategoryService();
        }

        public IHttpActionResult GetAll()
        {
            try
            {
                var categories = categoryService.GetAllCategories();
                return Ok(categories);
            }
            catch (Exception ex)
            {
                return InternalServerError(ex);
            }
        }
    }
}