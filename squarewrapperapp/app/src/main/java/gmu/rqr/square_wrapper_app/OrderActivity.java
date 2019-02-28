package gmu.rqr.square_wrapper_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OrderActivity  extends AppCompatActivity implements BuyDialog.SaveQuantityListener{

    ArrayList<Product> products;
    ArrayList<CheckoutProduct> checkoutProducts = new ArrayList<>();
    ProductAdapter adapter;
    private CheckoutProduct currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_layout);
        initListItemClick();
        initCartButton();


        Product p1 = new Product("Sausage", 2.99);
        Product p2 = new Product("Bacon", 1.99);
        Product p3 = new Product("Chicken", 4.99);
        Product p4 = new Product("Canadian Bacon", 2.99);
        Product p5 = new Product("Tenderloin", 3.49);

        ProductDataSource ds = new ProductDataSource(this);
        try{
            ds.open();
//            ds.insertProduct(p1);
//            ds.insertProduct(p2);
//            ds.insertProduct(p3);
//            ds.insertProduct(p4);
//            ds.insertProduct(p5);
            ds.close();

        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        ProductDataSource ds = new ProductDataSource(this);
        try{
            ds.open();
            products = ds.getProducts();
            ds.close();
            if(products.size() > 0){
                ListView listView = (ListView) findViewById(R.id.orderList);
                adapter = new ProductAdapter(this, products);
                listView.setAdapter(adapter);
            }
            else{
                //Open something else
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error retrieving products", Toast.LENGTH_SHORT).show();
        }
    }


    private void initListItemClick(){
        ListView listView = (ListView) findViewById(R.id.orderList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = products.get(position);
                FragmentManager fm = getSupportFragmentManager();
                BuyDialog bd = new BuyDialog().newInstance(selectedProduct.getProductName(), selectedProduct.getProductPrice());
                bd.show(fm, "BuyDialog");
            }
        });
    }

    @Override
    public void finishedBuyDialog(String productName, double productPrice, double productQnty){
        currentProduct = new CheckoutProduct();
        currentProduct.setProductName(productName);
        currentProduct.setProductPrice(productPrice);
        currentProduct.setProductWeight(productQnty);
        checkoutProducts.add(currentProduct);
        Toast.makeText(this, "Added " + productName + " to cart", Toast.LENGTH_SHORT).show();
    }

    private void initCartButton(){
        Button cartButton = (Button) findViewById(R.id.cartBtn);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkoutProducts.size() == 0){
                    Toast.makeText(OrderActivity.this, "Cart is currently empty", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(OrderActivity.this, CheckoutActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("checkoutproducts", checkoutProducts);
                    startActivity(intent);
                }
            }
        });
    }


}
