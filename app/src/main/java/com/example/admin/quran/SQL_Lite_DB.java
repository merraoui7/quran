package com.example.admin.quran;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 6/11/2018.
 */



public class SQL_Lite_DB extends SQLiteOpenHelper {

    private static final String database_Name ="MyDataBase";
    private static final String table_Name ="Favorite";
    private static final int database_Version =1;
    private static final String UID="id";
    private static final String surahName ="names";
    private static final String fileName ="file";
    private static final String urle="url";
    private static final String stand="stand";
    private static final String Drop_Table ="DROP TABLE IF EXISTS"+table_Name;
    private static final String Create_Table = "CREATE TABLE "+table_Name+" ( "+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "
            +surahName+" VARCHAR(255) , "+fileName+" VARCHAR(255) , "+urle+" VARCHAR(255) , "+stand+" VARCHAR(255) "+ ") ;";
    private Context context;
    private List<Surah> datalist = new ArrayList<>();

    public SQL_Lite_DB(Context context) {
        super(context, database_Name, null, database_Version);
        this.context= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Drop_Table);
        onCreate(db);
    }

    public long insert_Data(String name,String filename ,String url ,String stande){
        SQLiteDatabase sqldb=getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(surahName,name);
        contentValues.put(fileName,filename);
        contentValues.put(urle,url);
        contentValues.put(stand,stande);
        long id=sqldb.insert(table_Name,null,contentValues);
        return id;
    }
    public List<Surah> readDataList(){
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        String columns[]={surahName,fileName,urle,stand};
        Cursor cursor=sqLiteDatabase.query(table_Name,columns,null,null,null,null,null);
        while (cursor.moveToNext()){
            String name =cursor.getString(1);
            String file =cursor.getString(2);
            String url = cursor.getString(3);
            String stand = cursor.getString(4);
            datalist.add(new Surah(stand,name,url,file));
        }
        return datalist;
    }

    public int deleteData(String name){
        SQLiteDatabase liteDatabase=getWritableDatabase();
        String whereArgs[] ={name};
        String s=surahName + " =? ";
        int count =liteDatabase.delete(table_Name,s,whereArgs);
        return count;
    }
    public int get_check_List_Favorite(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from favorite Where Title Like'" + surahName + "'", null);
        int count = rs.getCount();
        return count;
    }
}
