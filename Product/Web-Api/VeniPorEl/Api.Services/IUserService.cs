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
        User GetById(int id);
        User GetByUserName(string userName);
        void Update(User user);
        ICollection<User> GetAll();
        void Delete(int id);
        int GetUserIdByUserName(String username);
    }
}
