package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Used to create DB and perform basic tasks. Inherits SQLiteOpenHelper
public class ProductDBHelper extends SQLiteOpenHelper {
    //Constants defined for database name and version (version # arbitrary)
    private static final String DATABASE_NAME = "RAF_products.db";
    private static final int DATABASE_VERSION = 7;

    protected static final String TABLE_PRODUCT = "product";
    protected static final String COL_ID = "_id";
    protected static final String COL_NAME = "productname";
    protected static final String COL_CATEGORY = "productcategory";
    protected static final String COL_PRICE = "productprice";
    //Constant for creating table "product". Has 4 fields: auto ID, name, category, price
    private static final String CREATE_TABLE_PRODUCT = "create table " + TABLE_PRODUCT + "(_id integer primary key autoincrement, productname text not null, productcategory text, productprice real)";


    //constants defined for new table to hold transaction data
    protected static final String TABLE_TRANSACTION = "transaction_table";
    protected static final String TRANS_ID = "_tid";
    protected static final String PROD_ID = "product_id";
    protected static final String QUANTITY = "quantity";
    protected static final String PRICE = "price";
    protected static final String DATE_TIME_STAMP = "time_stamp";
    // constant for creating table "table_transaction". Has 4 fields including the foreign key
    private static final String CREATE_TABLE_TRANSACTION = "create table " + TABLE_TRANSACTION + " (" + TRANS_ID + " integer primary key autoincrement, " + PROD_ID + " integer, " + QUANTITY + " real, " + PRICE + " real, "+ DATE_TIME_STAMP + " datetime default CURRENT_TIMESTAMP, FOREIGN KEY (product_id) REFERENCES product(_id))";


    //Create database
    public ProductDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    //Create table
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_TRANSACTION);
    }




    //To force app to use an update DB, increase the version number. If new number is higher than old it will drop table and create updated version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProductDBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS transaction_table");
        onCreate(db);
    }
}