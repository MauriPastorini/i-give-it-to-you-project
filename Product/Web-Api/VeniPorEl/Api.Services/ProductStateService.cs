using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Data;
using Data.Repository;

namespace Api.Services
{
    public class ProductStateService : IProductStateService
    {

        private readonly IUnitOfWork unitOfWork;

        public ProductStateService()
        {
            unitOfWork = new UnitOfWork();
        }
        
        public void Dispose()
        {
            unitOfWork.Dispose();
        }

        public ProductState GetProductStateById(int productStateId)
        {
            ProductState productStateToReturn = unitOfWork.ProductStateRepository.Get(productStateId);
            return productStateToReturn;
        }
    }
}
