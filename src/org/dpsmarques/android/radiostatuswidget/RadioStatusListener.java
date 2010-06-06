/*
 * This work is licensed under the Creative Commons Attribution-Noncommercial 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc/3.0/us/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
 * Copyright David Marques - dpsmarques@gmail.com
 */
package org.dpsmarques.android.radiostatuswidget;

import org.dpsmarques.android.radiostatuswidget.RadioStatusWidgetService.WidgetUpdateHolder;

import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

public class RadioStatusListener extends PhoneStateListener {

    private Handler mHandler;

    public RadioStatusListener(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onCallStateChanged(int state, String notUsed) {
        WidgetUpdateHolder holder = new WidgetUpdateHolder();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                holder.statusText = "CALL_STATE_RINGING";
            break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                holder.statusText = "CALL_STATE_OFFHOOK";
            break;
            case TelephonyManager.CALL_STATE_IDLE:
                holder.statusText = "CALL_STATE_IDLE";
            break;
        }
        holder.statusIcon = R.drawable.ic_calls;
        dispatchUpdate(holder);
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        WidgetUpdateHolder holder = new WidgetUpdateHolder();
        switch (state) {
            case TelephonyManager.DATA_CONNECTED:
                holder.statusText = "DATA_CONNECTED";
            break;
            case TelephonyManager.DATA_CONNECTING:
                holder.statusText = "DATA_CONNECTING";
            break;
            case TelephonyManager.DATA_DISCONNECTED:
                holder.statusText = "DATA_DISCONNECTED";
            break;
            case TelephonyManager.DATA_SUSPENDED:
                holder.statusText = "DATA_SUSPENDED";
            break;
        }
        holder.statusIcon = R.drawable.ic_data;
        dispatchUpdate(holder);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        WidgetUpdateHolder holder = new WidgetUpdateHolder();
        switch (serviceState.getState()) {
            case ServiceState.STATE_EMERGENCY_ONLY:
                holder.statusText = "STATE_EMERGENCY_ONLY";
            break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                holder.statusText = "STATE_OUT_OF_SERVICE";
            break;
            case ServiceState.STATE_POWER_OFF:
                holder.statusText = "STATE_POWER_OFF";
            break;
            case ServiceState.STATE_IN_SERVICE:
                holder.statusText = "STATE_IN_SERVICE";
            break;
        }
        holder.statusIcon = R.drawable.ic_cell;
        dispatchUpdate(holder);
    }

    private void dispatchUpdate(WidgetUpdateHolder holder) {
        holder.statusTime = System.currentTimeMillis();
        mHandler.obtainMessage(0x00, holder).sendToTarget();
    }
}
