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
        ICollection<Product> GetUnmoderatedProducts();
        int AcceptProduct(int productId);
        void DeleteProduct(int productId);
        ICollection<ProductImage> GetImagesFromProductId(int productId);
        Product GetProduct(int productId);
        ICollection<Product> GetProductsByCategory(int categoryId);
        void CreateSolicitudeForProduct(int productId, int accountId);
        void DeleteSolicitudeForProduct(int productId, int accountId, string userNameConnected);
        ICollection<Product> GetProductsSolicitatedByUser(int userId);
    }
}
