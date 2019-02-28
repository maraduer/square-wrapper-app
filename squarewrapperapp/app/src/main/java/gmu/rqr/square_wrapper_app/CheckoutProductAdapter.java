package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;


public class CheckoutProductAdapter extends ArrayAdapter{
    private ArrayList<CheckoutProduct> checkoutProducts;
    private Context adapterContext;

    public CheckoutProductAdapter(Context context, ArrayList<CheckoutProduct> products){
        super(context, R.layout.checkout_list_item, products);
        adapterContext = context;
        this.checkoutProducts = products;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            CheckoutProduct product = checkoutProducts.get(position);
            if(v==null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.checkout_list_item, null);
            }
            TextView productName = (TextView) v.findViewById(R.id.prodName);
            TextView productPrice = (TextView) v.findViewById(R.id.prodPrice);
            TextView productQnty = (TextView) v.findViewById(R.id.prodQnty);
            TextView productTotal = (TextView) v.findViewById(R.id.prodTotal);
            productName.setText(product.getProductName());
            productPrice.setText("$"+String.valueOf(product.getProductPrice()));
            productQnty.setText(String.valueOf(product.getProductWeight())+"lb");
            productTotal.setText("$"+String.format(Locale.ENGLISH,"%.2f", product.getProductWeight() * product.getProductPrice()));

        }
        catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }





}

