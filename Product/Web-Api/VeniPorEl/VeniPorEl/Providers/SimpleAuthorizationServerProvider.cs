using Microsoft.Owin.Security.OAuth;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Threading.Tasks;
using Microsoft.AspNet.Identity.EntityFramework;
using System.Security.Claims;

namespace VeniPorEl.Providers
{
    public class SimpleAuthorizationServerProvider : OAuthAuthorizationServerProvider
    {

        public override async Task ValidateClientAuthentication(OAuthValidateClientAuthenticationContext context)
        {
            context.Validated();
        }

        public override async Task GrantResourceOwnerCredentials(OAuthGrantResourceOwnerCredentialsContext context)
        {
            var userRoles = new List<string>();
            context.OwinContext.Response.Headers.Add("Access-Control-Allow-Origin", new[] { "*" });
            using(AuthRepository _repo = new AuthRepository())
            {
                IdentityUser user = await _repo.FindUser(context.UserName, context.Password);
                if(user == null)
                {
                    context.SetError("invalid_grant", "The user name or password is incorrect.");
                    return;
                }
                userRoles = _repo.GetRoles(user.Id);
            }

            var identity = GetAcualUserRoles(context, userRoles);

            context.Validated(identity);
        }

        private ClaimsIdentity GetAcualUserRoles(OAuthGrantResourceOwnerCredentialsContext context, List<string> userRoles)
        {
            var identity = new ClaimsIdentity(context.Options.AuthenticationType);
            identity.AddClaim(new Claim(ClaimTypes.Name, context.UserName));
            identity.AddClaim(new Claim("sub", context.UserName));
            foreach(var item in userRoles)
            {
                identity.AddClaim(new Claim(ClaimTypes.Role, item));
            }
            return identity;
        }

    }
}