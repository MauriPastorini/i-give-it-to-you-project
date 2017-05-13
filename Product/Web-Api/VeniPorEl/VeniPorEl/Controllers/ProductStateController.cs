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
    [RoutePrefix("api/ProductState")]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class ProductStateController : ApiController
    {
        IProductStateService stateService;
        public ProductStateController()
        {
            stateService = new ProductStateService();
        }

        public IHttpActionResult GetAll()
        {
            try
            {
                var categories = stateService.GetAllCategories();
                return Ok(categories);
            }
            catch (Exception ex)
            {
                return InternalServerError(ex);
            }
        }
    }
}