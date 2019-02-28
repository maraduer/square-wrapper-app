package gmu.rqr.square_wrapper_app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class BuyDialog extends DialogFragment {
    String prodName;
    double prodPrice;


    public BuyDialog() {
        //Do nothing
    }

    static BuyDialog newInstance(String s, double num){
        BuyDialog bd = new BuyDialog();
        Bundle args = new Bundle();
        args.putString("name", s);
        args.putDouble("price", num);
        bd.setArguments(args);

        return bd;

    }

    public interface SaveQuantityListener{
        void finishedBuyDialog(String prodName, double prodPrice, double prodQuantity);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        prodName = getArguments().getString("name");
        prodPrice = getArguments().getDouble("price");
        final View view = inflater.inflate(R.layout.buy_product, container);
        getDialog().setTitle("Enter Quantity");
        TextView dialogName = (TextView) view.findViewById(R.id.dialogProdName);
        dialogName.setText(prodName);
        TextView dialogPrice = (TextView) view.findViewById(R.id.dialogProdPrice);
        dialogPrice.setText(String.valueOf(prodPrice));
        final EditText qnty = (EditText) view.findViewById(R.id.dialogQty);
        Button saveBtn = (Button) view.findViewById(R.id.buttonSelect);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                double prodQnty = Double.parseDouble(qnty.getText().toString());
                saveItem(prodQnty);
            }
        });
        Button cancelButton = (Button) view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getDialog().dismiss();
            }
        });
        return view;

    }

    private void saveItem(double prodQnty){
        SaveQuantityListener activity = (SaveQuantityListener) getActivity();
        activity.finishedBuyDialog(prodName, prodPrice, prodQnty);
        getDialog().dismiss();
    }

}
