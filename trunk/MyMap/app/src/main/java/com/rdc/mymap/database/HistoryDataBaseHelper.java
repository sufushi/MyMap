package com.rdc.mymap.database;

import android.content.Context;

public class HistoryDataBaseHelper extends MyDataBaseHelper {

    private static HistoryDataBaseHelper mInstance;

    public HistoryDataBaseHelper(Context context) {
        super(context);
    }

    public static HistoryDataBaseHelper getInstance(Context context) {
        if(mInstance == null) {
            synchronized (MyDataBaseHelper.class) {
                if(mInstance == null) {
                    mInstance = new HistoryDataBaseHelper(context);
                    if(mInstance.getSqLiteDatabase() == null || ! mInstance.getSqLiteDatabase().isOpen()) {
                        mInstance.open();
                    }
                }
            }
        }
        return mInstance;
    }

    @Override
    protected int getDbVersion(Context context) {
        return 1;
    }

    @Override
    protected String getDbName(Context context) {
        return "history.db";
    }

    @Override
    protected String[] getDbCreateSql(Context context) {
        String[] strings = new String[1];
        strings[0] = "create table record (id int primary key, name varchar(200))";
        return strings;
    }

    @Override
    protected String[] getDbUpdateSql(Context context) {
        return new String[0];
    }
}
