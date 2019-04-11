package gmu.rqr.square_wrapper_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    //Used to hold cart items
    private ArrayList<CheckoutProduct> checkoutProducts = new ArrayList<>();
    CheckoutProductAdapter adapter;
    private ProductDataSource ds;


    //Runs when activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uses checkout_layout.xml for layout and widgets
        setContentView(R.layout.checkout_layout);
        //Gets ArrayList of cart items that were passed as Extras from OrderActivity. CheckoutProduct must implement Serializable in order for this to work.
        checkoutProducts = (ArrayList<CheckoutProduct>) getIntent().getSerializableExtra("checkoutproducts");
        //initListItemLongClick();
        initSquareCheckoutBtn();
        ds = new ProductDataSource(this);

    }


    //Runs when activity is started or resumed. Building ListView is done in this method in order to refresh the view if it is changed
    @Override
    protected void onResume() {
        super.onResume();
        //There should always be something in cart by this point anyway
        if (checkoutProducts.size() > 0) {
            //Create link to ListView defined in checkout_layout.xml
            ListView listView = (ListView) findViewById(R.id.cartList);
            TextView textView = findViewById(R.id.fullTotal);
            CheckoutProduct product = new CheckoutProduct();
            double cartTotal = 0;
            for (int i = 0; i < checkoutProducts.size(); i++) {
                product = checkoutProducts.get(i);
                double itemTotal = product.getProductWeight() * product.getProductPrice();
                cartTotal += itemTotal;
            }
            //Layout for each row of ListView populated by CheckoutProductAdapter using list of items in cart
            adapter = new CheckoutProductAdapter(this, checkoutProducts);
            listView.setAdapter(adapter);
            textView.setText(" Total: $" + String.format(Locale.ENGLISH, "%.2f", cartTotal));
        }
    }


    private void initSquareCheckoutBtn(){
        Button squareBtn = (Button) findViewById(R.id.squareCheckout);
        squareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ds.open();
                ds.logTransaction(checkoutProducts);
                ds.close();
                Toast.makeText(CheckoutActivity.this,"Transaction logged", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ***Deleting from cart currently doesn't work because of how cart items are passed between activities***

//    private AlertDialog AskOption(final CheckoutProduct toDelete) {
//        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
//                //set message, title, and icon
//                .setTitle("Delete")
//                .setMessage("Do you want to delete?")
//                // .setIcon(R.drawable.delete)
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        //your deleting code
//                        checkoutProducts.remove(toDelete);
//                        dialog.dismiss();
//                        onResume();
//                    }
//
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//
//                    }
//                })
//                .create();
//        return myQuittingDialogBox;
//    }
//
//
//    private void initListItemLongClick() {
//        ListView listView = (ListView) findViewById(R.id.cartList);
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                CheckoutProduct selectedProduct = checkoutProducts.get(position);
//                AlertDialog alert = AskOption(selectedProduct);
//                alert.show();
//                return true;
//            }
//        });
//
//    }


}