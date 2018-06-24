package com.andyshon.thumbsandtasks.Database;

import android.content.Context;

import io.realm.Realm;

/**
 * Created by andyshon on 12.05.18.
 */

public class RealmConfiguration {

    public static void init(Context context) {
        Realm.init(context);
        io.realm.RealmConfiguration realmConfig = new io.realm.RealmConfiguration.Builder()
                .name("items.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
