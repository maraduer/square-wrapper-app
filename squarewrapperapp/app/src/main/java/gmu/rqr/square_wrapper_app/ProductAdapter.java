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

public class ProductAdapter extends ArrayAdapter {
    private ArrayList<Product> products;
    private Context adapterContext;

    public ProductAdapter(Context context, ArrayList<Product> products){
        super(context, R.layout.list_item, products);
        adapterContext = context;
        this.products = products;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            Product product = products.get(position);
            if(v==null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            TextView productName = (TextView) v.findViewById(R.id.prodName);
            TextView productPrice = (TextView) v.findViewById(R.id.prodPrice);
            //Button buyBtn = (Button) v.findViewById(R.id.prodBtn);
            productName.setText(product.getProductName());
            Double dbPrice = product.getProductPrice();
            String strPrice = dbPrice.toString();
            productPrice.setText(strPrice);

        }
        catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }





}
