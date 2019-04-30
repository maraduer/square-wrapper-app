package gmu.rqr.square_wrapper_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_CATEGORY;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_ID;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_NAME;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.COL_PRICE;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.DATE_TIME_STAMP;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.PRICE;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.PROD_ID;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.QUANTITY;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.TABLE_PRODUCT;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.TABLE_TRANSACTION;
import static gmu.rqr.square_wrapper_app.ProductDBHelper.TRANS_ID;

//Class used to manipulate database once it has been created.
// ***This class has insert and update methods to manipulate database. Just need to add the code to use them
public class ProductDataSource{
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



    //Inserts a new product into the database.
    //Input: New Product object (does not have ID already)
    //Output: Bool success
    public boolean insertProduct(Product np){
        boolean succeeded = false;
        try{
            //Uses ContentValues object with key,value pair to load into database
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_NAME, np.getProductName());
            initialValues.put(COL_CATEGORY, np.getProductCategory());
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
            updateValues.put(COL_CATEGORY, up.getProductCategory());
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
                nextProduct.setProductCategory(cursor.getString(2));
                nextProduct.setProductPrice(cursor.getDouble(3));
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
            product.setProductCategory(cursor.getString(2));
            product.setProductPrice(cursor.getDouble(3));
            cursor.close();
        }
        return product;
    }

    public void logTransaction (ArrayList<CheckoutProduct> cart){
        for(CheckoutProduct checkoutItem : cart){
            String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COL_NAME + " = " + "'" + checkoutItem.getProductName() + "'";
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()){
                Integer pid = cursor.getInt(0);
                ContentValues initialValues = new ContentValues();
                initialValues.put(PROD_ID, pid);
                initialValues.put(QUANTITY, checkoutItem.getProductWeight());
                initialValues.put(PRICE, checkoutItem.getProductPrice()*checkoutItem.getProductWeight());
                database.insert(TABLE_TRANSACTION, null, initialValues);
                cursor.close();
            }
            else{
                Log.d("Critical", "Query didn't work");
            }
        }
    }

    public List<PieEntry> getPieChartData(Calendar startDate, Calendar endDate){
        List<PieEntry> chartData = new ArrayList<PieEntry>();
        String startDateStr = DateFormat.format("yyyy-MM-dd 00:00:00", startDate.getTimeInMillis()).toString();
        String endDateStr = DateFormat.format("yyyy-MM-dd 23:59:59", endDate.getTimeInMillis()).toString();
        String[] categories = {"Poultry", "Pork", "Beef", "Other"};
        for(String category : categories) {
            double categoryRevenue = 0.0;
            try {
                String query = "SELECT SUM(" + PRICE + ") FROM " + TABLE_TRANSACTION + " JOIN " + TABLE_PRODUCT + " ON " + COL_ID + " = " + PROD_ID + " WHERE " + COL_CATEGORY + " = " + "'" + category + "' AND " + DATE_TIME_STAMP +" BETWEEN '" + startDateStr + "' AND '" + endDateStr + "'";
                Cursor cursor = database.rawQuery(query, null);
                cursor.moveToFirst();
                categoryRevenue = cursor.getDouble(0);
                cursor.close();
            } catch (Exception e) {
                //Function will return 0.0
            }
            chartData.add(new PieEntry((float)categoryRevenue, category));
        }
        return chartData;
    }

    public List<ILineDataSet> getCategoryLineChartData(Context context, Calendar startDate, Calendar endDate, boolean isProfit){
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        String[] categories = {"Poultry", "Pork", "Beef", "Other"};
        int[] graph_colors = new int[] {R.color.graph_red, R.color.graph_blue, R.color.graph_yellow, R.color.graph_green};
        int colorIndex = 0;
        String query;
        for(String category : categories) {
            List<Entry> chartData = new ArrayList<>();
            int entryIndex = 0;
            //Start from start date and working forwards until end date
            Calendar targetDate = Calendar.getInstance(startDate.getTimeZone());
            targetDate.setTime(startDate.getTime());
            //Following is used to prevent non-updated end date from impacting same day reporting (e.g. 10:42:41 < 10:41:59 causes chart to drop today's data)
            targetDate.add(Calendar.HOUR_OF_DAY, -1);
            while(targetDate.getTime().before(endDate.getTime())){
                String startDateStr = DateFormat.format("yyyy-MM-dd 00:00:00", targetDate.getTimeInMillis()).toString();
                String endDateStr = DateFormat.format("yyyy-MM-dd 23:59:59", targetDate.getTimeInMillis()).toString();
                try {
                    if(isProfit) {
                        query = "SELECT SUM(" + PRICE + ") FROM " + TABLE_TRANSACTION + " JOIN " + TABLE_PRODUCT + " ON " + COL_ID + " = " + PROD_ID + " WHERE " + COL_CATEGORY + " = " + "'" + category + "' AND " + DATE_TIME_STAMP + " BETWEEN '" + startDateStr + "' AND '" + endDateStr + "'";
                    }
                    else{
                        query = "SELECT SUM(" + QUANTITY + ") FROM " + TABLE_TRANSACTION + " JOIN " + TABLE_PRODUCT + " ON " + COL_ID + " = " + PROD_ID + " WHERE " + COL_CATEGORY + " = " + "'" + category + "' AND " + DATE_TIME_STAMP + " BETWEEN '" + startDateStr + "' AND '" + endDateStr + "'";
                    }
                    Cursor cursor = database.rawQuery(query, null);
                    cursor.moveToFirst();
                    chartData.add(new Entry((float) entryIndex, cursor.getFloat(0)));
                } catch (Exception e) {
                    //Error case
                }
                entryIndex++;
                targetDate.add(Calendar.DAY_OF_MONTH,1);
            }
            LineDataSet dataSet = new LineDataSet(chartData, category);
            dataSet.setColor(ContextCompat.getColor(context, graph_colors[colorIndex]));
            dataSets.add(dataSet);
            colorIndex++;
        }
        return dataSets;
    }



    public List<String> getDates(Calendar startDate, Calendar endDate){
        List<String> sqlDates = new ArrayList<>();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            Calendar cal = Calendar.getInstance(startDate.getTimeZone());
            cal.setTime(startDate.getTime());
            //Following is used to prevent non-updated end date from impacting same day reporting (e.g. 10:42:41 < 10:41:59 causes chart to drop today's data)
            cal.add(Calendar.HOUR_OF_DAY, -1);
            while(cal.getTime().before(endDate.getTime())){
                Date date = cal.getTime();
                sqlDates.add(dateFormat.format(date));
                cal.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            //Error case
        }

        return sqlDates;
    }

    public boolean transactionBackup(){
        boolean returnValue = false;
        if(!Environment.getExternalStorageState().equals((Environment.MEDIA_MOUNTED))){
            Log.d("Error", "Couldn't perform backup due to no SD card mounted");
        }
        else {
            String dir = "";
            dir = Environment.getExternalStorageDirectory() + File.separator + "Transaction_backup";
            File path = new File(dir);
            if(!path.exists()){
                path.mkdirs();
            }
            String csvHeader;
            String csvRow;
            Date todaysDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy_kk-mm-ss");
            String formatDate = formatter.format(todaysDate);
            String filename = "Trans_Log_" + formatDate + ".csv";
            File outfile = new File(dir, filename);
            csvHeader = "Transaction ID,Product Name, Product Category,Unit Price,Quantity,Item Total, Date Time Stamp\n";
            try {
                FileWriter fileWriter = new FileWriter(outfile);
                BufferedWriter out = new BufferedWriter(fileWriter);
                String query = "SELECT " + TRANS_ID + "," + COL_NAME + "," + COL_CATEGORY + "," + COL_PRICE + "," + QUANTITY + "," + PRICE + "," + DATE_TIME_STAMP + " FROM " + TABLE_TRANSACTION + " LEFT OUTER JOIN " + TABLE_PRODUCT + " ON " + COL_ID + " = " + PROD_ID;
                Cursor cursor = database.rawQuery(query, null);
                if (cursor != null) {
                    out.write(csvHeader);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        csvRow = Integer.toString(cursor.getInt(0)) + ",";
                        csvRow += cursor.getString(1) + ",";
                        csvRow += cursor.getString(2) + ",";
                        csvRow += Double.toString(cursor.getDouble(3)) + ",";
                        csvRow += Double.toString(cursor.getDouble(4)) + ",";
                        csvRow += Double.toString(cursor.getDouble(5)) + ",";
                        csvRow += cursor.getString(6) + "\n";
                        out.write(csvRow);
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                out.close();
                returnValue = true;
            } catch (IOException e) {
                returnValue = false;
                Log.d("Critical", e.getMessage());
                e.printStackTrace();
            }
        }
        return returnValue;
    }




}
