import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static jdk.nashorn.internal.objects.Global.println;

public class DateTest {

    private static String converToGmtDate(String unixTimeStamp)
    {
        //unix timestamps have GMT time zone.
//        DateFormat gmtFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //date obtained here is in IST on my system which needs to be converted into GMT.
        Date time = new Date(Long.valueOf(unixTimeStamp) * 1000);

        String result = gmtFormat.format(time);

        return result;
    }
    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
//        Date date = new Date(timestamp.getTime());
        Date date = new Date((long)1496980793 * 1000);

        // S is the millisecond
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss:S");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");

        System.out.println(simpleDateFormat.format(timestamp));
        System.out.println(simpleDateFormat.format(date));

//        System.out.println( converToGmtDate("1497053132"));
        System.out.println( converToGmtDate("1496980793"));
    }
}