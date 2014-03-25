package mil.nga.giat.mage.sdk.datastore.observation;

import mil.nga.giat.mage.sdk.datastore.common.Geometry;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "observation_geometries")
public class ObservationGeometry {

	@DatabaseField(generatedId = true)
	private Long pk_id;

	@DatabaseField(canBeNull = false, dataType=DataType.SERIALIZABLE)
	private Geometry geometry;

	public ObservationGeometry() {
		// ORMLite needs a no-arg constructor
	}

	public ObservationGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Long getPk_id() {
		return pk_id;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public String toString() {
		return "ObservationGeometry [pk_id=" + pk_id + ", geometry=" + getGeometry() + "]";
	}
}