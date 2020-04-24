package mil.nga.giat.mage.sdk.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mil.nga.giat.mage.sdk.R;
import mil.nga.giat.mage.sdk.datastore.DaoStore;
import mil.nga.giat.mage.sdk.datastore.user.User;
import mil.nga.giat.mage.sdk.datastore.user.UserHelper;
import mil.nga.giat.mage.sdk.http.resource.DeviceResource;
import mil.nga.giat.mage.sdk.jackson.deserializer.UserDeserializer;
import mil.nga.giat.mage.sdk.utils.ISO8601DateFormatFactory;

/**
 * Performs login to specified oauth server.
 *
 * @author newmanw
 *
 */
public class IdpLoginTask extends AbstractAccountTask {

	private static final String LOG_NAME = IdpLoginTask.class.getName();
	private DateFormat iso8601Format = ISO8601DateFormatFactory.ISO8601();
	private UserDeserializer userDeserializer;

	public IdpLoginTask(AccountDelegate delegate, Context context) {
		super(delegate, context);

		userDeserializer = new UserDeserializer(context);
	}

	/**
	 * Called from execute
	 *
	 * @param params Should contain oauth login JSON info as first param
	 * @return On success, {@link AccountStatus#getAccountInformation()}
	 * contains the user's token
	 */
	@Override
	protected AccountStatus doInBackground(String... params) {
		String strategy = params[0];
		String uid = params[1];
		String jwt = params[2];

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
		final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mApplicationContext).edit();

		try {
			DeviceResource deviceResource = new DeviceResource(mApplicationContext);
			JsonObject authorization = deviceResource.authorize(jwt, strategy, uid);
			if (authorization == null) {
				return new AccountStatus(AccountStatus.Status.FAILED_AUTHORIZATION);
			}

			// Successful login, put the token information in the shared preferences
			String token = authorization.get("token").getAsString();
			editor.putString(mApplicationContext.getString(mil.nga.giat.mage.sdk.R.string.tokenKey), token.trim());
			try {
				Date tokenExpiration = iso8601Format.parse(authorization.get("expirationDate").getAsString().trim());
				long tokenExpirationLength = tokenExpiration.getTime() - (new Date()).getTime();
				editor.putString(mApplicationContext.getString(mil.nga.giat.mage.sdk.R.string.tokenExpirationDateKey), iso8601Format.format(tokenExpiration));
				editor.putLong(mApplicationContext.getString(mil.nga.giat.mage.sdk.R.string.tokenExpirationLengthKey), tokenExpirationLength);
			} catch (java.text.ParseException e) {
				Log.e(LOG_NAME, "Problem parsing token expiration date.", e);
			}

			JsonObject userJson = authorization.getAsJsonObject("user");

			// if user id is different, then clear the db
			String oldUserId = sharedPreferences.getString(mApplicationContext.getString(R.string.usernameKey), null);
			String newUserId = userJson.get("id").getAsString();
			if (oldUserId == null || !oldUserId.equals(newUserId)) {
				DaoStore.getInstance(mApplicationContext).resetDatabase();
			}

			User user = userDeserializer.parseUser(userJson.toString());
			if (user != null) {
				user.setFetchedDate(new Date());
				UserHelper userHelper = UserHelper.getInstance(mApplicationContext);
				user = userHelper.createOrUpdate(user);

				userHelper.setCurrentUser(user);
			} else {
				Log.e(LOG_NAME, "Unable to Deserializer user.");
				List<Integer> errorIndices = new ArrayList<Integer>();
				errorIndices.add(2);
				List<String> errorMessages = new ArrayList<String>();
				errorMessages.add("Problem retrieving your user.");
				return new AccountStatus(AccountStatus.Status.FAILED_LOGIN, errorIndices, errorMessages);
			}

			editor.putString(mApplicationContext.getString(R.string.usernameKey), newUserId);
			editor.putString(mApplicationContext.getString(R.string.displayNameKey), user.getDisplayName());

			editor.commit();

			return new AccountStatus(AccountStatus.Status.SUCCESSFUL_LOGIN, new ArrayList<Integer>(), new ArrayList<String>(), authorization);

		} catch (Exception e) {
			Log.e(LOG_NAME, "Problem with oauth login attempt", e);
			return new AccountStatus(AccountStatus.Status.FAILED_LOGIN);
		}
	}
}