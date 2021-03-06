package gmu.rqr.square_wrapper_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_ID;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_NAME;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_PRICE;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.TABLE_PRODUCT;

//Class used to manipulate database once it has been created.
// ***This class has insert and update methods to manipulate database. Just need to add the code to use them
public class ProductDataSource {
    private SQLiteDatabase database;
    private ProductDBHelper dbHelper;



    //Initialize DataSource. Creating DBHelper object creates DB
    public ProductDataSource(Context context){
        dbHelper = new ProductDBHelper(context);
    }



    //Pretty much just uses functions from SQLiteOpenHelper for open, close
    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }



    public void close(){
        dbHelper.close();
    }



    //Inserts a new product into the database. Not currently used
    //Input: New Product object (does not have ID already)
    //Output: Bool success
    public boolean insertProduct(Product np){
        boolean succeeded = false;
        try{
            //Uses ContentValues object with key,value pair to load into database
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_NAME, np.getProductName());
            initialValues.put(COL_PRICE, np.getProductPrice());
            succeeded = database.insert(TABLE_PRODUCT, null, initialValues) > 0;
        }
        catch(Exception e){
            //Something went horribly wrong
            succeeded = false;
        }
        return succeeded;
    }



    //Updates an existing product entry in database. Not currently used
    //Input: Existing Product (already has ID)
    //Output: Bool success
    public boolean updateProduct(Product up){
        boolean succeeded = false;
        try{
            Long rowID = (long) up.getProductID();
            //Uses ContentValues object with key,value pair to update database entry at rowID
            ContentValues updateValues = new ContentValues();
            updateValues.put(COL_NAME, up.getProductName());
            updateValues.put(COL_PRICE, up.getProductPrice());
            succeeded = database.update(TABLE_PRODUCT, updateValues, COL_ID+"="+rowID, null) > 0;
        }
        catch(Exception e){
            //Something went horribly wrong
            succeeded = false;
        }
        return succeeded;
    }

    public boolean deleteProduct(Product product){

        boolean succeeded = false;
        try{
            Long rowID = (long) product.getProductID();
            succeeded = database.delete(TABLE_PRODUCT, COL_ID+"= ?", new String[]{rowID.toString()}) > 0;
        }
        catch(Exception e){
            //Something went horribly wrong
            succeeded = false;
        }
        return succeeded;
    }

    //Gets all products from database
    //Input: None
    //Output: ArrayList<Product> all products in DB
    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            String query = "SELECT * FROM " + TABLE_PRODUCT + " ORDER BY " + COL_NAME + " ASC";
            //Cursor object is used to move through database
            Cursor cursor = database.rawQuery(query, null);
            Product nextProduct;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                nextProduct = new Product();
                nextProduct.setProductID(cursor.getInt(0));
                nextProduct.setProductName(cursor.getString(1));
                nextProduct.setProductPrice(cursor.getDouble(2));
                //Add Product to list
                products.add(nextProduct);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e){
            //Returns empty list
            products = new ArrayList<Product>();
        }
        return products;
    }



    //Does same as above except only for one specific product based on productID. Not currently used
    //Input: int product ID (must have already been stored in DB to have ID)
    //Output: Product object
    public Product getSpecificProduct(int productID){
        Product product = new Product();
        String query = "SELECT * FROM" + TABLE_PRODUCT + "WHERE" + COL_ID + "=" + productID;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            product.setProductID(cursor.getInt(0));
            product.setProductName(cursor.getString(1));
            product.setProductPrice(cursor.getDouble(2));
            cursor.close();
        }
        return product;
    }


}
