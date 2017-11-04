using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class ProductState
    {
        public int ProductStateId { get; set; }
        public string Name { get; set; }
     


        private ProductState() { }

        private ProductState(string name)
        {
            Name = name;
        }

        public static ProductState CreateWithName(string name)
        {
            if(!IsNameCorrect(name))
            {
                throw new ArgumentException("ProductState creation argument error!");
            }
            return new ProductState(name);
        }

        private static bool IsNameCorrect(string name)
        {
            return name != null && name.Trim() != "";
        }
    }
}
