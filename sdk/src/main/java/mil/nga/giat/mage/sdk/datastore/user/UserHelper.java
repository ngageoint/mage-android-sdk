package mil.nga.giat.mage.sdk.datastore.user;

import java.sql.SQLException;
import java.util.List;

import mil.nga.giat.mage.sdk.datastore.DaoHelper;
import mil.nga.giat.mage.sdk.exceptions.UserException;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

/**
 * A utility class for accessing {@link User} data from the physical data model.
 * The details of ORM DAOs and Lazy Loading should not be exposed past this
 * class.
 * 
 * @author travis, wiedemanns
 * 
 */
public class UserHelper extends DaoHelper<User> {

	private static final String LOG_NAME = UserHelper.class.getName();

	private final Dao<User, Long> userDao;
	
	/**
	 * Singleton.
	 */
	private static UserHelper mUserHelper;

	/**
	 * Use of a Singleton here ensures that an excessive amount of DAOs are not
	 * created.
	 * 
	 * @param context
	 *            Application Context
	 * @return A fully constructed and operational UserHelper.
	 */
	public static UserHelper getInstance(Context context) {
		if (mUserHelper == null) {
			mUserHelper = new UserHelper(context);
		}
		return mUserHelper;
	}

	/**
	 * Only one-per JVM. Singleton.
	 * 
	 * @param pContext
	 */
	private UserHelper(Context pContext) {
		super(pContext);

		try {
			userDao = daoStore.getUserDao();
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "Unable to communicate with User database.", sqle);

			throw new IllegalStateException("Unable to communicate with User database.", sqle);
		}

	}

	@Override
	public User create(User pUser) throws UserException {
		User createdUser = null;
		try {
			createdUser = userDao.createIfNotExists(pUser);
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "There was a problem creating user: " + pUser, sqle);
			throw new UserException("There was a problem creating user: " + pUser, sqle);
		}
		return createdUser;
	}

	@Override
	public User read(Long id) throws UserException {
		try {
			return userDao.queryForId(id);
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "Unable to query for existance for id = '" + id + "'", sqle);
			throw new UserException("Unable to query for existance for id = '" + id + "'", sqle);
		}
	}
	
    @Override
    public User read(String pRemoteId) throws UserException {
        User user = null;
        try {
            List<User> results = userDao.queryBuilder().where().eq("remote_id", pRemoteId).query();
            if (results != null && results.size() > 0) {
                user = results.get(0);
            }
        } catch (SQLException sqle) {
            Log.e(LOG_NAME, "Unable to query for existance for remote_id = '" + pRemoteId + "'", sqle);
            throw new UserException("Unable to query for existance for remote_id = '" + pRemoteId + "'", sqle);
        }
        return user;
    }

	public void update(User pUser) throws UserException {
		try {
			userDao.update(pUser);
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "There was a problem creating user: " + pUser);
			throw new UserException("There was a problem creating user: " + pUser, sqle);
		}
	}

	/**
	 * This method is used to read current Active Users from the database. An
	 * active user is one that is currently logged into the client and is
	 * presumably the user consuming Location Services.
	 * 
	 * @return A List of Users that are flagged as active in the datastore.
	 * @throws UserException
	 *             Indicates a problem reading users from the datastore.
	 */
	public User readCurrentUser() throws UserException {

		User currentUser = null;
		try {
			QueryBuilder<User, Long> qb = userDao.queryBuilder();
			Where<User, Long> where = qb.where();
			where.eq("isCurrentUser", Boolean.TRUE);
			PreparedQuery<User> preparedQuery = qb.prepare();
			currentUser = userDao.queryForFirst(preparedQuery);
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "There was a problem reading active users.");
			throw new UserException("There was a problem reading active users.", sqle);
		}

		return currentUser;
	}

	/**
	 * Delete all users that are flagged as isCurrentUser.
	 * 
	 * @throws UserException
	 *             If current users can't be deleted.
	 */
	public void deleteCurrentUsers() throws UserException {

		try {
			DeleteBuilder<User, Long> db = userDao.deleteBuilder();
			db.where().eq("isCurrentUser", Boolean.TRUE);
			db.delete();
		} catch (SQLException sqle) {
			Log.e(LOG_NAME, "There was a problem deleting active users.", sqle);
			throw new UserException("There was a problem deleting active users.", sqle);
		}
	}
	
	public User createOrUpdate(User user) {
		try {
			User oldUser = read(user.getRemoteId());
			if (oldUser == null) {
				user = create(user);
				Log.d(LOG_NAME, "Created user with remote_id " + user.getRemoteId());
			} else {
				// perform update?
				user.setId(oldUser.getId());
				update(user);
				Log.d(LOG_NAME, "Updated user with remote_id " + user.getRemoteId());
			}
		} catch (UserException ue) {
			Log.e(LOG_NAME, "There was a problem reading user: " + user, ue);
		}
		return user;
	}
}