package digi.kitplay.data;

import digi.kitplay.data.local.prefs.PreferencesService;
import digi.kitplay.data.local.sqlite.DbService;
import digi.kitplay.data.remote.ApiService;


public interface Repository {

    /**
     * ################################## Preference section ##################################
     */
    String getToken();
    void setToken(String token);
    String getSsid();
    String getWifiPassword();
    String getPort();
    String getIp();

    void setSsid(String ssid);
    void setWifiPassword(String password);
    void setPort(String port);
    void setIp(String ip);
    PreferencesService getSharedPreferences();


    /**
     *  ################################## Remote api ##################################
     */
    ApiService getApiService();
    DbService getSqLiteService();

}
