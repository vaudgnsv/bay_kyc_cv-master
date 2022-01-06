package com.thaivan.bay.branch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class Preference {

    public static final String KEY_TOKEN = Preference.class.getName() + "token";

    public static final String KEY_SERIAL_NUMBER = Preference.class.getName() + "sn";
    public static final String KEY_SEGMENT = Preference.class.getName() + "segment";
    public static final String KEY_TERMINAL_ID = Preference.class.getName() + "tid";
    public static final String KEY_MERCHANT_ID = Preference.class.getName() + "mid";
    public static final String KEY_MERCHANTNAME_ID = Preference.class.getName() + "merchant_name";
    public static final String KEY_IP = Preference.class.getName() + "ip";
    public static final String KEY_PORT = Preference.class.getName() + "port";
    public static final String KEY_IP2 = Preference.class.getName() + "ip2";
    public static final String KEY_PORT2 = Preference.class.getName() + "port2";
    public static final String KEY_JSON_VERSION = Preference.class.getName() + "json_version";
    public static final String KEY_ADMIN_PIN = Preference.class.getName() + "pin";
//    public static final String KEY_WIFI_NAME = Preference.class.getName() + "wifi_name";
//    public static final String KEY_SIM_SN = Preference.class.getName() + "sim_sn";

    public static final String POST_CODE = Preference.class.getName() + "post_code";
    public static final String TEMP_POST_CODE = Preference.class.getName() + "temp_post_code";

    private static Preference settingPreference = null;
    private final String preferenceName = BuildConfig.APPLICATION_ID + "Setting";
    private SharedPreferences preference = null;
    private Editor editor = null;
    private Context context = null;

    /**
     * Constructor method
     *
     * @param context
     */
    public Preference(Context context) {
        this.context = context;
        int mode = Context.MODE_PRIVATE;
        this.preference = this.context.getSharedPreferences(this.preferenceName, mode);
        this.editor = this.preference.edit();
    }

    /**
     * Factory method
     *
     * @param context
     * @return
     */
    public static Preference getInstance(Context context) {
        if (settingPreference == null) {
            settingPreference = new Preference(context);
        }
        return settingPreference;
    }

    public void setValueString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String getValueString(String key) {
        return this.preference.getString(key, "");
    }

    public String getValueString(String key, String defaultValue) {
        return this.preference.getString(key, defaultValue);
    }

    public void setValueInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    public int getValueInt(String key, int defaultValue) {
        return this.preference.getInt(key, defaultValue);
    }


    public int getValueInt(String key) {
        return this.preference.getInt(key, 0);
    }

    public void setValueLong(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    public long getValueLong(String key, long defaultValue) {
        return this.preference.getLong(key, defaultValue);
    }

    public long getValueLong(String key) {
        return this.preference.getLong(key, 0);
    }

    public void setValueDouble(String key, double value) {
        this.editor.putFloat(key, (float) value);
        this.editor.commit();
    }

    public double getValueDouble(String key, float defaultValue) {
        float value = this.preference.getFloat(key, defaultValue);
        return value;
    }

    public double getValueDouble(String key) {
        float value = this.preference.getFloat(key, 0.0f);
        return value;
    }

    public void setValueFloat(String key, float value) {
        this.editor.putFloat(key, value);
        this.editor.commit();
    }

    public float getValueFloat(String key, float defaultValue) {
        float value = this.preference.getFloat(key, defaultValue);
        return value;
    }

    public float getValueFloat(String key) {
        float value = this.preference.getFloat(key, 0.0f);
        return value;
    }

    public void setValueBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public boolean getValueBoolean(String key) {
        return this.preference.getBoolean(key, false);
    }

    public void clear() {
        this.editor.clear();
        this.editor.commit();
    }

    public void deleteKey(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }
}
