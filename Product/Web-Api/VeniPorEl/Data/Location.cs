using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class Location
    {
        public const int MAX_LATITUDE = 85;
        public const int MIN_LATITUDE = -85;
        public const int MAX_LONGITUDE = 180;
        public const int MIN_LONGITUDE = -180;


        public int Latitude { get; set; }
        public int Longitude { get; set; }

        private Location() { }

        private Location(int latitude, int longitude)
        {
            Latitude = latitude;
            Longitude = longitude;
        }

        public static Location CreateWithLatLon(int latitude, int longitude)
        {
            if(!IsLatitudeCorrect(latitude) || !IsLongitudeCorrect(longitude))
            {
                throw new ArgumentException("Location creation argument error!");
            }
            return new Location(latitude, longitude);
        }

        private static bool IsLatitudeCorrect(int latitude)
        {
            return (latitude >= MIN_LATITUDE) && (latitude <= MAX_LATITUDE);
        }

        private static bool IsLongitudeCorrect(int longitude)
        {
            return (longitude >= MIN_LONGITUDE) && (longitude <= MAX_LONGITUDE);
        }
    }
}
