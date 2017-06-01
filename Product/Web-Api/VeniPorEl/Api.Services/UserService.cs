using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Data;
using Data.Repository;

namespace Api.Services
{
    public class UserService : IUserService
    {

        private readonly IUnitOfWork UnitOfWork;

        public UserService()
        {
            UnitOfWork = new UnitOfWork();
        }

        public UserService(IUnitOfWork uow)
        {
            this.UnitOfWork = uow;
        }

        public User GetByUserName(string userName)
        {
            return UnitOfWork.UsersRepository.SingleOrDefault(u => (u.UserName == userName));
        }

        public void Register(User user)
        {
            if(this.GetByUserName(user.UserName) != null)
            {
                throw new ArgumentException("Invalid username!");
            }
            UnitOfWork.UsersRepository.Add(user);
            UnitOfWork.Save();
        }

        public void Update(User user)
        {
            UnitOfWork.UsersRepository.Update(user);
            UnitOfWork.Save();
        }
    }
}
