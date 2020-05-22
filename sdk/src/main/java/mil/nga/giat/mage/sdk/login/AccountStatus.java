package mil.nga.giat.mage.sdk.login;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information from resulting login or sign up
 * 
 * @author wiedemanns
 *
 */
public class AccountStatus {
	
	public static enum Status {
		SUCCESSFUL_LOGIN,
		DISCONNECTED_LOGIN,
		FAILED_LOGIN,
		FAILED_AUTHORIZATION,
		FAILED_SIGNUP,
		SUCCESSFUL_SIGNUP,
		INVALID_SERVER
	}

	/**
	 * Request was successful or not
	 */
	private Status status = Status.FAILED_LOGIN;

	/**
	 * If status was false. Then this list can correspond to which argument(s)
	 * of {@link AbstractAccountTask#execute(String...)} that were problematic
	 * in the request.
	 */
	private List<Integer> errorIndices = new ArrayList<Integer>();

	/**
	 * Information about the problems if errorIndices is present
	 */
	private List<String> errorMessages = new ArrayList<String>();

	/**
	 * 
	 * If status was true, contains information relevant to the
	 * {@link AbstractAccountTask}, such as a user's token
	 */
	private JsonObject accountInformation = new JsonObject();

	public AccountStatus(Status status) {
		super();
		this.status = status;
	}

	public AccountStatus(Status status, List<Integer> errorIndices, List<String> errorMessages) {
		super();
		this.status = status;
		this.errorIndices = errorIndices;
		this.errorMessages = errorMessages;
	}

	public AccountStatus(Status status, List<Integer> errorIndices, List<String> errorMessages, JsonObject accountInformation) {
		super();
		this.status = status;
		this.errorIndices = errorIndices;
		this.errorMessages = errorMessages;
		this.accountInformation = accountInformation;
	}

	public final Status getStatus() {
		return status;
	}

	public final List<Integer> getErrorIndices() {
		return errorIndices;
	}

	public final List<String> getErrorMessages() {
		return errorMessages;
	}

	public final JsonObject getAccountInformation() {
		return accountInformation;
	}
}
