﻿using Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace Api.Services
{
    public interface IProductService : IDisposable
    {
        int CreateWithNameCategoryStateLocation(string name, Category category, ProductState productState, int productLatitude, int productLongitude);

    }
}
