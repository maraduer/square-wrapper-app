package com.example.test1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.test1.MESSAGE";
    public double cartTotal=0;
    public ArrayList<Double> cartTotals = new ArrayList<Double>();
    public ArrayList<String> cartItems = new ArrayList<String>();
    static final String cItemState = "itemStates";
    static final String cTotalState = "cartTotalStates";
    static final String totalState = "totalState";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cartItems = savedInstanceState.getStringArrayList(cItemState);
            cartTotal = savedInstanceState.getDouble(totalState);
            double[] savedState = savedInstanceState.getDoubleArray(cTotalState);
            for (int i = 0; i < savedState.length; i++) {
                cartTotals.add(new Double(savedState[i]));
            }
            TextView textCartTotal = findViewById(R.id.TVcartTotal);
            textCartTotal.setText(Double.toString(cartTotal));
        }
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button
     *
     * Should be Checkout button. Does Square POS API calls instead of loading a new screen.*/
   public void sendCart(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        int i;
        String message = "";
        for(i = 0; i < cartTotals.size(); i++) {
            message = cartItems.get(i) + "\n" + Double.toString(cartTotals.get(i)) +"\n \n";
        }
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    public void addItem(View view){
        double itemTotal=0;
        EditText unitNumText = findViewById(R.id.editText3);
        EditText unitPriceText = findViewById(R.id.editText2);
        double unitNumber= Double.parseDouble(unitNumText.getText().toString());
        double unitPrice = Double.parseDouble(unitPriceText.getText().toString());
        double newitemTotal = unitNumber * unitPrice;
        itemTotal += newitemTotal;
        TextView textView = (TextView) findViewById(R.id.TVitemTotal);
        textView.setText(Double.toString(itemTotal));
    }


    public void addToCart(View view){
        TextView textItemTotal = findViewById(R.id.TVitemTotal);
        EditText editText = findViewById(R.id.editText);
        double newCartTotal = Double.parseDouble(textItemTotal.getText().toString());
        cartTotals.add(newCartTotal);
        cartItems.add(editText.getText().toString());
        int i;
        double sum = 0;
        for(i = 0; i < cartTotals.size(); i++) {
            sum += cartTotals.get(i);
        }
        cartTotal = sum;
        TextView textCartTotal = findViewById(R.id.TVcartTotal);
        textCartTotal.setText(Double.toString(sum));
    }


    public void removeLastCart(View view){
        int last = cartTotals.size() - 1;
        double sum = 0;
        try {
            cartTotals.remove(last);
            cartItems.remove(last);
            int i;
            for (i = 0; i < cartTotals.size(); i++) {
                sum += cartTotals.get(i);
            }
        }catch(Exception e){sum = 0;}
        cartTotal = sum;
        TextView textCartTotal = findViewById(R.id.TVcartTotal);
        textCartTotal.setText(Double.toString(sum));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble(totalState, cartTotal);
        savedInstanceState.putStringArrayList(cItemState,cartItems);
        double[] saved = new double[cartTotals.size()];
        for (int i =0; i < cartTotals.size(); i++) {
            saved[i] = cartTotals.get(i);
        }
        savedInstanceState.putDoubleArray(cTotalState, saved);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}

