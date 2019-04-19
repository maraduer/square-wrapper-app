package gmu.rqr.square_wrapper_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import com.squareup.sdk.reader.ReaderSdk;
import com.squareup.sdk.reader.authorization.AuthorizationManager;
import com.squareup.sdk.reader.authorization.DeauthorizeErrorCode;
import com.squareup.sdk.reader.checkout.AdditionalPaymentType;
import com.squareup.sdk.reader.checkout.CheckoutErrorCode;
import com.squareup.sdk.reader.checkout.CheckoutManager;
import com.squareup.sdk.reader.checkout.CheckoutParameters;
import com.squareup.sdk.reader.checkout.CheckoutResult;
import com.squareup.sdk.reader.checkout.CurrencyCode;
import com.squareup.sdk.reader.checkout.Money;
import com.squareup.sdk.reader.core.CallbackReference;
import com.squareup.sdk.reader.core.Result;
import com.squareup.sdk.reader.core.ResultError;
import com.squareup.sdk.reader.hardware.ReaderManager;
import com.squareup.sdk.reader.hardware.ReaderSettingsErrorCode;

public class CheckoutActivity extends AppCompatActivity{

    //Used to hold cart items
    private ArrayList<CheckoutProduct> checkoutProducts;
    private static final String TAG = CheckoutActivity.class.getSimpleName();
    CheckoutProductAdapter adapter;
    private CallbackReference deauthorizeCallbackRef;
    private CallbackReference checkoutCallbackRef;
    private CallbackReference readerSettingsCallbackRef;
    private boolean waitingForActivityStart = false;
    private ProductDataSource ds;



    //Runs when activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Uses checkout_layout.xml for layout and widgets
        setContentView(R.layout.checkout_layout);
        initListItemLongClick();
        //Gets ArrayList of cart items that were passed as Extras from OrderActivity. CheckoutProduct must implement Serializable in order for this to work.
        checkoutProducts = (ArrayList<CheckoutProduct>) getIntent().getSerializableExtra("checkoutproducts");
        ds = new ProductDataSource(this);
    }

    private void onCheckoutResult(Result<CheckoutResult, ResultError<CheckoutErrorCode>> result) {
        if (result.isSuccess()) {
            CheckoutResult checkoutResult = result.getSuccessValue();
            String totalAmount = checkoutResult.getTotalMoney().format();
            showDialog(getString(R.string.checkout_success_dialog_title, totalAmount),
                    getString(R.string.checkout_success_dialog_message),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO: store successfully purchased items in transaction DB for reporting
                            ds.open();
                            ds.logTransaction(checkoutProducts);
                            ds.close();
                            goToOrderActivity();
                        }
                    });
            Log.d(TAG, "\n" + checkoutResult.toString() + "\n");
        } else {
            ResultError<CheckoutErrorCode> error = result.getError();

            switch (error.getCode()) {
                case SDK_NOT_AUTHORIZED:
                    goToAuthorizeActivity();
                    break;
                case CANCELED:
                    Toast.makeText(this, R.string.checkout_canceled_toast, Toast.LENGTH_SHORT).show();
                    break;
                case USAGE_ERROR:
                    showErrorDialog(error);
                    break;
            }
        }
    }

    private void onReaderSettingsResult(Result<Void, ResultError<ReaderSettingsErrorCode>> result) {
        if (result.isError()) {
            ResultError<ReaderSettingsErrorCode> error = result.getError();
            switch (error.getCode()) {
                case SDK_NOT_AUTHORIZED:
                    goToAuthorizeActivity();
                    break;
                case USAGE_ERROR:
                    showErrorDialog(error);
                    break;
            }
        }
    }

    private void onDeauthorizeResult(Result<Void, ResultError<DeauthorizeErrorCode>> result) {
        if (result.isSuccess()) {
            goToAuthorizeActivity();
        } else {
            showErrorDialog(result.getError());
        }
    }

    private void goToAuthorizeActivity() {
        if (waitingForActivityStart) {
            return;
        }
        waitingForActivityStart = true;
        Intent intent = new Intent(this, StartAuthorizeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToOrderActivity() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadCartList() {
        if(checkoutProducts.size() > 0){
            //Create link to ListView defined in checkout_layout.xml
            ListView listView = (ListView) findViewById(R.id.cartList);
            //Layout for each row of ListView populated by CheckoutProductAdapter using list of items in cart
            adapter = new CheckoutProductAdapter(this, checkoutProducts);
            listView.setAdapter(adapter);

            double totalPrice = 0.0;
            for (int i = 0; i < checkoutProducts.size(); i++) {
                totalPrice += checkoutProducts.get(i).getTotalPrice();
            }CheckoutManager checkoutManager = ReaderSdk.checkoutManager();
            checkoutCallbackRef = checkoutManager.addCheckoutActivityCallback(this::onCheckoutResult);

            ReaderManager readerManager = ReaderSdk.readerManager();
            readerSettingsCallbackRef =
                    readerManager.addReaderSettingsActivityCallback(this::onReaderSettingsResult);

            AuthorizationManager authorizationManager = ReaderSdk.authorizationManager();
            deauthorizeCallbackRef = authorizationManager.addDeauthorizeCallback(this::onDeauthorizeResult);

            if (!authorizationManager.getAuthorizationState().isAuthorized()) {
                goToAuthorizeActivity();
            } else {
                // 100 is the amount of money in the smallest denomination of the specified currency.
                // For example, when the currency is USD, the amount here is 100 cents, i.e. 1 US Dollar
                int checkoutPrice = (int) Math.ceil(totalPrice * 100);
                Money checkoutAmount = new Money(checkoutPrice, CurrencyCode.current());

                TextView startCheckoutButton = findViewById(R.id.start_checkout_button);
                startCheckoutButton.setOnClickListener(view -> startCheckout(checkoutAmount));
                startCheckoutButton.setText(getString(R.string.start_checkout, checkoutAmount.format()));
            }
        } else {
            goToOrderActivity();
        }
    }

    //Runs when activity is started or resumed. Building ListView is done in this method in order to refresh the view if it is changed
    @Override
    protected void onResume(){
        super.onResume();
        //There should always be something in cart by this point anyway
        loadCartList();
        waitingForActivityStart = false;
    }

    private void initListItemLongClick() {
        ListView listView = (ListView) findViewById(R.id.cartList);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog alert = removeProduct(position);
                alert.show();
                return true;
            }
        });
    }

    private AlertDialog removeProduct(final int toDelete)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                // .setIcon(R.drawable.delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        checkoutProducts.remove(toDelete);
                        loadCartList();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        readerSettingsCallbackRef.clear();
        checkoutCallbackRef.clear();
        deauthorizeCallbackRef.clear();
    }

    private void startCheckout(Money checkoutAmount) {
        if (waitingForActivityStart) {
            return;
        }
        waitingForActivityStart = true;
        CheckoutManager checkoutManager = ReaderSdk.checkoutManager();
        CheckoutParameters.Builder params = CheckoutParameters.newBuilder(checkoutAmount);
        params.additionalPaymentTypes(AdditionalPaymentType.CASH);
        params.note("Hello 💳 💰 World!");
        checkoutManager.startCheckoutActivity(this, params.build());
    }

    private void showErrorDialog(ResultError<?> error) {
        String dialogMessage = error.getMessage();
        if (BuildConfig.DEBUG) {
            dialogMessage += "\n\nDebug Message: " + error.getDebugMessage();
            Log.d(TAG, error.getCode() + ": " + error.getDebugCode() + ", " + error.getDebugMessage());
        }
        showDialog(getString(R.string.error_dialog_title), dialogMessage, null);
    }

    private void showDialog(CharSequence title, CharSequence message, DialogInterface.OnClickListener callback) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, callback)
                .show();
    }
}
