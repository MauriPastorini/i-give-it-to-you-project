using Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Api.Services
{
    public interface ICategoryService : IDisposable
    {
        Category GetCategoryById(int categoryId);
        IEnumerable<Category> GetAllCategories();
    }
}
