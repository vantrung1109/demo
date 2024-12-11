package digi.kitplay.data;

import digi.kitplay.data.local.prefs.PreferencesService;
import digi.kitplay.data.local.sqlite.DbService;
import digi.kitplay.data.remote.ApiService;

import javax.inject.Inject;

public class AppRepository implements Repository {

    private final ApiService mApiService;
    private final DbService mDbService;
    private final PreferencesService mPreferencesHelper;

    @Inject
    public AppRepository(PreferencesService preferencesHelper, ApiService apiService, DbService mDbService) {
        this.mPreferencesHelper = preferencesHelper;
        this.mApiService = apiService;
        this.mDbService = mDbService;
    }

    /**
     * ################################## Preference section ##################################
     */
    @Override
    public String getToken() {
        return mPreferencesHelper.getToken();
    }

    @Override
    public void setToken(String token) {
        mPreferencesHelper.setToken(token);
    }

    @Override
    public String getSsid() {
        return mPreferencesHelper.getSsid();
    }

    @Override
    public String getWifiPassword() {
        return mPreferencesHelper.getWifiPassword();
    }

    @Override
    public String getPort() {
        return mPreferencesHelper.getPort();
    }

    @Override
    public String getIp() {
        return mPreferencesHelper.getIp();
    }

    @Override
    public void setSsid(String ssid) {
        mPreferencesHelper.setSsid(ssid);
    }

    @Override
    public void setWifiPassword(String password) {
        mPreferencesHelper.setWifiPassword(password);
    }

    @Override
    public void setPort(String port) {
        mPreferencesHelper.setPort(port);
    }

    @Override
    public void setIp(String ip) {
        mPreferencesHelper.setIp(ip);
    }

    @Override
    public PreferencesService getSharedPreferences(){
        return mPreferencesHelper;
    }



    /**
    *  ################################## Remote api ##################################
    */
    @Override
    public ApiService getApiService(){
        return mApiService;
    }

    @Override
    public DbService getSqLiteService() {
        return mDbService;
    }

}
