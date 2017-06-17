package MyStaticElements;

import android.util.Log;

/**
 * Created by Mauri on 03-Jun-17.
 */

public class LogRegistration {
    private static final String TAG = "myLogMessageTag";

    public enum TypeLog{
        STEP,EXCEPTION, ERROR
    }

    public static void log(TypeLog typeLog , String message){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LogType: ");
        stringBuilder.append(typeLog);
        stringBuilder.append(". LogMsg: ");
        stringBuilder.append(message);
        Log.i(TAG, stringBuilder.toString());
    }

}
