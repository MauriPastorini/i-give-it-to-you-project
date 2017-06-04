using Api.Services;
using Data;
using Newtonsoft.Json.Serialization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Formatting;
using System.Web.Http;
using VeniPorEl.Models;

namespace VeniPorEl
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            DefineSuperUser();
            // Web API configuration and services

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            var jsonFormatter = config.Formatters.OfType<JsonMediaTypeFormatter>().First();
            jsonFormatter.SerializerSettings.ContractResolver = new CamelCasePropertyNamesContractResolver();
        }

        private static async void DefineSuperUser()
        {
            AuthRepository _repo = new AuthRepository();
            string superUserName = "superUser";
            string superUserPass = "Super123";
            IUserService userService = new UserService();
            if(_repo.FindUser(superUserName, superUserPass).Result == null)
            {
                UserModel superUser = new UserModel();
                superUser.UserName = superUserName;
                superUser.Password = superUserPass;
                superUser.ConfirmPassword = superUserPass;
                await _repo.RegisterUser(superUser);
                await _repo.SetAsAdmin(superUser);
            }
            if(userService.GetByUserName(superUserName) == null)
            {
                User superUserDb = User.CreateWithNamePasswordAndRole(superUserName, superUserPass, new AdminRole());
                userService.Register(superUserDb);
            }
            {

            }
        }
    }
}
