package info.bati11.mywearapplication.models;

import android.content.Context;
import android.content.SharedPreferences;

public class PassedCount {
    public static final String FORMAT = "MM/dd";
    private static final String PATH = "passedCount";
    private static final String KEY_COUNT = "keyCount";
    private static final String KEY_LABEL = "keyLabel";

    public static void save(Context context, PassedCount passedCount) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PATH, context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_COUNT, passedCount.getCount()).apply();
        sharedPreferences.edit().putString(KEY_LABEL, passedCount.getLabel()).apply();
    }

    public static PassedCount get(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PATH, context.MODE_PRIVATE);
        return new PassedCount(
            sharedPreferences.getInt(KEY_COUNT, 0),
            sharedPreferences.getString(KEY_LABEL, "")
        );
    }

    private int count;
    private String label;

    public PassedCount(int count, String label) {
        this.count = count;
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public String getLabel() {
        return label;
    }
}
