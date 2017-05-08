package com.rdc.mymap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MyDataBaseHelper {

    protected MySQLiteOpenHelper mySQLiteOpenHelper;
    protected SQLiteDatabase sqLiteDatabase;
    private int mDbVersion;
    private String mDbName;
    private String[] mDbCreateSql;
    private String[] mDbUpdateSql;

    protected abstract int getDbVersion(Context context);

    protected abstract String getDbName(Context context);

    protected abstract String[] getDbCreateSql(Context context);

    protected abstract String[] getDbUpdateSql(Context context);

    public MyDataBaseHelper(Context context) {
        this.mDbVersion = this.getDbVersion(context);
        this.mDbName = this.getDbName(context);
        this.mDbCreateSql = this.getDbCreateSql(context);
        this.mDbUpdateSql = this.getDbUpdateSql(context);
        this.mySQLiteOpenHelper = new MySQLiteOpenHelper(context, this.mDbName, null, this.mDbVersion);
    }

    protected void open() {
//        new Thread() {
//            @Override
//            public void run() {
//                sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
//                Log.e("error", "sqLiteDatabase open");
//            }
//        }.start();
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Log.e("error", "sqLiteDatabase open");
    }

    protected SQLiteDatabase getSqLiteDatabase() {
        return this.sqLiteDatabase;
    }

    public void close() {
        this.sqLiteDatabase.close();
        this.mySQLiteOpenHelper.close();
    }

    public boolean insert(String tableName, String[] columns, Object[] values) {
        ContentValues contentValues = new ContentValues();
        for(int rows = 0; rows < columns.length; rows ++) {
            putContentValues(contentValues, columns[rows], values[rows]);
        }
        long rowId = this.sqLiteDatabase.insert(tableName, null, contentValues);
        return rowId != -1;
    }

    public boolean insert(String tableName, Map<String, Object> columnValues) {
        ContentValues contentValues = new ContentValues();
        Iterator iterator = columnValues.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            this.putContentValues(contentValues, key, columnValues.get(key));
        }
        long rowId = this.sqLiteDatabase.insert(tableName, null, contentValues);
        return rowId != -1;
    }

    public boolean delete(String tableName, String[] whereColumns, String[] whereParam) {
        String whereStr = this.initWhereSqlFromArray(whereColumns);
        int rowNumber = this.sqLiteDatabase.delete(tableName, whereStr, whereParam);
        return rowNumber > 0;
    }

    public boolean delete(String tableName, Map<String, String> whereParams) {
        Map map = this.initWhereSqlFromMap(whereParams);
        int rowNumber = this.sqLiteDatabase.delete(tableName, map.get("whereSql").toString(), (String[]) map.get("whereSqlParam"));
        return rowNumber > 0;
    }

    public boolean clear(String sql) {
        this.sqLiteDatabase.execSQL(sql);
        return true;
    }

    public boolean update(String tableName, String[] columns, Object[] values, String[] whereColumns, String[] whereArgs) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < columns.length; i++) {
            this.putContentValues(contentValues, columns[i], values[i]);
        }
        String whereClause = this.initWhereSqlFromArray(whereColumns);
        int rowNumber = this.sqLiteDatabase.update(tableName, contentValues, whereClause, whereArgs);
        return rowNumber > 0;
    }

    public boolean update(String tableName, Map<String, Object> columnValues, Map<String, String> whereParam) {
        ContentValues contentValues = new ContentValues();
        Iterator iterator = columnValues.keySet().iterator();
        String columns;
        while (iterator.hasNext()) {
            columns = (String) iterator.next();
            putContentValues(contentValues, columns, columnValues.get(columns));
        }
        Map map = this.initWhereSqlFromMap(whereParam);
        int rowNumber = this.sqLiteDatabase.update(tableName, contentValues, (String) map.get("whereSql"), (String[]) map.get("whereSqlParam"));
        return rowNumber > 0;
    }

    public List<Map> queryListMap(String sql, String[] params) {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = this.sqLiteDatabase.rawQuery(sql, params);
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap item = new HashMap();
            for (int i = 0; i < columnCount; i++) {
                int type = cursor.getType(i);
                switch (type) {
                    case 0 :
                        item.put(cursor.getColumnName(i), null);
                        break;
                    case 1 :
                        item.put(cursor.getColumnName(i), cursor.getInt(i));
                        break;
                    case 2 :
                        item.put(cursor.getColumnName(i), cursor.getFloat(i));
                        break;
                    case 3 :
                        item.put(cursor.getColumnName(i), cursor.getString(i));
                        break;
                }
            }
            arrayList.add(item);
        }
        cursor.close();
        return arrayList;
    }

    public Map queryItemMap(String sql, String[] params) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(sql, params);
        HashMap map = new HashMap();
        if(cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                int type = cursor.getType(i);
                switch (type) {
                    case 0 :
                        map.put(cursor.getColumnName(i), null);
                        break;
                    case 1 :
                        map.put(cursor.getColumnName(i), cursor.getInt(i));
                        break;
                    case 2 :
                        map.put(cursor.getColumnName(i), cursor.getFloat(i));
                        break;
                    case 3 :
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                        break;
                }
            }
        }
        cursor.close();
        return map;
    }

    public void execSQL(String sql) {
        this.sqLiteDatabase.execSQL(sql);
    }

    public void execSQL(String sql, Object[] params) {
        this.sqLiteDatabase.execSQL(sql, params);
    }

    private void putContentValues(ContentValues contentValues, String key, Object value) {
        if(value == null) {
            contentValues.put(key, "");
        } else {
            String className = value.getClass().getName();
            if(className.equals("java.lang.String")) {
                contentValues.put(key, value.toString());
            } else if(className.equals("java.lang.Integer")) {
                contentValues.put(key, Integer.valueOf(value.toString()));
            } else if(className.equals("java.lang.Float")) {
                contentValues.put(key, Float.valueOf(value.toString()));
            } else if(className.equals("java.lang.Double")) {
                contentValues.put(key, Double.valueOf(value.toString()));
            } else if(className.equals("java.lang.Boolean")) {
                contentValues.put(key, Boolean.valueOf(value.toString()));
            } else if(className.equals("java.lang.Long")) {
                contentValues.put(key, Long.valueOf(value.toString()));
            } else if(className.equals("java.lang.Short")) {
                contentValues.put(key, Short.valueOf(value.toString()));
            }
        }

    }

    private String initWhereSqlFromArray(String[] whereColumns) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < whereColumns.length; i++) {
            stringBuffer.append(whereColumns[i]).append(" = ? ");
            if(i < whereColumns.length - 1) {
                stringBuffer.append(" and ");
            }
        }
        return stringBuffer.toString();
    }

    private Map<String ,Object> initWhereSqlFromMap(Map<String, String> whereParams) {
        Set set = whereParams.keySet();
        String[] temp  = new String[whereParams.size()];
        int i = 0;
        Iterator iterator = set.iterator();
        StringBuffer stringBuffer = new StringBuffer();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            stringBuffer.append(key).append(" = ? ");
            temp[i] = whereParams.get(key);
            if(i < set.size() - 1) {
                stringBuffer.append(" and ");
            }
            i ++;
        }
        HashMap result = new HashMap();
        result.put("whereSql", stringBuffer);
        result.put("whereSqlParam", temp);
        return result;
    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String[] strings = MyDataBaseHelper.this.mDbCreateSql;
            for (int i = 0; i < strings.length; i++) {
                String sql = strings[i];
                db.execSQL(sql);
                Log.e("error", " execSQL=" + sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String[] strings = MyDataBaseHelper.this.mDbUpdateSql;
            for (int i = 0; i < strings.length; i++) {
                String sql = strings[i];
                db.execSQL(sql);
            }
        }
    }
}
