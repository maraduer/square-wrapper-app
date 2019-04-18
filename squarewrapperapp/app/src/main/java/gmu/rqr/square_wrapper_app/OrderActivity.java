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

        //Temporary way to load database
        Product p1 = new Product("Sausage", 2.99);
        Product p2 = new Product("Bacon", 1.99);
        Product p3 = new Product("Chicken", 4.99);
        Product p4 = new Product("Canadian Bacon", 2.99);
        Product p5 = new Product("Tenderloin", 3.49);

        //Initialize DataSource used to access DB
        ds = new ProductDataSource(this);
        //Insert temporary products into database. This will happen every time you test app, so comment out when you have enough products
        try{
            //ds.open();
            //Product toDelete = ds.getProducts().get(0);
            //ds.deleteProduct(toDelete);
            //Comment out to stop adding products
           // ds.insertProduct(p1);
            //ds.insertProduct(p2);
            //ds.insertProduct(p3);
            //ds.insertProduct(p4);
            //ds.insertProduct(p5);
            //^^^^
            ds.close();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to progrmattically add an item to the db
     * Product newProduct = new Product(name, price);
     * ds.open();
     * ds.insertProduct(newProduct);
     * ds.close();
     */


    private void loadItems() {
//        ds = new ProductDataSource(this);
//        if (ds == null) {
//            ds = new ProductDataSource(this);
//        }
        try{
            ds.open();
            //Returns ArrayList<Product> from database
            products = ds.getProducts();
            ds.close();
            if(products.size() > 0){
                //Populate ListView from order_layout.xml with list of products
                ListView listView = (ListView) findViewById(R.id.orderList);
                //Uses ProductAdapter to create each row
                adapter = new ProductAdapter(this, products);
                listView.setAdapter(adapter);
            }
            else{
                //Can add default behavior here if database is empty
            }
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
                .setMessage("Do you want to Delete")
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
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
                Product selectedProdut = products.get(position);
                AlertDialog alert = AskOption(selectedProdut);
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
        currentProduct.setProductName(productName);
        currentProduct.setProductPrice(productPrice);
        currentProduct.setProductWeight(productQnty);
        currentProduct.setTotalPrice(productQnty * productPrice);
        //Add to cart list
        checkoutProducts.add(currentProduct);
        //Confirm correct product was added
        Toast.makeText(this, "Added " + productName + " to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishedAddProductListener(String productName, double productPrice) {
        Product newProduct = new Product(productName, productPrice);
        ds.open();
        ds.insertProduct(newProduct);
        ds.close();
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
                    //Cart items are passed as Extras with Intent object uaing key,value pair
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
}