package acme.cb2;
import android.content.*;
import android.net.wifi.*;

import java.net.*;
import java.util.*;

import static p.IO.*;
// http://www.grokkingandroid.com/android-getting-notified-of-connectivity-changes/
// maybe register these programmatically and make an abstract base class
// not sure we need this in cb2?
// but problems with rooted fire
public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
        super();
    }
    /*
    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
for( WifiConfiguration i : list ) {
    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
         wifiManager.disconnect();
         wifiManager.enableNetwork(i.networkId, true);
         wifiManager.reconnect();

         break;
    }
 }

     */
    @Override
    public void onReceive(Context context,Intent intent) {
        if(!isEnabled)
            return;
        mContext=context;
        p("wifi receiver got action: "+intent.getAction());
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(!isChecking) {
            isChecking=true;
            tryToConnectToWifi(wifiManager); // loops - take this out somewhere
            isChecking=false;
        }
        // or put it on a guard.
    }
    void tryToConnectToWifi(WifiManager wifiManager) {
        final String tabletWifiSsid="\"tablets\"";
        boolean isWifiEnabled=wifiManager.isWifiEnabled();
        p("is wifi enabled: "+isWifiEnabled);
        List<WifiConfiguration> list=wifiManager.getConfiguredNetworks();
        if(list!=null&&list.size()>0)
            for(WifiConfiguration wifiConfiguration : list) {
                InetAddress inetAddress=null;
                p("configured: "+wifiConfiguration.SSID+", status: "+wifiConfiguration.status+": "+WifiConfiguration.Status.strings[wifiConfiguration.status]+", networkId: "+wifiConfiguration.networkId);
                if(!wifiConfiguration.SSID.equals(tabletWifiSsid))
                    p("not our network: "+wifiConfiguration.SSID);
                else
                    switch(wifiConfiguration.status) {
                        case WifiConfiguration.Status.CURRENT:
                            p("current connection is: "+tabletWifiSsid);
                            break;
                        case WifiConfiguration.Status.DISABLED:
                            p(wifiConfiguration.SSID+" is disabled, try to enable");
                            p(wifiConfiguration.SSID+" disconnecting first.");
                            boolean ok=wifiManager.disconnect();
                            if(ok) {
                                p("says we disconnected, try to enable.");
                                ok=wifiManager.enableNetwork(wifiConfiguration.networkId,true);
                                if(ok) {
                                    p(wifiConfiguration.SSID+" was enabled, trying to connect.");
                                    ok=wifiManager.reconnect();
                                    if(ok) {
                                        p(wifiConfiguration.SSID+" says is connected.");
                                    } else
                                        p(wifiConfiguration.SSID+" says was not connected!");
                                } else
                                    p(wifiConfiguration.SSID+" was not enabled!");
                            } else
                                p("says disconnect failed!");
                            break;
                        case WifiConfiguration.Status.ENABLED:
                            p(wifiConfiguration.SSID+" is enabled, trying to connect.");
                            p(wifiConfiguration.SSID+" disconnecting first.");
                            ok=wifiManager.disconnect();
                            if(ok) {
                                p("says we disconnected, try to reconnect.");
                                ok=wifiManager.reconnect();
                                if(ok) {
                                    p(wifiConfiguration.SSID+" says is connected.");
                                } else
                                    p(wifiConfiguration.SSID+" says was not connected!");
                            } else
                                p("says disconnect failed!");
                            break;
                        default:
                            p("unknown status: "+wifiConfiguration.status);
                    }
            }
        else
            p("no wifi networks are configured.");
    }
    private Context mContext;
    boolean isEnabled=false;
    boolean isChecking;

}