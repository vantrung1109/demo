package digi.kitplay.data.local.prefs;

import android.content.SharedPreferences;

public interface PreferencesService {
    String KEY_BEARER_TOKEN="KEY_BEARER_TOKEN";

    String SSID = "SSID";
    String WIFI_PASSWORD = "WIFI_PASSWORD";
    String PORT = "PORT";
    String IP = "IP";
    String RABBITMQ_PORT = "RABBITMQ_PORT";
    String RABBITMQ_USERNAME = "RABBITMQ_USERNAME";
    String RABBITMQ_PASSWORD = "RABBITMQ_PASSWORD";
    String PRINTER_ID = "PRINTER_ID";
    String getSsid();
    String getWifiPassword();
    String getPort();
    String getIp();
    String getRabbitmqPort();
    String getRabbitmqUsername();
    String getRabbitmqPassword();

    void setSsid(String ssid);
    void setWifiPassword(String password);
    void setPort(String port);
    void setIp(String ip);
    void setRabbitmqUsername(String username);
    void setRabbitmqPassword(String password);
    void setRabbitmqPort(String port);

    String getToken();
    void setToken(String token);

    void removeKey(String key);
    void removeAllKeys();
    boolean containKey(String key);
    void registerChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);
    void unregisterChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    void setBoolean(String key, boolean val);
    boolean getBooleanVal(String key);

    void setString(String key, String val);
    String getStringVal(String key);

    void setInt(String key, int val);
    int getIntVal(String key);

    void setLong(String key, long val);
    long getLongVal(String key);

    void setFloat(String key, float val);
    float getFloatVal(String key);

    <T> T getObjectVal(String key, Class<T> mModelClass);
    void setMyPrinterId(Integer printerId);
    Integer getMyPrinterId();
}
