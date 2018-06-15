package mil.nga.giat.mage.sdk.event;

import mil.nga.giat.mage.sdk.datastore.layer.Layer;

public interface ILayerEventListener extends IEventListener {

	public void onLayerCreated(Layer layer);
	public void onLayerUpdated(Layer layer);

}
