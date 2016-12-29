package com.mycompany.readmark.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mycompany.readmark.base.BaseApplication;
import com.mycompany.readmark.search.SearchedInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2016/11/22.
 */
public class DatabaseTableSingleton {
    private static final String TAG = "BooksDatabase";
    private static final String DATABASE_NAME = "READMARK";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mDatabase;

    private static DatabaseTableSingleton sDatabaseTableSingleton;
    //这里只能用这种单例，用其他的会产生问题。
    public synchronized static DatabaseTableSingleton getDatabaseTable(Context context){
        if (sDatabaseTableSingleton == null){
            sDatabaseTableSingleton = new DatabaseTableSingleton(context);
        }
        return sDatabaseTableSingleton;
    }

    private DatabaseTableSingleton(Context context){
        DatabaseOpenHelper databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(databaseOpenHelper instanceof DatabaseOpenHelper){
            Log.e("注意", "数据库创建成功");
        }
        mDatabase = databaseOpenHelper.getWritableDatabase();
    }


    //利用静态内部类进行延迟初始化
    /*
    static class DataBaseTableHolder{
        //RetrofitSingleton的静态变量
        private static DatabaseTableSingleton sDatabaseTableSingleton =
                new DatabaseTableSingleton(BaseApplication.sAppContext);
    }

    public static DatabaseTableSingleton getDataBaseTable(){
        return DataBaseTableHolder.sDatabaseTableSingleton;
    }

    private DatabaseTableSingleton(Context context){
        DatabaseOpenHelper databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(databaseOpenHelper instanceof DatabaseOpenHelper){


            Log.e("注意", "数据库创建成功");
        }
        //mDatabase = databaseOpenHelper.getWritableDatabase();
    }
    */
    /*
    private static DatabaseTableSingleton mDatabaseTableSingleton =
            new DatabaseTableSingleton(BaseApplication.sAppContext);

    private DatabaseTableSingleton(Context context){
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
        mDatabase = databaseOpenHelper.getWritableDatabase();
    }

    public static DatabaseTableSingleton getDatabaseTableSingleton(){
        return mDatabaseTableSingleton;
    }
    */

    public void saveSearchedInfo(SearchedInfoBean info){
        ContentValues values = new ContentValues();
        values.put("book_name", info.getKeyWord());
        values.put("book_author", info.getAuthor());
        mDatabase.insert("BookSearch", null, values);
        Log.e("进行了一次存入", info.getKeyWord());
    }

    public List<SearchedInfoBean> loadSearchedInfo(){
        List<SearchedInfoBean> list = new ArrayList<>();
        Cursor cursor = mDatabase.query("BookSearch", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                SearchedInfoBean searchedInfoBean = new SearchedInfoBean();
                searchedInfoBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                searchedInfoBean.setKeyWord(cursor.getString(cursor.getColumnIndex("book_name")));
                searchedInfoBean.setAuthor(cursor.getString(cursor.getColumnIndex("book_author")));
                list.add(searchedInfoBean);
            }while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }




    private static class DatabaseOpenHelper extends SQLiteOpenHelper{

        //表名是BookSearch，有id、book_name、book_author几列
        public static final String CREATE_INFO_IN_SEARCH = "create table BookSearch("
                + "id integer primary key autoincrement,"
                + "book_name text,"
                + "book_author text)";


        DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);

        }

        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_INFO_IN_SEARCH);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS" + CREATE_INFO_IN_SEARCH);
        }
    }
}