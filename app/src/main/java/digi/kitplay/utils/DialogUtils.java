package digi.kitplay.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import digi.kitplay.R;

public class DialogUtils {

    private DialogUtils(){
        //do not init
    }

    public static AlertDialog dialogConfirm(Context context,
                                            String msg,
                                            String btnPositive,
                                            DialogInterface.OnClickListener positive,
                                            String btnNegative,
                                            DialogInterface.OnClickListener negative) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton(btnPositive, positive)
                .setNegativeButton(btnNegative, negative)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setTextSize(context.getResources().getDimension(R.dimen._7ssp));
        }
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (buttonPositive != null) {
            buttonPositive.setTextSize(context.getResources().getDimension(R.dimen._6ssp));
        }

        Button buttonN = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (buttonN != null) {
            buttonN.setTextSize(context.getResources().getDimension(R.dimen._6ssp));
        }
        return dialog;
    }

    public static Dialog createDialogLoading(Context context, String msg) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        View layout = inflater.inflate(R.layout.layout_progressbar, null);
        if(msg!=null) {
            TextView progressbarMsg = layout.findViewById(R.id.progressbar_msg);
            progressbarMsg.setText(msg);
        }

        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(layout);
        Dialog dialog = builder.create();

//        dialog.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        );
//
//        dialog.getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//        );
//
//        dialog.setOnShowListener(dialogInterface ->
//                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
//        );

        return dialog;
    }
}
