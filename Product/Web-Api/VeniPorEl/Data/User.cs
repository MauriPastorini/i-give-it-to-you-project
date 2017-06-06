using System;
using System.Collections.Generic;
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

        private User() { }

        private User(string name, string email, string pass, IRole role)
        {
            UserName = name;
            Email = email;
            Pass = pass;
            Role = role;
            RoleId = role.RoleId;
        }

        public static User CreateWithNameEmailPasswordAndRole(string name, string email, string pass, IRole role)
        {
            if(!IsNameCorrect(name))
            {
                throw new ArgumentException("Wrong name format!");
            }
            else if(!IsPasswordCorrect(pass))
            {
                throw new ArgumentException("Wrong password format!");
            }
            else if(!IsEmailCorrect(email))
            {
                throw new ArgumentException("Wrong email format!");
            }
            else
            {
                return new User(name, email, pass, role);
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
            Regex regex = new Regex(@"((?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
            Match match = regex.Match(pass);
            return match.Success;
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
    }
}
