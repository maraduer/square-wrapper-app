package gmu.rqr.square_wrapper_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ProductDataSource {

    private SQLiteDatabase database;
    private ProductDBHelper dbHelper;

    public ProductDataSource(Context context){
        dbHelper = new ProductDBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public boolean insertProduct(Product np){
        boolean succeeded = false;
        try{
            ContentValues initialValues = new ContentValues();
            initialValues.put("productname", np.getProductName());
            initialValues.put("productprice", np.getProductPrice());

            succeeded = database.insert("product", null, initialValues) > 0;
        }
        catch(Exception e){
            //Something went horribly wrong
        }
        return succeeded;
    }


    public boolean updateProduct(Product up){
        boolean succeeded = false;
        try{
            Long rowID = (long) up.getProductID();

            ContentValues updateValues = new ContentValues();
            updateValues.put("productname", up.getProductName());
            updateValues.put("productprice", up.getProductPrice());

            succeeded = database.update("product", updateValues, "_id="+rowID, null) > 0;
        }
        catch(Exception e){
            //Something went horribly wrong
        }
        return succeeded;
    }

    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            String query = "SELECT * FROM product ORDER BY productname ASC";
            Cursor cursor = database.rawQuery(query, null);

            Product nextProduct;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                nextProduct = new Product();
                nextProduct.setProductID(cursor.getInt(0));
                nextProduct.setProductName(cursor.getString(1));
                nextProduct.setProductPrice(cursor.getDouble(2));
                products.add(nextProduct);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e){
            products = new ArrayList<Product>();
        }
        return products;
    }

    public Product getSpecificProduct(int productID){
        Product product = new Product();
        String query = "SELECT * FROM product WHERE _id=" + productID;
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
