using System;
using System.Runtime.Serialization;

namespace Api.Services
{
    [Serializable]
    public class ProductAlreadySolicitatedException : Exception
    {
        public ProductAlreadySolicitatedException()
        {
        }

        public ProductAlreadySolicitatedException(string message) : base(message)
        {
        }

        public ProductAlreadySolicitatedException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected ProductAlreadySolicitatedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}