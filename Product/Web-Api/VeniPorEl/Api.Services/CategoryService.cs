using Data.Repository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Data;

namespace Api.Services
{
    public class CategoryService : ICategoryService
    {

        private readonly IUnitOfWork unitOfWork;

        public CategoryService()
        {
            unitOfWork = new UnitOfWork();
        }

        public void Dispose()
        {
           unitOfWork.Dispose();
        }

        public Category GetCategoryById(int categoryId)
        {
           Category categoryToReturn =  unitOfWork.CategoryRepository.Get(categoryId);
           return categoryToReturn;

        }
    }
}
