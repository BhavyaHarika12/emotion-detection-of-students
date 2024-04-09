package com.example.emotiondetection;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import java.util.HashMap;
import java.util.Map;
public class DBConnect extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "emotion.db";

    public DBConnect(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table register(username text primary key,password text,address text,email text,contactno text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS register");
        onCreate(db);
    }

    public String register(String user,String pass,String address,String email,String contact){
        boolean flag = isUserExists(user);
        String error = "none";
        if(!flag) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", user);
            contentValues.put("password", pass);
            contentValues.put("address", address);
            contentValues.put("email", email);
            contentValues.put("contactno", contact);
            db.insert("register", null, contentValues);
            error = "success";
        }else{
            error = "Username already exists";
        }
        return error;
    }

    public boolean login(String user,String pass){
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select username,password from register",null);
        while(res.moveToNext()) {
            String id = res.getString(res.getColumnIndex("username"));
            String password = res.getString(res.getColumnIndex("password"));
            if(id.equals(user) && pass.equals(password)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isUserExists(String user){
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select username from register",null);
        while(res.moveToNext()) {
            String id = res.getString(res.getColumnIndex("username"));
            if(id.equals(user)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
