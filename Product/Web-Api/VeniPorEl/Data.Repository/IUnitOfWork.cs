using Data.Repository.GenericRepository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repository
{
    public interface IUnitOfWork : IDisposable
    {
        IRepository<Product> ProductRepository { get; }
        IRepository<ProductState> ProductStateRepository { get; }
        IRepository<Category> CategoryRepository { get; }
        IRepository<ProductImage> ProductImagesRepository { get; }
        IRepository<User> UsersRepository { get; }

        void Save();
    }
}
