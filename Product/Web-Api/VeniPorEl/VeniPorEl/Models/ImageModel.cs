using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace VeniPorEl.Models
{

    public class ImageModel
    {
        [Required]
        [Display(Name = "ImageName")]
        public string ImageName { get; set; }
        [Required]
        [Display(Name = "ImageBase64")]
        public string ImageBase64 { get; set; }
    }
}