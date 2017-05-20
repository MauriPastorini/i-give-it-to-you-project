using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class Product
    {

        public int  ProductId { get; set; }
        public string Name { get; set; }
        public Category Category { get; set; }
        public ProductState State { get; set; }
        public Location Location { get; set; }
        public List<ProductImage> ProductImages { get; set; }


        private Product()
        {
            ProductImages = new List<ProductImage>();
        }

        private Product(string name, Category category, ProductState state, Location location)
        {
            Name = name;
            Category = category;
            State = state;
            Location = location;
            ProductImages = new List<ProductImage>();
        }

        public static Product CreateWithNameCategoryStateLocation(string name, Category category, ProductState state, Location location)
        {
            if(!IsNameCorrect(name) || !IsCategoryCorrect(category) || !IsProductStateCorrect(state) || !IsLocationCorrect(location))
            {
                throw new ArgumentException("Product creation argument error!");
            }
            return new Product(name, category,state, location);
        }

        private static bool IsNameCorrect(string name)
        {
            return name != null && name.Trim() != "";
        }

        private static bool IsCategoryCorrect(Category category)
        {
            return category != null;
        }

        private static bool IsProductStateCorrect(ProductState state)
        {
            return state != null;
        }

        private static bool IsLocationCorrect(Location location)
        {
            return location != null;
        }

    }
}
