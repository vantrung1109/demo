package digi.kitplay.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import digi.kitplay.constant.Constants;
import digi.kitplay.di.qualifier.PreferenceInfo;
import digi.kitplay.utils.LogService;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import javax.inject.Inject;

public class AppPreferencesService implements PreferencesService {

    private final SharedPreferences mPrefs;
    private final Gson gson;

    @Inject
    public AppPreferencesService(Context context, @PreferenceInfo String prefFileName, Gson gson) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        this.gson = gson;
    }

    @Override
    public String getSsid() {
        return mPrefs.getString(SSID, "");
    }

    @Override
    public String getWifiPassword() {
        return mPrefs.getString(WIFI_PASSWORD, "");
    }

    @Override
    public String getPort() {
        return mPrefs.getString(PORT, "");
    }

    @Override
    public String getIp() {
        return mPrefs.getString(IP, "");
    }

    @Override
    public String getRabbitmqPort() {
        return mPrefs.getString(RABBITMQ_PORT, "");
    }

    @Override
    public String getRabbitmqUsername() {
        return mPrefs.getString(RABBITMQ_USERNAME, "");
    }

    @Override
    public String getRabbitmqPassword() {
        return mPrefs.getString(RABBITMQ_PASSWORD, "");
    }

    @Override
    public void setSsid(String ssid) {
        mPrefs.edit().putString(SSID, ssid).apply();
    }

    @Override
    public void setWifiPassword(String password) {
        mPrefs.edit().putString(WIFI_PASSWORD, password).apply();
    }

    @Override
    public void setPort(String port) {
        mPrefs.edit().putString(PORT, port).apply();
    }

    @Override
    public void setIp(String ip) {
        mPrefs.edit().putString(IP, ip).apply();
    }

    @Override
    public void setRabbitmqUsername(String username) {
        mPrefs.edit().putString(RABBITMQ_USERNAME, username).apply();
    }

    @Override
    public void setRabbitmqPassword(String password) {
        mPrefs.edit().putString(RABBITMQ_PASSWORD, password).apply();
    }

    @Override
    public void setRabbitmqPort(String port) {
        mPrefs.edit().putString(RABBITMQ_PORT, port).apply();
    }


    @Override
    public String getToken() {
        return mPrefs.getString(KEY_BEARER_TOKEN, Constants.VALUE_BEARER_TOKEN_DEFAULT);
    }

    @Override
    public void setToken(String token) {
        mPrefs.edit().putString(KEY_BEARER_TOKEN, token).apply();
    }

    @Override
    public void removeKey(String key) {
        mPrefs.edit().remove(key).apply();
    }

    @Override
    public void removeAllKeys() {
        mPrefs.edit().clear().apply();
    }

    @Override
    public boolean containKey(String key) {
        return mPrefs.contains(key);
    }

    @Override
    public void registerChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void setBoolean(String key, boolean val){
        mPrefs.edit().putBoolean(key, val).apply();
    }
    @Override
    public boolean getBooleanVal(String key) {
        return mPrefs.getBoolean(key, false);
    }

    @Override
    public void setString(String key, String val){
        mPrefs.edit().putString(key, val).apply();
    }
    @Override
    public String getStringVal(String key) {
        return mPrefs.getString(key, null);
    }

    @Override
    public void setInt(String key, int val){
        mPrefs.edit().putInt(key, val).apply();
    }
    @Override
    public int getIntVal(String key) {
        return mPrefs.getInt(key, 0);
    }

    @Override
    public void setLong(String key, long val){
        mPrefs.edit().putLong(key, val).apply();
    }
    @Override
    public long getLongVal(String key) {
        return mPrefs.getLong(key, 0);
    }

    @Override
    public void setFloat(String key, float val){
        mPrefs.edit().putFloat(key, val).apply();
    }
    @Override
    public float getFloatVal(String key) {
        return mPrefs.getFloat(key, 0);
    }

    @Override
    public <T> T getObjectVal(String key, Class<T> mModelClass) {
        Object object = null;
        try {
            object = gson.fromJson(mPrefs.getString(key, ""), mModelClass);
        } catch (Exception ex) {
            LogService.e(ex);
        }
        return Primitives.wrap(mModelClass).cast(object);
    }

    @Override
    public void setMyPrinterId(Integer printerId) {
        mPrefs.edit().putInt(PRINTER_ID, printerId).apply();
    }

    @Override
    public Integer getMyPrinterId() {
        return mPrefs.getInt(PRINTER_ID,0);
    }
}
