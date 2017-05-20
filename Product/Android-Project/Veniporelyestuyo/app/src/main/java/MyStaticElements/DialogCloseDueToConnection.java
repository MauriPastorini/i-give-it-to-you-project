package MyStaticElements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Mauri on 20-May-17.
 */

public class DialogCloseDueToConnection {
    public void ShowCloseDialog(String title, String message, final Context mycontext) {
        AlertDialog alertDialog = new AlertDialog.Builder(mycontext).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity)mycontext).finishAffinity();
                    }
                });
        alertDialog.show();
    }
}
