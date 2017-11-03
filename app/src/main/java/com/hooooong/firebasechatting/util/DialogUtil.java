package com.hooooong.firebasechatting.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Android Hong on 2017-11-03.
 */

public class DialogUtil {

    public static void showDialogPopUp(String title, String msg, final Activity activity, final boolean activitiyFinish){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false) //false면 버튼을 단다는 것(다른곳을 눌러도 사라지지 않는다.), true면 반대
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(activitiyFinish){
                            activity.finish();
                        }
                    }
                });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


}
