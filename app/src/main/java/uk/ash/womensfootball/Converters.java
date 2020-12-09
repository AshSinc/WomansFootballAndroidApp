package uk.ash.womensfootball;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Converters {

    @TypeConverter
    public static LocalDateTime ldtFromLong(Long value){
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault());
    }

    @TypeConverter
    public static Long longFromLdt(LocalDateTime ldt){
        return ldt.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

}
