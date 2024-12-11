package digi.kitplay.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MODIFY_PHONE_STATE;
import static android.content.Context.WIFI_SERVICE;

import digi.kitplay.ui.main.session.UserSession;


public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
    public static boolean checkNetworkConnected(Context context){
        boolean result = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                result = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Ket qua check network: "+result);
        return result;
    }
    public static void resetNetwork(Context context){
        try {

            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(checkNetworkConnected(context)){
                wifiManager.disconnect();
                //wifiManager.reconnect();
                List<WifiConfiguration> listOld = wifiManager.getConfiguredNetworks();
                //System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", scan size: "+listOld.size());
                if(listOld!=null && UserSession.CURRENT_SSID !=null){
                    for(WifiConfiguration item : listOld){
                        System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", item: "+item.SSID);
                        if(item.SSID.trim().equals("\""+UserSession.CURRENT_SSID.trim()+"\"")){
                            boolean connect = wifiManager.enableNetwork(item.networkId, true);
                            System.out.println("Ket noi vao =================>ssid"+item.SSID+": "+connect);
                            wifiManager.reconnect();
                            break;
                        }
                    }
                }
            }else{
                checkNetworkStatus(context);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void checkNetworkStatus(Context context){
        try {
            //kiem tra on hay off
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(true);
                if(!checkNetworkConnected(context)){
                    //wifiManager.disconnect();
                    List<WifiConfiguration> listOld = wifiManager.getConfiguredNetworks();
                    System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", scan size: "+listOld.size());
                    if(listOld!=null && UserSession.CURRENT_SSID!=null){
                        for(WifiConfiguration item : listOld){
                            System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", item: "+item.SSID);
                            if(item.SSID.trim().equals("\""+UserSession.CURRENT_SSID.trim()+"\"")){
                                boolean connect = wifiManager.enableNetwork(item.networkId, true);
                                System.out.println("Ket noi vao =================>ssid"+item.SSID+": "+connect);
                                wifiManager.reconnect();
                                break;
                            }
                        }
                    }

                }
            }else{
                //kiem tra danh sach wifi ra de connect
                if(!checkNetworkConnected(context)){
                    //wifiManager.disconnect();
                    List<WifiConfiguration> listOld = wifiManager.getConfiguredNetworks();
                    System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", scan size: "+listOld.size());
                    if(listOld!=null && UserSession.CURRENT_SSID!=null){
                        for(WifiConfiguration item : listOld){
                            System.out.println("Current SSID =================> "+UserSession.CURRENT_SSID +", item: "+item.SSID);
                            if(item.SSID.trim().equals("\""+UserSession.CURRENT_SSID.trim()+"\"")){
                                boolean connect = wifiManager.enableNetwork(item.networkId, true);
                                System.out.println("Ket noi vao =================>ssid"+item.SSID+": "+connect);
                                wifiManager.reconnect();
                                break;
                            }
                        }
                    }

                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void openWirelessSettings(Context context) {
        context.startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }


    @RequiresPermission(MODIFY_PHONE_STATE)
    public static boolean setMobileDataEnabled(final boolean enabled, Context context) {
        try {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.setDataEnabled(enabled);
                return false;
            }
            Method setDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setDataEnabledMethod) {
                setDataEnabledMethod.invoke(tm, enabled);
                return true;
            }
        } catch (Exception e) {
            LogService.e(e);
        }
        return false;
    }

    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean getWifiEnabled(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return false;
        }
        return manager.isWifiEnabled();
    }

    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequiresPermission(INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean checkNetworkError(Throwable throwable){
        if(throwable == null){
            return false;
        }
        return throwable instanceof IOException || throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException;
    }

}
