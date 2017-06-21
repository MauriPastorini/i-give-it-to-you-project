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
        public double Latitude { get; set; }
        [Required]
        [Display(Name = "LocationLongitude")]
        public double Longitude { get; set; }
        [Required]
        [Display(Name = "UserId")]
        public int UserId { get; set; }
        [Display(Name = "ProductId")]
        public int ProductId { get; set; }
        [Display(Name = "CategoryName")]
        public string CategoryName { get; set; }
        [Display(Name = "StateId")]
        public int StateId { get; set; }
        [Display(Name = "StateName")]
        public string StateName { get; set; }
    }
}