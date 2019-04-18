package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

//Extends ArrayAdapter
public class CheckoutProductAdapter extends ArrayAdapter{

    //ArrayList of cart items
    private ArrayList<CheckoutProduct> checkoutProducts;
    private Context adapterContext;



    //Initializes adapter. Layout of each row defined by checkout_list_item.xml
    public CheckoutProductAdapter(Context context, ArrayList<CheckoutProduct> products){
        super(context, R.layout.checkout_list_item, products);
        adapterContext = context;
        this.checkoutProducts = products;
    }



    //Called automatically from CheckoutActivity. Populates each ListView row with each cart item.
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            CheckoutProduct product = checkoutProducts.get(position);
            //Layout of each row defined by checkout_list_item.xml
            if(v==null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.checkout_list_item, null);
            }
            //Initialize TextViews for name, price, quantity/weight, and total cost for each cart item
            TextView productName = (TextView) v.findViewById(R.id.prodName);
            TextView productPrice = (TextView) v.findViewById(R.id.prodPrice);
            TextView productQnty = (TextView) v.findViewById(R.id.prodQnty);
            TextView productTotal = (TextView) v.findViewById(R.id.prodTotal);
            //Set text values for each TextView
            productName.setText(product.getProductName());
            //Studio doesn't like doing Strings like this but it works
            productPrice.setText("$"+String.valueOf(product.getProductPrice()));
            productQnty.setText(String.valueOf(product.getProductWeight())+"lb");
            //Total cost rounded to two decimal places.
            productTotal.setText("$"+String.format(Locale.ENGLISH,"%.2f", product.getTotalPrice()));
        }
        catch(Exception e){
            //I'm sure this does something important
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }
}
