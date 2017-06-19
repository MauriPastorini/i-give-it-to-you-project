﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class Product
    {

        public int ProductId { get; set; }
        public string Name { get; set; }
        public virtual Category Category { get; set; }
        public int CategoryId { get; set; }
        public ProductState State { get; set; }
        public Location Location { get; set; }
        public List<ProductImage> ProductImages { get; set; }
        public bool Moderated { get; set; }
        [ForeignKey("UserOwnProductId")]
        public virtual User UserOwnProduct { get; set; }
        public int UserOwnProductId { get; set; }
        [ForeignKey("UserSolicitudeProductId")]
        public virtual User UserSolicitudeProduct { get; set; }
        public int? UserSolicitudeProductId { get; set; }


        private Product()
        {
            ProductImages = new List<ProductImage>();
            Moderated = false;
        }

        private Product(string name, Category category, ProductState state, Location location)
        {
            Name = name;
            Category = category;
            State = state;
            Location = location;
            ProductImages = new List<ProductImage>();
            Moderated = false;
        }

        public static Product CreateWithNameCategoryStateLocation(string name, Category category, ProductState state, Location location)
        {
            if (!IsNameCorrect(name) || !IsCategoryCorrect(category) || !IsProductStateCorrect(state) || !IsLocationCorrect(location))
            {
                throw new ArgumentException("Product creation argument error!");
            }
            return new Product(name, category, state, location);
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

        public override string ToString() {
            return "Name: " + this.Name + ". " + this.Category;
        }

    }
}
