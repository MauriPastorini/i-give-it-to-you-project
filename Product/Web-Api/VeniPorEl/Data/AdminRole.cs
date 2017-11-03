using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class AdminRole : IInterface
    {
        public int RoleId
        {
            get
            {
                return (int)User.Roles.Admin;
            }
        }

        public bool IsAdmin()
        {
            return true;
        }

        public bool IsNormalUser()
        {
            return false;
        }
    }
}
