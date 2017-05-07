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
        
        public UnitOfWork()
        {
            Context = new Context();            
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
