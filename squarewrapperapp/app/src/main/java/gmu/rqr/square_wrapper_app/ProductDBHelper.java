package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Used to create DB and perform basic tasks. Inherits SQLiteOpenHelper
public class ProductDBHelper extends SQLiteOpenHelper {
    //Constants defined for database name and version (version # arbitrary)
    private static final String DATABASE_NAME = "RAF_products.db";
    private static final int DATABASE_VERSION = 1;
    //Constant for creating table "product". Has 3 fields: auto ID, name, price
    private static final String CREATE_TABLE_PRODUCT = "create table product (_id integer primary key autoincrement, productname text not null, productprice real)";



    //Create database
    public ProductDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    //Create table
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_PRODUCT);
    }



    //To force app to use an update DB, increase the version number. If new number is higher than old it will drop table and create updated version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProductDBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS product");
        onCreate(db);
    }
}
