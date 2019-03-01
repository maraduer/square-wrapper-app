package gmu.rqr.square_wrapper_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity{

    //Used to hold cart items
    private ArrayList<CheckoutProduct> checkoutProducts;
    CheckoutProductAdapter adapter;



    //Runs when activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Uses checkout_layout.xml for layout and widgets
        setContentView(R.layout.checkout_layout);
        //Gets ArrayList of cart items that were passed as Extras from OrderActivity. CheckoutProduct must implement Serializable in order for this to work.
        checkoutProducts = (ArrayList<CheckoutProduct>) getIntent().getSerializableExtra("checkoutproducts");
    }



    //Runs when activity is started or resumed. Building ListView is done in this method in order to refresh the view if it is changed
    @Override
    protected void onResume(){
        super.onResume();
        //There should always be something in cart by this point anyway
        if(checkoutProducts.size() > 0){
            //Create link to ListView defined in checkout_layout.xml
            ListView listView = (ListView) findViewById(R.id.cartList);
            //Layout for each row of ListView populated by CheckoutProductAdapter using list of items in cart
            adapter = new CheckoutProductAdapter(this, checkoutProducts);
            listView.setAdapter(adapter);
        }
    }
}
