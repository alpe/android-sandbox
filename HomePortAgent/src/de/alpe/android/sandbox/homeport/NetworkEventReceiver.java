package de.alpe.android.sandbox.homeport;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * @author ap
 * 
 */
public class NetworkEventReceiver extends BroadcastReceiver {

	private static final String APP_HOME_PORT = "homePort";

	private static KeyguardManager.KeyguardLock lock;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			handleNetworkEvent(context, intent);
		} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			handleStateEvent(context, intent);
		}

	}

	private void handleStateEvent(Context context, Intent intent) {
		Log.d(APP_HOME_PORT, "received wifi state change event");
		int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
				WifiManager.WIFI_STATE_UNKNOWN);
		if (WifiManager.WIFI_STATE_DISABLED == state) {
			lockDevice(context);
		}
	}

	private void handleNetworkEvent(Context context, Intent intent) {
		Log.d(APP_HOME_PORT, "received network event");
		NetworkInfo networkInfos = (NetworkInfo) intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (networkInfos != null) {
			handleNetworkEvent(context, networkInfos);
		}
	}

	private void handleNetworkEvent(Context context,
			NetworkInfo networkInfos) {
		switch (networkInfos.getState()) {
		case CONNECTED:
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (SSIDWhitelist.contains(wifiManager.getConnectionInfo()
					.getSSID())) {
				unlockDevice(context);
			}
			break;
		case DISCONNECTING:
		case DISCONNECTED:
			lockDevice(context);
			break;

		}
	}

	private void unlockDevice(Context context) {
		if (lock == null) {
			lock = fetchKeyguardLock(context);
			Log.d(APP_HOME_PORT, "disabling key guard");
			lock.disableKeyguard();
		}
	}

	private void lockDevice(Context context) {
		if (lock != null) {
			Log.d(APP_HOME_PORT, "enabling key guard");
			lock.reenableKeyguard();
			lock = null;
		}
	}


	private KeyguardManager.KeyguardLock fetchKeyguardLock(Context context) {
		KeyguardManager keyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock lock = keyguardManager
				.newKeyguardLock(APP_HOME_PORT);
		return lock;
	}
}
