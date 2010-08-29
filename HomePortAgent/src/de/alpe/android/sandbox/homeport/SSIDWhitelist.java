
package de.alpe.android.sandbox.homeport;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author ap
 *
 */
public class SSIDWhitelist {
	
	private static Collection<String> ssid_whitelist = Arrays.asList("foobar");


	public static boolean contains(String ssid){
		return ssid_whitelist.contains(ssid);
	}

}
