using Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace Api.Services
{
    public interface IProductService : IDisposable
    {
        int CreateWithNameCategoryStateLocation(string name, int productCategoryId, int productStateId, int productLatitude, int productLongitude);
        bool AddImageToProduct(int productId, string imageName, byte[] imageByteArray);
        ICollection<ProductImage> GetImagesFromProductId(int productId);
    }
}
