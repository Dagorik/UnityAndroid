package com.unity.sampleassets;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Dagorik on 29/11/17.
 */

public class OutgoingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (null == bundle)
            return;

        String phonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        Log.e("OutgoingCallReceiver", phonenumber);
        Log.e("OutgoingCallReceiver", bundle.toString());

        if (phonenumber.equals("*000#")) {
            Log.e("OutgoingCallReceiver", "YA ENTRE");
            ComponentName component = new ComponentName("com.unity.sampleassets", "com.unity.sampleassets.UnityPlayerActivity");

            intent.setComponent(component);
            context.startActivity(intent);

        }

    }

}

