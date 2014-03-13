package mil.nga.giat.mage.sdk.database.orm;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="observations")
public class Observation {

    @DatabaseField(generatedId = true)
    private Long pk_id;
    
    @DatabaseField
    private String remote_id;

    @DatabaseField(canBeNull = false,foreign = true)
    private State state;
    
    @DatabaseField(canBeNull = false,foreign = true)
    private Geometry geometry;
    
    @ForeignCollectionField
    Collection<Property> properties;
    
    @ForeignCollectionField
    Collection<Attachment> attachments;
    
	public Observation() {
        // ORMLite needs a no-arg constructor 
    }
    public Observation(String pRemoteId, State pState, Geometry pGeometry, Collection<Property> pProperties,Collection<Attachment> pAttachments) {        
        this.remote_id = pRemoteId;
        this.state = pState;
        this.geometry = pGeometry;
        this.properties = pProperties;
        this.attachments = pAttachments;
    }

    public Long getPk_id() {
		return pk_id;
	}
	public void setPk_id(Long pk_id) {
		this.pk_id = pk_id;
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

	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Collection<Property> getProperties() {
		return properties;
	}
	public void setProperties(Collection<Property> properties) {
		this.properties = properties;
	}

	public Collection<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(Collection<Attachment> attachments) {
		this.attachments = attachments;
	}
	
}