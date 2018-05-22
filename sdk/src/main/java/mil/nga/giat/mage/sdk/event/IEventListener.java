package mil.nga.giat.mage.sdk.event;

import java.util.EventListener;

/**
 * Part of a small event framework. Used to pass events to different parts of
 * the mdk. When locations are saved, when tokens expire, etc...
 * 
 * @author wiedemanns
 *
 * @deprecated doesn't seem to be much reason for this interface to exist
 */
@Deprecated
public interface IEventListener extends EventListener {
}
