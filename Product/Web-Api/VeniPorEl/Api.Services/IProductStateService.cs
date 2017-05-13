using Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Api.Services
{
    public interface IProductStateService : IDisposable
    {
        ProductState GetProductStateById(int productStateId);
        IEnumerable<ProductState> GetAllCategories();
    }
}
