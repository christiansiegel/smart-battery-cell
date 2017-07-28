package gateway.comm.web;

import gateway.config.Config;
import gateway.config.ObjectManager;

/**
 * Handles communication between gateway and web.
 */
public interface WebClientInterface extends Runnable {
	public void initialize(ObjectManager objectManager, Config config);	
}
