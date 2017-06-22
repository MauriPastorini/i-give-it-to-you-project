using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Data
{
    public class User
    {
        public int UserId { get; set; }
        public enum Roles : int
        {
            Admin = 1,
            NormalUser = 2
        }
        [Column(TypeName = "VARCHAR")]
        [StringLength(200)]
        [Index]
        public string UserName { get; set; }
        public string Email { get; set; }
        public int RoleId { get; set; }
        private string PassAux { get; set; }
        public string Pass
        {
            get
            {
                return PassAux;
            }
            set
            {
                if(IsPasswordCorrect(value))
                    PassAux = value;
                else
                    throw new ArgumentException("Wrong password format!");
            }
        }
        private IRole RoleAux;
        public IRole Role
        {
            get { return RoleAux; }
            set
            {
                RoleId = value.RoleId;
                RoleAux = value;
            }
        }

        public string Country { get; set; }

        public virtual ICollection<Product> ProductsOwner { get; set; }
        public virtual ICollection<Product> ProductsSolicitude { get; set; }


        private User() { }

        private User(string name, string email, string pass, IRole role, string country)
        {
            UserName = name;
            Email = email;
            Pass = pass;
            Role = role;
            RoleId = role.RoleId;
            Country = country;
        }

        public static User CreateWithNameEmailPasswordAndRole(string name, string email, string pass, IRole role, string country)
        {
            if(!IsNameCorrect(name))
            {
                throw new ArgumentException("Error en formato de nombre");
            }
            else if(!IsPasswordCorrect(pass))
            {
                throw new ArgumentException("Error en formato de contraseña, debe tener algun caracter y mayúscula");
            }
            else if(!IsEmailCorrect(email))
            {
                throw new ArgumentException("Error en formato de email");
            }
            else if(!IsCountryCorrect(country))
            {
                throw new ArgumentException("Wrong country!");
            }
            else
            {
                return new User(name, email, pass, role, country);
            }
        }

        public void LoadRole(int roleId)
        {
            if(roleId == (int)Roles.Admin)
                this.Role = new AdminRole();
            else if(roleId == (int)Roles.NormalUser)
                this.Role = new NormalUserRole();
        }

        private static bool IsEmailCorrect(string email)
        {
            try
            {
                MailAddress m = new MailAddress(email);
                return true;
            }
            catch(FormatException)
            {
                return false;
            }
        }

        private static bool IsPasswordCorrect(string pass)
        {
            return true;//delete after debug
            Regex regex = new Regex(@"((?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
            Match match = regex.Match(pass);
            return match.Success;
        }

        private static bool IsCountryCorrect(string country)
        {
            return (country == "Uruguay") || (country == "Argentina") || (country == "Brasil");
        }

        private static bool IsNameCorrect(string name)
        {
            return !String.IsNullOrEmpty(name);
        }

        public override bool Equals(object obj)
        {
            if(obj is User)
            {
                User parmUs = (User)obj;
                return this.UserName.Equals(parmUs.UserName);
            }
            return false;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override string ToString()
        {
            return "Nombre de usuario: " + this.UserName + ". Email: " + this.Email + ". ";
        }
    }
}
