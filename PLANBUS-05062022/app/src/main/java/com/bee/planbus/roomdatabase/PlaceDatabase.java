package com.bee.planbus.roomdatabase;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bee.planbus.model.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class PlaceDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}


