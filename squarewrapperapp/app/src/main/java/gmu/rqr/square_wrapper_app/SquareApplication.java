package gmu.rqr.square_wrapper_app;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.sdk.reader.ReaderSdk;
import com.squareup.sdk.reader.authorization.AuthorizationManager;
import com.squareup.sdk.reader.authorization.AuthorizeErrorCode;
import com.squareup.sdk.reader.authorization.Location;
import com.squareup.sdk.reader.core.CallbackReference;
import com.squareup.sdk.reader.core.Result;
import com.squareup.sdk.reader.core.ResultError;

public class SquareApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        ReaderSdk.initialize(this);
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Required if minSdkVersion < 21
        MultiDex.install(this);
    }
}
