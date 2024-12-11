package digi.kitplay.data.remote;

import android.app.Application;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import digi.kitplay.constant.Constants;
import digi.kitplay.data.local.prefs.PreferencesService;
import digi.kitplay.utils.LogService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final PreferencesService appPreferences;
    private final Application application;

    public AuthInterceptor(PreferencesService appPreferences, Application application) {
        this.appPreferences = appPreferences;
        this.application = application;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        String isIgnore = chain.request().header("IgnoreAuth");
        if (isIgnore != null && isIgnore.equals("1")) {
            Request.Builder newRequest = chain.request().newBuilder();
            newRequest.removeHeader("IgnoreAuth");
            return chain.proceed(newRequest.build());
        }

        //Add Authentication
        Request.Builder newRequest = chain.request().newBuilder();
        String token = appPreferences.getToken();
        if (token != null && !token.equals("")) {
            newRequest.addHeader("Authorization", "Bearer " + token);
        }

        Response origResponse = chain.proceed(newRequest.build());
        if (origResponse.code() == 403 || origResponse.code() == 401) {
            LogService.i("Error http =====================> code: "+origResponse.code());
            appPreferences.removeKey(PreferencesService.KEY_BEARER_TOKEN);
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_EXPIRED_TOKEN);
            LocalBroadcastManager.getInstance(application.getApplicationContext()).sendBroadcast(intent);
        }
        return origResponse;
    }
}
