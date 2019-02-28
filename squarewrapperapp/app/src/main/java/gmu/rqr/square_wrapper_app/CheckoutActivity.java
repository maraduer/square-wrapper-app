package gmu.rqr.square_wrapper_app;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity{

    private ArrayList<CheckoutProduct> checkoutProducts;
    CheckoutProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        checkoutProducts = (ArrayList<CheckoutProduct>) getIntent().getSerializableExtra("checkoutproducts");

    }


    @Override
    protected void onResume(){
        super.onResume();
        if(checkoutProducts.size() > 0){
            ListView listView = (ListView) findViewById(R.id.cartList);
            adapter = new CheckoutProductAdapter(this, checkoutProducts);
            listView.setAdapter(adapter);
        }

    }




}
