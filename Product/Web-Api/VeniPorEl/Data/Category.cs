using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class Category
    {
        public int CategoryId { get; set; }
        public string Name { get; set; }

        private Category() { }

        private Category(string name)
        {
            Name = name;
        }

        public static Category CreateWithName(string name)
        {
            if(!IsNameCorrect(name))
            {
                throw new ArgumentException("Category creation argument error!");
            }
            return new Category(name);
        }

        private static bool IsNameCorrect(string name)
        {
            return name != null && name.Trim() != "";
        }
    }
}
