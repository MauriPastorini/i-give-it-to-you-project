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
        [Authorize(Roles ="Admin, Normal")]
        public IHttpActionResult UpdateUser(string id, UserModel userModel)
        {
            if(!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            if(id != userModel.UserName)
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
        public IHttpActionResult SetAsAdmin(string id)
        {
            IHttpActionResult dbResult = SetAsAdmin_Owin(id);
            if(dbResult is OkResult)
            {
                return SetAsAdmin_VeniPorEl(id);
            }
            else
            {
                return dbResult;
            }
        }

        private IHttpActionResult SetAsAdmin_VeniPorEl(string id)
        {
            string error = "This user is already an admin!";
            IHttpActionResult result = Ok();
            User userToModify = userService.GetByUserName(id);
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
                userToDel.UserName = id;
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
    }
}
