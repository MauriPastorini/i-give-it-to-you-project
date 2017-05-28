using Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Api.Services
{
    public interface IUserService
    {

        void Register(User user);
        User GetByUserName(string userName);
        void Update(User user);

    }
}
