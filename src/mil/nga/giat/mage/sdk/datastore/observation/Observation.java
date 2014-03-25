package mil.nga.giat.mage.sdk.datastore.observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mil.nga.giat.mage.sdk.datastore.common.State;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "observations")
public class Observation {

	@DatabaseField(generatedId = true)
	private Long pk_id;

	@DatabaseField
	private String remote_id;

	@DatabaseField(canBeNull = false, version = true)
	private long lastModified;
	
	@DatabaseField(canBeNull = false)
	private boolean dirty;

	@DatabaseField(canBeNull = false)
	private State state = State.ACTIVE;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
	private ObservationGeometry observationGeometry;

	@ForeignCollectionField(eager = true)
	private Collection<ObservationProperty> properties;

	@ForeignCollectionField(eager = true)
	private Collection<Attachment> attachments;

	public Observation() {
		// ORMLite needs a no-arg constructor
	}

	public Observation(ObservationGeometry observationGeometry, Collection<ObservationProperty> pProperties, Collection<Attachment> pAttachments) {
		this(null, System.currentTimeMillis(), observationGeometry, pProperties, pAttachments);
		this.setDirty(true);
	}

	public Observation(String remoteId, long lastModified, ObservationGeometry observationGeometry, Collection<ObservationProperty> pProperties, Collection<Attachment> pAttachments) {
		super();
		this.remote_id = remoteId;
		this.lastModified = lastModified;
		this.observationGeometry = observationGeometry;
		this.properties = pProperties;
		this.attachments = pAttachments;
		this.setDirty(false);
	}

	public Long getPk_id() {
		return pk_id;
	}

	public String getRemote_id() {
		return remote_id;
	}

	public void setRemote_id(String remote_id) {
		this.remote_id = remote_id;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public ObservationGeometry getObservationGeometry() {
		return observationGeometry;
	}

	public void setObservationGeometry(ObservationGeometry observationGeometry) {
		this.observationGeometry = observationGeometry;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLastModified() {
		return lastModified;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Collection<ObservationProperty> getProperties() {
		return properties;
	}

	public void setProperties(Collection<ObservationProperty> properties) {
		this.properties = properties;
	}

	public Collection<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * A convenience method used for returning an Observation's properties in a
	 * more useful data-structure.
	 * 
	 * @return
	 */
	public Map<String, String> getPropertiesMap() {

		Map<String, String> propertiesMap = new HashMap<String, String>();

		if (properties != null) {
			for (ObservationProperty property : properties) {
				propertiesMap.put(property.getKey(), property.getValue());
			}
		}

		return propertiesMap;
	}

	/**
	 * A convenience method used for setting an Observation's properties with a
	 * Map (instead of a Collection).
	 * 
	 * @param propertiesMap
	 *            A Map of ALL the properties to be set.
	 */
	public void setPropertiesMap(Map<String, String> propertiesMap) {
		Collection<ObservationProperty> properties = new ArrayList<ObservationProperty>();

		if (propertiesMap != null) {
			for (String key : propertiesMap.keySet()) {
				properties.add(new ObservationProperty(key, propertiesMap.get(key)));
			}
		}

		setProperties(properties);
	}

	@Override
	public String toString() {
		return "Observation [pk_id=" + pk_id + ", remote_id=" + remote_id + ", state=" + state + ", observationGeometry=" + observationGeometry + ", properties=" + properties + ", attachments=" + attachments + "]";
	}

}