﻿using Data;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data.Entity;
using System.Data.Entity.Infrastructure.Annotations;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace Data.Access
{
    public class Context : DbContext
    {
        public DbSet<Product> Products { get; set; }
        public DbSet<ProductState> ProductStates { get; set; }
        public DbSet<Category> Categories { get; set; }
        public DbSet<ProductImage> ProductImages { get; set; }
        public DbSet<User> Users { get; set; }

        public Context() : base("VeniPorEl") { addDefault(); }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            //Database.SetInitializer<Context>(null);
            modelBuilder.Entity<Product>()
                   .HasRequired(m => m.UserOwnProduct)
                   .WithMany(t => t.ProductsOwner)
                   .HasForeignKey(m => m.UserOwnProductId)
                   .WillCascadeOnDelete(false);
            modelBuilder.Entity<Product>()
                   .HasOptional(m => m.UserSolicitudeProduct)
                   .WithMany(t => t.ProductsSolicitude)
                   .HasForeignKey(m => m.UserSolicitudeProductId)
                   .WillCascadeOnDelete(false);
            modelBuilder.Entity<Product>()
                 .HasRequired(m => m.State)
                 .WithMany(t => t.Products)
                 .HasForeignKey(m => m.StateId)
                 .WillCascadeOnDelete(false);
           
            base.OnModelCreating(modelBuilder);
        }

        private void addDefault()
        {
            if (Categories.Find(1) == null)
            {
                CreateDefaultCategories();
            }
            if (ProductStates.Find(1) == null)
            {
                CreateDefaultProductStates();
            }
            this.SaveChanges();
        }


        private void CreateDefaultProductStates()
        {
            ProductState defaultProductState = ProductState.CreateWithName("Nuevo");
            ProductStates.Add(defaultProductState);
            ProductState defaultProductState2 = ProductState.CreateWithName("Usado");
            ProductStates.Add(defaultProductState2);
        }

        private void CreateDefaultCategories()
        {
            try
            {
                var path = Properties.Resources.CategoryTextDefault;
                string[] lines = System.IO.File.ReadAllLines(path);
                foreach (string line in lines)
                {
                    Category defaultCategory = Category.CreateWithName(line);
                    Categories.Add(defaultCategory);
                }
            }
            catch (DirectoryNotFoundException)
            {
                Category defaultCategory = Category.CreateWithName("Tecnologia");
                Categories.Add(defaultCategory);
            }
        }
    }
}

