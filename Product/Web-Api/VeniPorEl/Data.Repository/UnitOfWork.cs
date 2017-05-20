using Data.Access;
using Data.Repository.GenericRepository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repository
{
    public class UnitOfWork : IUnitOfWork
    {
        private readonly Context Context;

        private Repository<Product> productRepository;
        private Repository<Category> categoryRepository;
        private Repository<ProductState> productStateRepository;
        private Repository<ProductImage> productImagesRepository;

        public UnitOfWork()
        {
            Context = new Context();            
        }

        public UnitOfWork(Context context)
        {
            Context = context;
        }

        public IRepository<Product> ProductRepository
        {
            get
            {
                if (productRepository == null)
                {
                    productRepository = new Repository<Product>(Context);
                }
                return productRepository;
            }
        }

        public IRepository<Category> CategoryRepository
        {
            get
            {
                if (categoryRepository == null)
                {
                    categoryRepository = new Repository<Category>(Context);
                }
                return categoryRepository;
            }
        }

        public IRepository<ProductState> ProductStateRepository
        {
            get
            {
                if (productStateRepository == null)
                {
                    productStateRepository = new Repository<ProductState>(Context);
                }
                return productStateRepository;
            }
        }

        public IRepository<ProductImage> ProductImagesRepository
        {
            get
            {
                if (productImagesRepository == null)
                {
                    productImagesRepository = new Repository<ProductImage>(Context);
                }
                return productImagesRepository;
            }
        }

        public void Save()
        {
            Context.SaveChanges();
        }
        private bool disposed = false;

        protected virtual void Dispose(bool disposing)
        {
            if (!disposed)
            {
                if (disposing)
                {
                    Context.Dispose();
                }
            }
            this.disposed = true;
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }
    }
}
