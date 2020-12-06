package uk.ash.womensfootball;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import uk.ash.womensfootball.event.EventData;


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
