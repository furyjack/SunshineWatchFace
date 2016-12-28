package wear;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceManager {
    Context context;
     SharedPreferences sharedPreferences;
     SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("pref", 0);
        editor = sharedPreferences.edit();
        this.context = context;
    }

    public  void setPrefBool(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public  void setPrefString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public  void setPrefInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getPrefBool(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public  String getPrefString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public  int getPrefInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public  void deleteAll() {
        editor.clear();
        editor.commit();
    }
}
