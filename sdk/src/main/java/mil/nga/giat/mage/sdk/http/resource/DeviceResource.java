package mil.nga.giat.mage.sdk.http.resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonObject;

import mil.nga.giat.mage.sdk.R;
import mil.nga.giat.mage.sdk.http.HttpClientManager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/***
 * RESTful communication for devices
 *
 * @author newmanw
 */

public class DeviceResource {

    public interface DeviceService {
        @POST("/auth/{strategy}/authorize")
        Call<JsonObject> authorize(@Header("Authorization") String authorization, @Path("strategy") String strategy, @Body JsonObject body);
    }

    private static final String LOG_NAME = DeviceResource.class.getName();

    private Context context;

    public DeviceResource(Context context) {
        this.context = context;
    }

    public JsonObject authorize(String token, String strategy, String uid) {
        JsonObject authorization = null;

        String baseUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.serverURLKey), context.getString(R.string.serverURLDefaultValue));

        try {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(HttpClientManager.getInstance().httpClient())
                .build();

            DeviceService service = retrofit.create(DeviceService.class);

            JsonObject json = new JsonObject();
            json.addProperty("uid", uid);

            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                json.addProperty("appVersion", String.format("%s-%s", packageInfo.versionName, packageInfo.versionCode));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_NAME , "Problem retrieving package info.", e);
            }

            Response<JsonObject> response = service.authorize(String.format("Bearer %s", token), strategy, json).execute();

            if (response.isSuccessful()) {
                authorization = response.body();
            } else {
                Log.e(LOG_NAME, "Bad request.");
                if (response.errorBody() != null) {
                    Log.e(LOG_NAME, response.errorBody().string());
                }
            }
        } catch (Exception e) {
            Log.e(LOG_NAME, "Bad request.", e);
        }

        return authorization;
    }
}