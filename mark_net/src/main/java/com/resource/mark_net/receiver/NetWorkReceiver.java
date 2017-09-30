package com.resource.mark_net.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.resource.mark_net.utils.HttpNetUtil;

public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        HttpNetUtil.INSTANCE.setConnected(context);
    }
}
