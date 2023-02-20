package kr.co.nicevan.nvcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.co.nicevan.nvcat.activity.MainActivity;

public class Starter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}