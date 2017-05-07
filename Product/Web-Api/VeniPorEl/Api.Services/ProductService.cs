using Data;
using Data.Repository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Api.Services
{
    public class ProductService : IProductService
    {

        private readonly IUnitOfWork unitOfWork;

        public ProductService()
        {
            unitOfWork = new UnitOfWork();
        }

        public ProductService(IUnitOfWork unitOfWork)
        {
            this.unitOfWork = unitOfWork;
        }

        public int CreateWithNameCategoryStateLocation(string name, Category category, ProductState productState, int productLatitude,int productLongitude)
        {
            try
            {
                Location productLocation = Location.CreateWithLatLon(productLatitude, productLongitude);
                Product productToCreate = Product.CreateWithNameCategoryStateLocation(name,category,productState,productLocation);
                unitOfWork.ProductRepository.Add(productToCreate);
                unitOfWork.Save();
                return productToCreate.ProductId;
            }
            catch (ArgumentException ex)
            {
                throw ex;
            }
        }

        public void Dispose()
        {
            unitOfWork.Dispose();
        }

    }
}
