using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class ProductImage
    {
        public int ProductImageId { get; set; }
        public string ImagePath { get; set; }
        public virtual Product Product { get; set; }
        [NotMapped]
        public byte[] Image { get; set; }

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

        public static string EncodePathForImage(int productId, string imageName)
        {
            string directoryPath = Properties.Resources.ImageFolderPath;
            string filename = $@"{directoryPath}{@"\ProductID_ "}{productId}{"_"}{imageName}.";
            return filename;
        }

        public static string GetNameFromPath(string pathImage)
        {
            int startFileName = pathImage.LastIndexOf(@"\")+1;
            string filename = pathImage.Substring(startFileName);

            int start = filename.LastIndexOf('_')+1;
            filename = filename.Substring(start);
            return filename;
        }
    }
}
