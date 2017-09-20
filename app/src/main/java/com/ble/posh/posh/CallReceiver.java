package com.ble.posh.posh;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ble.posh.posh.ble.DfuServiceInitiator;

import java.lang.reflect.Method;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("P_CALL","onReceive " + intent.getAction().toString());
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                //sendSMS(phoneNumber, "Hi You got a message!");
                Log.d("P_CALL","incoming Call " + phoneNumber);
                final boolean keepBond = true;
                final boolean forceDfu = false;
                final boolean enablePRNs = true;
                String value = "12";
                int numberOfPackets = 12;

                final DfuServiceInitiator starter = new DfuServiceInitiator("F0:6A:57:4B:67:2C")
                            .setDeviceName("Boots")
                            .setKeepBond(keepBond)
                            .setForceDfu(forceDfu)
                            .setPacketsReceiptNotificationsEnabled(enablePRNs)
                            .setPacketsReceiptNotificationsValue(numberOfPackets)
                            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                            .setCmdOrFile(true)
                            .setFileName("1.jpg")
                            .setCmd_op(1);

                    Log.d("BOOT", " send command ");
                    starter.start(context, DfuService.class);

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                if (incomingCall) {
                    Log.d("P_CALL","talk");
                    incomingCall = false;
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
                if (incomingCall) {
                    Log.d("P_CALL","hang");
                    incomingCall = false;
                }
            }
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private boolean disconnectCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d("CALL","PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

}
