using Data;
using Microsoft.AspNet.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using VeniPorEl.Models;
using System.Web.Http.Results;
using Api.Services;
using System.Web.Http.Description;
using Microsoft.AspNet.Identity.EntityFramework;

namespace VeniPorEl.Controllers
{
    [RoutePrefix("api/Account")]
    public class AccountController : ApiController
    {

        private AuthRepository _repo = null;
        private IUserService userService;

        public AccountController()
        {
            userService = new UserService();
            _repo = new AuthRepository();
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public IHttpActionResult GetUnmoderatedUsers()
        {
            try
            {
                List<User> users = userService.GetAll().ToList();
                var results = new List<UserModel>();
                for(int i = 0; i < users.Count; i++)
                {
                    if(users[i].UserName != User.Identity.Name && !_repo.IsAdmin(users[i].UserName))
                    {
                        var model = new UserModel();
                        model.UserId = users[i].UserId.ToString();
                        model.UserName = users[i].UserName;
                        model.Email = users[i].Email;
                        results.Add(model);
                    }
                    
                }
                return Ok(results);   
            }
            catch(Exception ex)
            {
                return InternalServerError(ex);
            }
        }

        [HttpPost]
        [AllowAnonymous]
        public async Task<IHttpActionResult> Register(UserModel userModel)
        {
            if(!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            User user;
            try
            {
                user = Data.User.CreateWithNameEmailPasswordAndRole(userModel.UserName, userModel.Email, userModel.Password, new NormalUserRole());
            }
            catch(ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            IHttpActionResult result = await Register_Owin(userModel);
            if(result is OkResult)
            {
                try
                {
                    Register_VeniPorEl(user);
                }
                catch(Exception ex)
                {
                    DeleteUser_Owin(userModel);
                    return BadRequest(ex.Message);
                }
                return Ok(user);
            }
            return result;
        }

        

        [HttpPut]
        [Route("{id}")]
        [Authorize(Roles ="Admin")]
        public IHttpActionResult UpdateUser(string id, UserModel userModel)
        {
            if(!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            IHttpActionResult dbResult = UpdateUser_Owin(userModel);
            if(dbResult is OkResult)
            {
                return UpdateUser_VeniPorEl(userModel);
            } else
            {
                return dbResult;
            }
        }

        [HttpPost]
        [Route("Admin/{id}")]
        [Authorize(Roles ="Admin")]
        public IHttpActionResult SetAsAdmin(int id, UserModel userModel)
        {
            IHttpActionResult dbResult = SetAsAdmin_Owin(userModel.UserName);
            if(dbResult is OkResult)
            {
                return SetAsAdmin_VeniPorEl(id);
            }
            else
            {
                return dbResult;
            }
        }

        [HttpDelete]
        [Route("{id}")]
        [Authorize(Roles = "Admin")]
        public IHttpActionResult Delete(string id, UserModel user)
        {
            int idd = Int32.Parse(id);
            IHttpActionResult dbResult = DeleteUser_Owin(user);
            if(dbResult is OkResult)
            {
                Delete_VeniPorEl(idd);
                return Ok();
            }
            else
            {
                return dbResult;
            }
        }

        private IHttpActionResult SetAsAdmin_VeniPorEl(int id)
        {
            string error = "This user is already an admin!";
            IHttpActionResult result = Ok();
            User userToModify = userService.GetById(id);
            if(userToModify == null)
            {
                result = NotFound();
            }
            else if(userToModify.RoleId == (int)Data.User.Roles.Admin)
            {
                ModelState.AddModelError("", error);
                result = BadRequest(ModelState);
            }
            else
            {
                userToModify.Role = new AdminRole();
                try
                {
                    userService.Update(userToModify);
                }
                catch(Exception ex)
                {
                    result = InternalServerError(ex);
                }
            }
            if(!(result is OkResult))
            {
                UserModel userToDel = new UserModel();
                userToDel.UserId = id.ToString();
                DeleteAdminRole_Owin(userToDel);
            }
            return result;
        }

        private IHttpActionResult DeleteAdminRole_Owin(UserModel userToDel)
        {
            IdentityResult result = _repo.DeleteAdminRole(userToDel);
            IHttpActionResult errorResult = GetErrorResult(result);
            if(errorResult != null)
            {
                return errorResult;
            }
            return Ok();
        }

        private IHttpActionResult SetAsAdmin_Owin(string id)
        {
            IdentityResult result = _repo.SetAsAdmin(id);
            IHttpActionResult errorResult = GetErrorResult(result);
            if(errorResult != null)
            {
                return errorResult;
            }
            return Ok();
        }

        private IHttpActionResult DeleteUser_Owin(UserModel userModel)
        {
            IdentityResult result = _repo.DeleteUser(userModel);
            IHttpActionResult errorResult = GetErrorResult(result);
            if(errorResult != null)
            {
                return errorResult;
            }
            return Ok();
        }

        private void Register_VeniPorEl(User user)
        {
            userService.Register(user);
        }

        private void Delete_VeniPorEl(int id)
        {
            userService.Delete(id);
        }

        private async Task<IHttpActionResult> Register_Owin(UserModel userModel)
        {
            IdentityResult result = await _repo.RegisterUser(userModel);
            IHttpActionResult errorResult = GetErrorResult(result);
            if(errorResult != null)
            {
                return errorResult;
            }
            return Ok();
        }

        private IHttpActionResult UpdateUser_VeniPorEl(UserModel userModel)
        {
            string passOld = "";
            IHttpActionResult result = Ok();
            User userToModify = userService.GetByUserName(userModel.UserName);
            if(userToModify == null)
            {
                result = NotFound();
            }
            else
            {
                passOld = userToModify.Pass;
                userToModify.Pass = userModel.Password;
                try
                {
                    userService.Update(userToModify);
                }
                catch(Exception ex)
                {
                    result = InternalServerError(ex);
                }
            }
            if(!(result is OkResult))
            {
                UserModel userRallBack = new UserModel();
                if(passOld == "")
                {
                    return InternalServerError();
                }
                userRallBack.Password = passOld;
                UpdateUser_Owin(userRallBack);
            }
            return result;
        }

        private IHttpActionResult UpdateUser_Owin(UserModel userRallBack)
        {
            IdentityResult result = _repo.UpdateUser(userRallBack);
            IHttpActionResult errorResult = GetErrorResult(result);
            if(errorResult != null)
            {
                return errorResult;
            }
            return Ok();
        }

        protected override void Dispose(bool disposing)
        {
            if(disposing)
            {
                _repo.Dispose();
            }
            base.Dispose(disposing);
        }

        private IHttpActionResult GetErrorResult(IdentityResult result)
        {
            if(result == null)
            {
                return InternalServerError();
            }
            if(!result.Succeeded)
            {
                if(result.Errors != null)
                {
                    foreach(string error in result.Errors)
                    {
                        ModelState.AddModelError("", error);
                    }
                }
                if(ModelState.IsValid)
                {
                    return BadRequest();
                }
                return BadRequest(ModelState);
            }
            return null;
        }

        [HttpGet]
        [Route("admin")]
        [Authorize(Roles = "Admin")]
        public IHttpActionResult IsAdmin()
        {
            return Ok(Data.User.Roles.Admin);
        }

        [HttpGet]
        [Route("{idAccount}/product")]
        [Authorize(Roles = "Admin, Normal")]
        [ResponseType(typeof(ICollection<Product>))]
        public IHttpActionResult GetProductsOfAccount(int idAccount)
        {
            ICollection<Product> productsSolicitatedByUser = new ProductService().GetProductsSolicitatedByUser(idAccount);
            ICollection<ProductModel> productsResu = new List<ProductModel>();
            foreach (var item in productsSolicitatedByUser)
            {
                ProductModel productModel = new ProductModel();
                productModel.ProductId = item.ProductId;
                productModel.Name = item.Name;
                productModel.CategoryId = item.CategoryId;
                productModel.CategoryName = item.Category.Name;
                productModel.StateId = item.StateId;
                productModel.StateName = item.State.Name;
                productModel.Latitude = item.Location.Latitude;
                productModel.Longitude = item.Location.Longitude;
                productModel.UserId = item.UserOwnProductId;
                productsResu.Add(productModel);
            }
            return Ok(productsResu);
        }

        [HttpGet]
        [Route("identifier")]
        [Authorize(Roles = "Admin, Normal")]
        [ResponseType(typeof(ICollection<Product>))]
        public IHttpActionResult GetIdOfTokenAccount()
        {
            var us = User;
            var userIdentity = us.Identity;
            int idAccount = userService.GetUserIdByUserName(userIdentity.Name);
            return Ok(idAccount);
        }
    }
}
