package mil.nga.giat.mage.sdk.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.util.Date;

import mil.nga.giat.mage.sdk.R;
import mil.nga.giat.mage.sdk.datastore.user.User;
import mil.nga.giat.mage.sdk.http.resource.DeviceResource;
import mil.nga.giat.mage.sdk.jackson.deserializer.UserDeserializer;
import mil.nga.giat.mage.sdk.preferences.PreferenceHelper;
import mil.nga.giat.mage.sdk.utils.DeviceUuidFactory;
import mil.nga.giat.mage.sdk.utils.ISO8601DateFormatFactory;
import retrofit2.Response;

/**
 * Performs login to specified oauth server.
 *
 * @author newmanw
 *
 */
public class AuthorizationTask extends AsyncTask<String, Void, AuthorizationStatus> {

	public interface AuthorizationDelegate {
		void onAuthorizationComplete(AuthorizationStatus status);
	}

	private static final String LOG_NAME = AuthorizationTask.class.getName();

	private DateFormat iso8601Format = ISO8601DateFormatFactory.ISO8601();
	private Context applicationContext;
	private AuthorizationDelegate delegate;
	private UserDeserializer userDeserializer;

	public AuthorizationTask(Context applicationContext, AuthorizationDelegate delegate) {
		this.applicationContext = applicationContext;
		this.delegate = delegate;
		userDeserializer = new UserDeserializer(applicationContext);
	}

	/**
	 * Called from execute
	 *
	 * @param params Should contain authentication strategy and authentication token
	 */
	@Override
	protected AuthorizationStatus doInBackground(String... params) {
		String jwt = params[0];

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();

		try {
			DeviceResource deviceResource = new DeviceResource(applicationContext);
			String uid = new DeviceUuidFactory(applicationContext).getDeviceUuid().toString();
			Response<JsonObject> authorizeResponse = deviceResource.authorize(jwt, uid);
			if (authorizeResponse == null || !authorizeResponse.isSuccessful()) {
				int code = authorizeResponse == null ? 401 : authorizeResponse.code();
				AuthorizationStatus.Status status = code == 403 ? AuthorizationStatus.Status.FAILED_AUTHORIZATION : AuthorizationStatus.Status.FAILED_AUTHENTICATION;
				return new AuthorizationStatus.Builder(status).build();
			}

			JsonObject authorization = authorizeResponse.body();

			// check server api version to ensure compatibility before continuing
			JsonObject serverVersion = authorization.get("api").getAsJsonObject().get("version").getAsJsonObject();
			if (!PreferenceHelper.getInstance(applicationContext).validateServerVersion(serverVersion.get("major").getAsInt(), serverVersion.get("minor").getAsInt())) {
				Log.e(LOG_NAME, "Server version not compatible");
				return new AuthorizationStatus.Builder(AuthorizationStatus.Status.INVALID_SERVER).build();
			}

			// Successful login, put the token information in the shared preferences
			String token = authorization.get("token").getAsString();
			editor.putString(applicationContext.getString(R.string.tokenKey), token.trim());
			try {
				Date tokenExpiration = iso8601Format.parse(authorization.get("expirationDate").getAsString().trim());
				long tokenExpirationLength = tokenExpiration.getTime() - (new Date()).getTime();
				editor.putString(applicationContext.getString(R.string.tokenExpirationDateKey), iso8601Format.format(tokenExpiration));
				editor.putLong(applicationContext.getString(R.string.tokenExpirationLengthKey), tokenExpirationLength);
			} catch (java.text.ParseException e) {
				Log.e(LOG_NAME, "Problem parsing token expiration date.", e);
			}
			editor.apply();

			JsonObject userJson = authorization.getAsJsonObject("user");
			User user = userDeserializer.parseUser(userJson.toString());
			return new AuthorizationStatus.Builder(AuthorizationStatus.Status.SUCCESSFUL_AUTHORIZATION)
					.authorization(user)
					.build();
		} catch (Exception e) {
			Log.e(LOG_NAME, "Problem with authorization attempt", e);
			return new AuthorizationStatus.Builder(AuthorizationStatus.Status.FAILED_AUTHORIZATION).build();
		}
	}

	@Override
	protected void onPostExecute(AuthorizationStatus status) {
		super.onPostExecute(status);

		delegate.onAuthorizationComplete(status);
	}
}
