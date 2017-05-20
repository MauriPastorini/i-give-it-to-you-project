using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class ProductImage
    {
        public int ProductImageId { get; set; }
        public string ImagePath { get; set; }

        private ProductImage()
        { }

        private ProductImage(string imagePath)
        {
            this.ImagePath = imagePath;
        }

        public static ProductImage CreateWithPath(string imagePath)
        {
            if (!IsPathCorrect(imagePath))
            {
                throw new ArgumentException("Image creation argument error!");
            }
            return new ProductImage(imagePath);
        }

        private static bool IsPathCorrect(string imagePath)
        {
            return imagePath != null && imagePath.Trim() != "";
        }
    }
}
