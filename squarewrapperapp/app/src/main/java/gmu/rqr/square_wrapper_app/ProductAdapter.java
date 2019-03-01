package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//Array adapter for Product class, inherits from ArrayAdapter
public class ProductAdapter extends ArrayAdapter {
    //List of all products for sale
    private ArrayList<Product> products;
    private Context adapterContext;



    //Initialize adapter. Layout for each row is defined by list_item.xml
    public ProductAdapter(Context context, ArrayList<Product> products){
        super(context, R.layout.list_item, products);
        adapterContext = context;
        this.products = products;
    }



    //Called automatically by ProductAdapter. Populates each ListView row with products for sale
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            Product product = products.get(position);
            //Layout of each ListView row defined by list_item.xml
            if(v==null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            //Initialize TextViews to display name and price for each product
            TextView productName = (TextView) v.findViewById(R.id.prodName);
            TextView productPrice = (TextView) v.findViewById(R.id.prodPrice);
            //Set the name and price to display in TextView
            productName.setText(product.getProductName());
            productPrice.setText(String.valueOf(product.getProductPrice()));
        }
        catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }
}
