using Data.Access;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repository.GenericRepository
{
    public class Repository<T> : IRepository<T> where T : class
    {
        internal Context ContextRepo;
        internal DbSet<T> dbSet;

        public Repository(Context context)
        {
            ContextRepo = context;
            dbSet = context.Set<T>();
        }

        public T Get(int id)
        {
            return dbSet.Find(id);
        }

        public IEnumerable<T> GetAll()
        {
            return dbSet.ToList();
        }

        public IEnumerable<T> Find(Expression<Func<T, bool>> predicate)
        {
            return dbSet.Where(predicate).ToList();
        }

        public T SingleOrDefault(Expression<Func<T, bool>> predicate)
        {
            return dbSet.SingleOrDefault(predicate);
        }

        public void Add(T entity)
        {
            dbSet.Add(entity);
        }

        public void AddRange(IEnumerable<T> entities)
        {
            dbSet.AddRange(entities);
        }

        public void Remove(T entity)
        {
            dbSet.Remove(entity);
        }

        public void RemoveRange(IEnumerable<T> entities)
        {
            dbSet.RemoveRange(entities);
        }

        public void Update(T entity)
        {
            dbSet.Attach(entity);
            ContextRepo.Entry(entity).State = EntityState.Modified;
        }

        public void Atach(T entity)
        {
            dbSet.Attach(entity);
        }
    }
}
