package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RAF_products.db";
    private static final int DATABASE_VERSION = 1;


    private static final String CREATE_TABLE_PRODUCT = "create table product (_id integer primary key autoincrement, productname text not null, productprice real)";

    public ProductDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProductDBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS product");
        onCreate(db);
    }
}
