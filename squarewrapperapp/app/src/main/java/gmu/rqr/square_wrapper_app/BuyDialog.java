package gmu.rqr.square_wrapper_app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Inherits from DialogFragment class (used for things you want as a pop-up/overlay)
public class BuyDialog extends DialogFragment {
    String prodName;
    double prodPrice;



    //Constructor required by the DialogFragment class
    public BuyDialog() {
        //Do nothing
    }



    //Creates new instance of BuyDialog, which is used to enter quantity/weight. This is called in OrderActivity in initListItemClick method
    //Input: String, double representing product name and price
    //Output: BuyDialog object
    static BuyDialog newInstance(String s, double num){
        BuyDialog bd = new BuyDialog();
        Bundle args = new Bundle();
        args.putString("name", s);
        args.putDouble("price", num);
        bd.setArguments(args);
        return bd;
    }



    //Interface that is used so that we can save results of BuyDialog in OrderActivity when dialog is closed. Called by saveItem method below.
    public interface SaveQuantityListener{
        //Calls finishedBuyDialog method in OrderActivity
        void finishedBuyDialog(String prodName, double prodPrice, double prodQuantity);
    }



    //Creates BuyDialog pop-up. Called automatically.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Get name and price from arguments that were added during object creation
        prodName = getArguments().getString("name");
        prodPrice = getArguments().getDouble("price");
        //Inflates using buy_product.xml as layout
        final View view = inflater.inflate(R.layout.buy_product, container);
        //Title of dialog
        getDialog().setTitle("Enter Quantity");
        //Add text displaying name of product
        TextView dialogName = (TextView) view.findViewById(R.id.dialogProdName);
        dialogName.setText(prodName);
        //Add text displaying price of product
        TextView dialogPrice = (TextView) view.findViewById(R.id.dialogProdPrice);
        dialogPrice.setText(String.valueOf(prodPrice));
        //Add EditText so user can enter quantity
        final EditText qnty = (EditText) view.findViewById(R.id.dialogQty);

        //Save button created and activated
        Button saveBtn = (Button) view.findViewById(R.id.buttonSelect);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Get value entered into EditText and pass to saveItem method below
                double prodQnty = Double.parseDouble(qnty.getText().toString());
                saveItem(prodQnty);
            }
        });

        //Cancel button created and activated
        Button cancelButton = (Button) view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Close dialog without doing anything
                getDialog().dismiss();
            }
        });
        return view;
    }



    //Used to save quantity/weight to OrderActivity
    private void saveItem(double prodQnty){
        //Uses interface created above
        SaveQuantityListener activity = (SaveQuantityListener) getActivity();
        //Call to method in OrderActivity
        activity.finishedBuyDialog(prodName, prodPrice, prodQnty);
        //Close dialog
        getDialog().dismiss();
    }

}
