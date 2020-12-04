package uk.ash.womensfootball;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class Converters {

    @TypeConverter
    public static LocalDateTime ldtFromLong(Long value){
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault());
    }

    @TypeConverter
    public static Long longFromLdt(LocalDateTime ldt){
        return ldt.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    //@TypeConverter
    //public static ArrayList<String> leagueFromString(String value) {

        //Type listType = new TypeToken<ArrayList<String>>() {}.getType();
       // return new Gson().fromJson(value, listType);
    //}

    //@TypeConverter
   // public static String stringFromLeague(ArrayList<LeagueData> list) {
   //     Gson gson = new Gson();
   //     String json = gson.toJson(list);
   //     return json;
   // }

    //public static String drawableToString(Drawable value){return value == null ? null : String.valueOf(value);} //team badge

    //public static Drawable stringToDrawable(String value){return value == null ? null : Drawable.;} //team badge
    //public static Long toTimestamp(Date date){
    //     return date == null ? null : new date.getTime();
    // }
}
