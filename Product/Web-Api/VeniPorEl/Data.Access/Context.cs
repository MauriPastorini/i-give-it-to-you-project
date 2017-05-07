using Data;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Access
{
    public class Context : DbContext
    {
        public DbSet<Product> Products { get; set; }

        public Context() : base("VeniPorEl") { }
     
    }
}

