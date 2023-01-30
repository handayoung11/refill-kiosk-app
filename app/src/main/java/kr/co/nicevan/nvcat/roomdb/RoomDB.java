package kr.co.nicevan.nvcat.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Payment.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    public abstract PaymentDao paymentDao();

    private static RoomDB INSTANCE;

    public static RoomDB getDBInstance(Context context){

        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, "ROOM_DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
