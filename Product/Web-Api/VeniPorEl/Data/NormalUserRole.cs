using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class NormalUserRole : IRole
    {
        public int RoleId
        {
            get
            {
                return (int)User.Roles.NormalUser;
            }
        }

        public bool IsAdmin()
        {
            return false;
        }

        public bool IsNormalUser()
        {
            return true;
        }
    }
}
