package com.rdc.mymap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rdc.mymap.model.MessageObject;
import com.rdc.mymap.model.Ticket;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsoyz on 2017/4/11.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHelper";
    private static final int VERSION = 1;
    private static final String BOOK1 = "create table Message(" +
            "date integer primary key," +
            "userid integer," +
            "isread integer," +
            "context String," +
            "ishost interger)";
    private static final String BOOK2 = "create table Friends(" +
            "userid integer primary key," +
            "username String," +
            "gender integer," +
            "address String," +
            "phonenumber String," +
            "signature String," +
            "hasphoto integer," +
            "photo BLOB)";
    private static final String BOOK3 = "create table Ticket(" +
            "busTicketId integer primary key," +
            "purchaseDate integer," +
            "useDate integer," +
            "busName String," +
            "fare interger)";
    private static final String BOOK4 = "create table FootPrint(" +
            "_id integer primary key," +
            "name String)";
    private static final String BOOK5 = "create table Photo(" +
            "_id integer primary key AUTOINCREMENT," +
            "path String)";
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
        db.execSQL(BOOK2);
        db.execSQL(BOOK3);
        db.execSQL(BOOK4);
        db.execSQL(BOOK5);
        Log.d(TAG, "create datebase OK!");
    }

    public List<Ticket> getTicket() {
        List<Ticket> list = new ArrayList<Ticket>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Ticket", null, null, null, null, null, "busTicketId desc");
        if (cursor.moveToFirst()) {
            list = new ArrayList<Ticket>();
            do {
                list.add(new Ticket(cursor.getInt(cursor.getColumnIndex("busTicketId")),
                        cursor.getLong(cursor.getColumnIndex("purchaseDate")),
                        cursor.getLong(cursor.getColumnIndex("useDate")),
                        cursor.getInt(cursor.getColumnIndex("fare")),
                        cursor.getString(cursor.getColumnIndex("busName"))));
            } while (cursor.moveToNext());
            return list;
        }
        return null;
    }

    public boolean saveFootprint(String name) {
        if (name == null || name == "") return false;
        sqLiteDatabase = getWritableDatabase();
        values.clear();
        values.put("name", name);
        sqLiteDatabase.replace("Footprint", null, values);
        return true;
    }

    public List<String> getFootprint() {
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Footprint", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            List<String> list = new ArrayList<String>();
            do {
                list.add(cursor.getString(cursor.getColumnIndex("name")));
            } while (cursor.moveToNext());
            return list;
        } else return null;
    }

    public boolean saveTicket(JSONObject jsonObject) {
        sqLiteDatabase = getWritableDatabase();
        if (jsonObject == null) return false;
        values.clear();
        try {
            values.put("busTicketId", jsonObject.getInt("busTicketId"));
            values.put("purchaseDate", jsonObject.getLong("purchaseDate"));
            if (jsonObject.isNull("useDate")) values.put("useDate", 0);
            else values.put("useDate", jsonObject.getLong("useDate"));
            values.put("busName", jsonObject.getString("busName"));
            values.put("fare", jsonObject.getInt("fare"));
            sqLiteDatabase.replace("Ticket", null, values);
            Log.d(TAG, "saving ticket context;" + jsonObject.getString("busName"));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveMessage(MessageObject messageObject) {
        Log.d(TAG, " saving message" + messageObject.getContext());
        if (messageObject.isnull()) return false;
        else {
            sqLiteDatabase = getWritableDatabase();
            values.clear();
            values.put("userid", messageObject.getUserid());
            values.put("context", messageObject.getContext());
            values.put("date", messageObject.getDate());
            values.put("isread", messageObject.getIsread());
            values.put("ishost", messageObject.getIshost());
            Log.d(TAG, " saving message:" + messageObject.getContext() + " id:" + messageObject.getUserid());
            sqLiteDatabase.replace("Message", null, values);
            return true;
        }
    }

    public List<MessageObject> getMessage(int userid) {
        List<MessageObject> messageList = new ArrayList<MessageObject>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Message", null, "userid = ?", new String[]{userid + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MessageObject messageObject = new MessageObject(cursor.getInt(cursor.getColumnIndex("userid")),
                        cursor.getString(cursor.getColumnIndex("context")),
                        cursor.getLong(cursor.getColumnIndex("date")),
                        cursor.getInt(cursor.getColumnIndex("isread")) == 1,
                        cursor.getInt(cursor.getColumnIndex("ishost")) == 1);
                messageList.add(messageObject);
            } while (cursor.moveToNext());
            return messageList;
        } else {
            return null;
        }
    }

    public List<MessageObject> getUnReadMessage(int userid) {
        List<MessageObject> messageList = new ArrayList<MessageObject>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Message", null, "userid = ? and isread = 0", new String[]{userid + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MessageObject messageObject = new MessageObject(cursor.getInt(cursor.getColumnIndex("userid")),
                        cursor.getString(cursor.getColumnIndex("context")),
                        cursor.getLong(cursor.getColumnIndex("date")),
                        false,
                        cursor.getInt(cursor.getColumnIndex("ishost")) == 1);
                messageList.add(messageObject);
            } while (cursor.moveToNext());
            return messageList;
        } else {
            return null;
        }
    }

    public boolean readAllMessage(int userid) {
        Cursor cursor = sqLiteDatabase.query("Message", null, "userid = ? and isread = 0", new String[]{userid + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                values.clear();
                values.put("userid", cursor.getInt(cursor.getColumnIndex("userid")));
                values.put("context", cursor.getString(cursor.getColumnIndex("context")));
                values.put("date", cursor.getLong(cursor.getColumnIndex("date")));
                values.put("ishost", cursor.getInt(cursor.getColumnIndex("ishost")));
                values.put("isread", 1);
                sqLiteDatabase.replace("Message", null, values);
            } while (cursor.moveToNext());
            return true;
        } else {
            return false;
        }
    }

    public boolean saveMessage(int userid, String context, long date, boolean isread, boolean ishost) {
        sqLiteDatabase = getWritableDatabase();
        if (userid < 0 || date < 0) return false;
        values.clear();
        values.put("userid", userid);
        values.put("context", context);
        values.put("date", date);
        if (isread) values.put("isread", 1);
        else values.put("isread", 0);
        if (ishost) values.put("ishost", 1);
        else values.put("ishost", 0);
        Log.d(TAG, " saving message:" + context + " id:" + userid);
        sqLiteDatabase.replace("Message", null, values);
        return true;
    }

    public List<Integer> getMessageId() {
        List<Integer> list = new ArrayList<Integer>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Message", null, null, null, null, null, "date desc");
        if (cursor.moveToFirst()) {
            do {
                if (list.contains(cursor.getInt(cursor.getColumnIndex("userid")))) continue;
                else {
                    list.add(cursor.getInt(cursor.getColumnIndex("userid")));
                }
            } while (cursor.moveToNext());
            return list;
        } else {
            return null;
        }
    }

    public int getUnReadNumber(int id) {
        int tem = 0;
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor;
        if (id == -1)
            cursor = sqLiteDatabase.query("Message", null, "isread = 0", null, null, null, null);
        else
            cursor = sqLiteDatabase.query("Message", null, "userid = ? and isread = 0", new String[]{id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tem++;
            } while (cursor.moveToNext());
            return tem;
        } else {
            return -1;
        }
    }

    public List<UserObject> getFriendsList() {
        List<UserObject> list = new ArrayList<UserObject>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Friends", null, null, null, null, null, "username ASC");
        if (cursor.moveToFirst()) {
            do {
                UserObject userObject = new UserObject(
                        cursor.getInt(cursor.getColumnIndex("userid")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getInt(cursor.getColumnIndex("gender")),
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getString(cursor.getColumnIndex("phonenumber")),
                        cursor.getString(cursor.getColumnIndex("signature"))
                );
                list.add(userObject);
            } while (cursor.moveToNext());
            return list;
        } else {
            return null;
        }
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

    public boolean saveUserPhoto(int id, Bitmap bitmap) {
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

    public Bitmap getUserPhotoToBitmap(int id) {
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

    public boolean saveCollectionPhoto(File file) {
        Log.d(TAG, "saving Collection Photo " );
        if (file.exists()) {
            sqLiteDatabase = getWritableDatabase();
            values.clear();
            values.put("path",file.getPath());
            sqLiteDatabase.insert("Photo",null,values);
            return true;
        }else{
            return false;
        }
    }

//    public Bitmap getCollectionPhotoToBitmap(int id) {
//        Log.d(TAG, "gettingPhoto id = " + id);
//        sqLiteDatabase = getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.query("Photo", null, "_id = ?", new String[]{id + ""}, null, null, null);
//        if (cursor.moveToFirst()) {
//            Log.d(TAG, "cursor not empty");
//            byte[] pic = cursor.getBlob(cursor.getColumnIndex("data"));
//            return BitmapFactory.decodeByteArray(pic, 0, pic.length);
//        } else {
//            Log.d(TAG, "cursor is empty");
//        }
//        return null;
//    }
    public ArrayList<String> getCollectionPathList(){
        Log.d(TAG, "getting Collection Path List");
        ArrayList<String> list = new ArrayList<String>();
        sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Photo",null,null, null, null, null, "_id desc");
        if (cursor.moveToFirst()) {
            do{
                list.add(cursor.getString(cursor.getColumnIndex("path")));
            }while(cursor.moveToNext());
            Log.d(TAG, "cursor not empty");
            return list;
        } else {
            Log.d(TAG, "cursor is empty");
        }
        return null;
    }

    public byte[] getUserPhotoToByte(int id) {
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
        sqLiteDatabase.execSQL("DELETE FROM Message");
        sqLiteDatabase.execSQL("DELETE FROM Ticket");
        return true;
    }

    public boolean refreshPhoto(int id) {
        return true;
    }
}
