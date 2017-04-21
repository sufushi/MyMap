package com.rdc.mymap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import java.io.ByteArrayOutputStream;

/**
 * Created by wsoyz on 2017/4/11.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHelper";
    private static final int VERSION = 1;
    private static final String BOOK1 = "create table Friends(" +
            "userid integer primary key," +
            "username String," +
            "gender integer," +
            "address String," +
            "phonenumber String," +
            "signature String," +
            "hasphoto integer," +
            "photo BLOB)";
    private SQLiteDatabase sqLiteDatabase;
    private ContentValues values = new ContentValues();

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DataBaseHelper(Context context, String name, int version) {
        this(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOOK1);
        Log.d(TAG, "create datebase OK!");
    }

    public boolean saveUser(UserObject userObject) {
        sqLiteDatabase = getWritableDatabase();
        if (userObject.isEmpty()) return false;
        values.clear();
        values.put("userid", userObject.getUserId());
        values.put("username", userObject.getUsername());
        values.put("gender", userObject.getGender());
        values.put("address", userObject.getaddress());
        values.put("phonenumber", userObject.getPhoneNumber());
        values.put("signature", userObject.getSignature());
        Bitmap bm = HttpUtil.getPhpop(userObject.getUserId());
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            values.put("photo", baos.toByteArray());
            values.put("hasphoto", 1);
            Log.d(TAG, " save! name:" + userObject.getUsername() + " id:" + userObject.getUserId() + " and photo done!");
        } else {
            values.put("hasphoto", 0);
            Log.d(TAG, " save:" + userObject.getUsername() + " id:" + userObject.getUserId() + " and photo undone!");
        }


        sqLiteDatabase.replace("Friends", null, values);
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        System.out.println("updataBase");
    }

    public boolean savePhoto(int id, Bitmap bitmap) {
        if (bitmap == null || id < 0) return false;
        Log.d(TAG, "saving Photo id = " + id);
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Friends", null, "userid = ?", new String[]{id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "cursor not empty");
            values.clear();
            values.put("userid", cursor.getInt(cursor.getColumnIndex("userid")));
            values.put("username", cursor.getString(cursor.getColumnIndex("username")));
            values.put("gender", cursor.getInt(cursor.getColumnIndex("gender")));
            values.put("address", cursor.getString(cursor.getColumnIndex("address")));
            values.put("phonenumber", cursor.getString(cursor.getColumnIndex("phonenumber")));
            values.put("signature", cursor.getString(cursor.getColumnIndex("signature")));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            values.put("photo", baos.toByteArray());
            values.put("hasphoto", 1);
            sqLiteDatabase.replace("Friends", null, values);
        } else {
            Log.d(TAG, "cursor is empty");
        }
        return false;
    }

    public Bitmap getPhotoToBitmap(int id) {
        Log.d(TAG, "gettingPhoto id = " + id);
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Friends", null, "userid = ?", new String[]{id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "cursor not empty");
            if (cursor.getInt(cursor.getColumnIndex("hasphoto")) == 1) {
                byte[] pic = cursor.getBlob(cursor.getColumnIndex("photo"));
                return BitmapFactory.decodeByteArray(pic, 0, pic.length);
            }
        } else {
            Log.d(TAG, "cursor is empty");
        }
        return null;
    }

    public byte[] getPhotoToByte(int id) {
        Log.d(TAG, "gettingPhoto id = " + id);
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Friends", null, "userid = ?", new String[]{id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "cursor not empty");
            if (cursor.getInt(cursor.getColumnIndex("hasphoto")) == 1) {
                byte[] pic = cursor.getBlob(cursor.getColumnIndex("photo"));
                return pic;
            }
        } else {
            Log.d(TAG, "cursor is empty");
        }
        return null;
    }

    public UserObject getUserObjict(int id) {
        Log.d(TAG, "gettingUserObjict id = " + id);
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Friends", null, "userid = ?", new String[]{id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            UserObject userObject = new UserObject(
                    cursor.getInt(cursor.getColumnIndex("userid")),
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getInt(cursor.getColumnIndex("gender")),
                    cursor.getString(cursor.getColumnIndex("address")),
                    cursor.getString(cursor.getColumnIndex("phonenumber")),
                    cursor.getString(cursor.getColumnIndex("signature"))
            );
            return userObject;
//            int userId, String username, int gender, String address, String phoneNumber, String signature, int money
        } else {
            Log.d(TAG, "cursor is empty");
        }
        return null;
    }

    public boolean clear() {
        sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM Friends");
        return true;
    }

    public boolean refreshPhoto(int id) {
        return true;
    }
}
