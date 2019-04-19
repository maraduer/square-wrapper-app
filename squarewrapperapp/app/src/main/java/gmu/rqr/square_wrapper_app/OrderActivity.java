package gmu.rqr.square_wrapper_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

//This is the Main activity. Implements BuyDialog method so that data from dialog can be saved
public class OrderActivity  extends AppCompatActivity implements BuyDialog.SaveQuantityListener, AddNewProduct.SaveProductListener {

    //List of all products for sale
    ArrayList<Product> products;
    //List of products that are in the cart
    ArrayList<CheckoutProduct> checkoutProducts = new ArrayList<>();
    ProductAdapter adapter;
    //Product that is selected from ListView
    private CheckoutProduct currentProduct;
    private ProductDataSource ds;



    //Runs when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Layout for activity is defined by order_layout.xml
        setContentView(R.layout.order_layout);
        //Initialize buttons/list items so that they are clickable
        initListItemLongClick();
        initListItemClick();
        initAddNewProductButton();
        initCartButton();
        initChartButton();


        //Initialize DataSource used to access DB
        ds = new ProductDataSource(this);
        //Insert temporary products into database. This will happen every time you test app, so comment out when you have enough products
        try{
            //ds.open();
            //Product toDelete = ds.getProducts().get(0);
            //ds.deleteProduct(toDelete);
            ds.close();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadItems() {
        try{
            ds.open();
            //Returns ArrayList<Product> from database
            products = ds.getProducts();
            ds.close();
            ListView listView = (ListView) findViewById(R.id.orderList);
            //Uses ProductAdapter to create each row
            adapter = new ProductAdapter(this, products);
            listView.setAdapter(adapter);
            //Only displays when ListView is empty
            listView.setEmptyView(findViewById(R.id.emptyListDisp));
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error retrieving products", Toast.LENGTH_SHORT).show();
        }
    }

    //Runs when app is first started or is resumed. Setting ListView is done here so that it is up-to-date if database is modified
    @Override
    protected void onResume(){
        super.onResume();
        //Initialize DataSource used to access database
        loadItems();
    }

    private AlertDialog AskOption(final Product toDelete)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete?")
                // .setIcon(R.drawable.delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        ds.open();
                        ds.deleteProduct(toDelete);
                        ds.close();
                        loadItems();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }


    private void initListItemLongClick() {
        ListView listView = (ListView) findViewById(R.id.orderList);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = products.get(position);
                AlertDialog alert = AskOption(selectedProduct);
                alert.show();
                return true;
            }
        });
    }

    //Initializes ListView row items to make them clickable
    private void initListItemClick(){
        ListView listView = (ListView) findViewById(R.id.orderList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Gets selected product at position clicked
                Product selectedProduct = products.get(position);
                //Start creating BuyDialog dialog box to get quantity/weight
                FragmentManager fm = getSupportFragmentManager();
                //Product name and price are passed so that dialog can display them
                BuyDialog bd = new BuyDialog().newInstance(selectedProduct.getProductName(), selectedProduct.getProductPrice());
                //The tag does nothing but it's required
                bd.show(fm, "BuyDialog");
            }
        });
    }



    //This is called in BuyDialog.SaveQuantityListener so that we can save quantity/weight entered in dialog
    @Override
    public void finishedBuyDialog(String productName, double productPrice, double productQnty){
        //Initialize and set name, price, and quantity/weight
        currentProduct = new CheckoutProduct();
        if(productQnty == 0){
            Toast.makeText(this, "Couldn't add to cart due to invalid quantity", Toast.LENGTH_LONG).show();
        }
        else {
            currentProduct.setProductName(productName);
            currentProduct.setProductPrice(productPrice);
            currentProduct.setProductWeight(productQnty);
            currentProduct.setTotalPrice(productQnty * productPrice);
            //Add to cart list
            checkoutProducts.add(currentProduct);
            //Confirm correct product was added
            Toast.makeText(this, "Added " + productName + " to cart", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finishedAddProductListener(String productName, String prodCategory, double productPrice) {
        if(productName.equals("") || productPrice== 0){
            Toast.makeText(this, "Couldn't add to database due to invalid name/price", Toast.LENGTH_LONG).show();
        }
        else {
            Product newProduct = new Product(productName, prodCategory, productPrice);
            ds.open();
            ds.insertProduct(newProduct);
            ds.close();
        }
        loadItems();
    }



    //Initializes button to go to cart
    private void initCartButton(){
        //Initialize cart button defined in checkout_layout.xml and make clickable
        Button cartButton = (Button) findViewById(R.id.cartBtn);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prevent user from going to cart if it's empty
                if(checkoutProducts.size() == 0){
                    Toast.makeText(OrderActivity.this, "Cart is currently empty", Toast.LENGTH_LONG).show();
                }
                else {
                    //Intent is used to transition from OrderActivity to CheckoutActivity
                    Intent intent = new Intent(OrderActivity.this, CheckoutActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //Cart items are passed as Extras with Intent object using key,value pair
                    intent.putExtra("checkoutproducts", checkoutProducts);
                    //Load CheckoutActivity
                    startActivity(intent);
                }
            }
        });
    }

    private void initAddNewProductButton(){
        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start creating BuyDialog dialog box to get quantity/weight
                FragmentManager fm = getSupportFragmentManager();
                //Product name and price are passed so that dialog can display them
                AddNewProduct anp = new AddNewProduct();
                //The tag does nothing but it's required
                anp.show(fm, "AddNewProduct");
            }
        });
    }

    private void  initChartButton(){
        Button chartButton = (Button) findViewById(R.id.chartBtn);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, GraphActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

}