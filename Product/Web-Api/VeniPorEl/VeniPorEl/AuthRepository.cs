using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using VeniPorEl.Models;

namespace VeniPorEl
{
    public class AuthRepository : IDisposable
    {

        private AuthContext _ctx;

        private UserManager<IdentityUser> _userManager;
        private RoleManager<IdentityRole> _roleManager;

        public AuthRepository()
        {
            _ctx = new AuthContext();
            _userManager = new UserManager<IdentityUser>(new UserStore<IdentityUser>(_ctx));
            _roleManager = new RoleManager<IdentityRole>(new RoleStore<IdentityRole>(_ctx));
        }

        public async Task<IdentityResult> RegisterUser(UserModel userModel)
        {
            IdentityUser user = new IdentityUser
            {
                UserName = userModel.UserName
            };
            IdentityRole role = _roleManager.FindByName("Normal");
            if(role == null)
            {
                role = new IdentityRole("Normal");
                await _roleManager.CreateAsync(role);
            }
            var result = await _userManager.CreateAsync(user, userModel.Password);
            if(result.Succeeded)
            {
                _userManager.AddToRole(user.Id, role.Name);
            }
            return result;
        }

        public IdentityResult SetAsAdmin(string id)
        {
            string roleToSet = "Admin";
            IdentityUser user = _userManager.FindByName(id);
            IdentityResult result = IdentityResult.Failed();
            if(user != null)
            {
                IdentityRole role = _roleManager.FindByName(roleToSet);
                if(role == null)
                {
                    role = new IdentityRole(roleToSet);
                    _roleManager.CreateAsync(role);
                }
                result = _userManager.AddToRole(user.Id, role.Name);
            }
            return result;
        }

        public async Task<IdentityResult> SetAsAdmin(UserModel userModel)
        {
            string roleToSet = "Admin";
            IdentityUser user = _userManager.FindByName(userModel.UserName);
            IdentityResult result = IdentityResult.Failed();
            if(user != null)
            {
                IdentityRole role = _roleManager.FindByName(roleToSet);
                if(role == null)
                {
                    role = new IdentityRole(roleToSet);
                    await _roleManager.CreateAsync(role);
                }
                result = _userManager.AddToRole(user.Id, role.Name);
            }
            return result;
        }

        public IdentityResult DeleteUser(UserModel userModel)
        {
            IdentityUser user = _userManager.FindByName(userModel.UserName);
            IdentityResult result = IdentityResult.Failed();
            if(user != null)
            {
                result = _userManager.Delete(user);
            }
            return result;
        }

        public IdentityResult UpdateUser(UserModel userModel)
        {
            IdentityUser user = _userManager.FindByName(userModel.UserName);
            IdentityResult result = IdentityResult.Failed();
            if(user != null)
            {
                if(userModel.IsAdmin)
                {
                    SetAsAdmin(userModel);
                }
                _userManager.RemovePassword(user.Id);
                _userManager.AddPassword(user.Id, userModel.Password);
                result = _userManager.Update(user);
            }
            return result;
        }

        public async Task<IdentityUser> FindUser(string userName, string password)
        {
            IdentityUser user = await _userManager.FindAsync(userName, password);
            return user;
        }

        public void Dispose()
        {
            _ctx.Dispose();
            _userManager.Dispose();
            _roleManager.Dispose();
        }

        public List<string> GetRoles(string userId)
        {
            return _userManager.GetRoles(userId).ToList();
        }

        public IdentityResult DeleteAdminRole(UserModel userModel)
        {
            IdentityUser user = _userManager.FindByName(userModel.UserName);
            IdentityResult result = IdentityResult.Failed();
            if(user != null)
            {
                IdentityRole role = _roleManager.FindByName("Admin");
                if(role != null)
                {
                    result = _userManager.RemoveFromRole(user.Id, role.Name);
                }
            }
            return result;
        }
    }
}