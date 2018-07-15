package ac0888bfc.chatcat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Storage {

    @Nullable
    static public SharedPreferences getLoginPrefs(@NonNull Context ctx) {
        return ctx.getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    static public int getSavedId(@NonNull SharedPreferences pref) {
        return pref.getInt("name", -1);
    }

    @Nullable
    static public String getSavedPassword(@NonNull SharedPreferences pref) {
        return pref.getString("pass", null);
    }

    @Nullable
    static public String getSavedServer(@NonNull SharedPreferences pref) {
        return pref.getString("server", null);
    }

    static public int getSavedPort(@NonNull SharedPreferences pref) {
        return pref.getInt("port", -1);
    }

    static public SharedPreferences.Editor putServer(SharedPreferences.Editor editor, String server) {
        editor.putString("server", server);
        return editor;
    }

    static public SharedPreferences.Editor putPort(SharedPreferences.Editor editor, int port) {
        editor.putInt("port", port);
        return editor;
    }

    static public SharedPreferences.Editor putId(SharedPreferences.Editor editor, int id) {
        editor.putInt("name", id);
        return editor;
    }

    static public SharedPreferences.Editor putPassword(SharedPreferences.Editor editor, String password) {
        editor.putString("pass", password);
        return editor;
    }

}
