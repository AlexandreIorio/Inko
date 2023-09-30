package ch.heigvd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    public class DateConversion {

        public static Date convertCESTtoGMT(Date cestDate) {

            try {
                // Create a format to parse the date in CEST
                SimpleDateFormat cestFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cestFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris")); // CEST

                // Create a format to format the date in GMT
                SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                // Format the date in GMT
                String gmtDateString = gmtFormat.format(cestDate);

                // Parse the GMT date as a Date object
                return gmtFormat.parse(gmtDateString);
            } catch (ParseException e) {
               System.out.println("Error when parsing date");
               return cestDate;
            }

        }
    }

}
