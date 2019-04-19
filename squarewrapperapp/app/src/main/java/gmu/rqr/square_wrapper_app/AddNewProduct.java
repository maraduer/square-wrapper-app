package gmu.rqr.square_wrapper_app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddNewProduct extends DialogFragment implements AdapterView.OnItemSelectedListener {
    String prodName;
    String prodCategory;
    double prodPrice;



    //Constructor required by the DialogFragment class
    public AddNewProduct() {
        //Do nothing
    }




    //Interface that is used so that we can save results of BuyDialog in OrderActivity when dialog is closed. Called by saveItem method below.
    public interface SaveProductListener{
        //Calls finishedBuyDialog method in OrderActivity
        void finishedAddProductListener(String prodName, String prodCategory, double prodPrice);
    }



    //Creates BuyDialog pop-up. Called automatically.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Inflates using buy_product.xml as layout
        final View view = inflater.inflate(R.layout.add_new_product, container);
        //Title of dialog
        getDialog().setTitle("Enter Product Details");

        //Add EditText so user can enter quantity
        final EditText name = (EditText) view.findViewById(R.id.productNameInput);
        final EditText price = (EditText) view.findViewById(R.id.productPriceInput);

        //Select product category spinner
        final Spinner categorySpin = (Spinner) view.findViewById(R.id.prodCategory);
        String[] categories = {"Poultry", "Pork", "Beef", "Other"};
        categorySpin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpin.setAdapter(aa);

        //Save button created and activated
        Button saveBtn = (Button) view.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Get value entered into EditText and pass to saveItem method below
                String prodName = name.getText().toString();
                if(!price.getText().toString().equals("") && !prodName.equals("")){
                    double prodPrice = Double.parseDouble(price.getText().toString());
                    String prodCategory = categorySpin.getSelectedItem().toString();
                    saveItem(prodName, prodCategory, prodPrice);

                }
                else{
                    saveItem("", "", 0.0);
                }

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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    //Used to save quantity/weight to OrderActivity
    private void saveItem(String prodName, String prodCategory, double prodPrice){
        //Uses interface created above
        AddNewProduct.SaveProductListener activity = (AddNewProduct.SaveProductListener) getActivity();
        //Call to method in OrderActivity
        activity.finishedAddProductListener(prodName, prodCategory, prodPrice);
        //Close dialog
        getDialog().dismiss();
    }

}
