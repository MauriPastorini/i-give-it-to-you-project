using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    abstract class RepositorioAbstracto
    {
        public abstract void test();
    }

    class RepoParticular : RepositorioAbstracto, IInterface
    {
        public int RoleId
        {
            get
            {
                throw new NotImplementedException();
            }
        }

        public bool IsAdmin()
        {
            throw new NotImplementedException();
        }

        public bool IsNormalUser()
        {
            throw new NotImplementedException();
        }

        public override void test()
        {
            throw new NotImplementedException();
        }
    }
}
