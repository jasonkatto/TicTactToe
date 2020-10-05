package com.example.tictacttoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
         Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        StringBuilder str = new StringBuilder();
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str.append("SMS from ").append(msgs[i].getOriginatingAddress());
                str.append(" :");
                str.append(msgs[i].getMessageBody().toString());
                str.append("n");
            }
            //---display the new SMS message---
            Toast.makeText(context, str.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}