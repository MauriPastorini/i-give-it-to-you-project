using Data;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;


namespace VeniPorEl.Models
{
    public class ProductModel
    {
        [Required]
        [Display(Name = "ProductName")]
        public string Name { get; set; }

        [Required]
        [Display(Name = "CategoryId")]
        public int CategoryId { get; set; }

        [Required]
        [Display(Name = "ProductStateId")]
        public int State { get; set; }

        [Required]
        [Display(Name = "LocationLatitude")]
        public int Latitude { get; set; }

        [Required]
        [Display(Name = "LocationLongitude")]
        public int Longitude { get; set; }

        [Required]
        [Display(Name = "UserId")]
        public int UserId { get; set; }
    }
}