package uk.ash.womensfootball;

import android.graphics.drawable.Drawable;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Date;


public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value){
        return value == null ? null : new Date(value);
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
