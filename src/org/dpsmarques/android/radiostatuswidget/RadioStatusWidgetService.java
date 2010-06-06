/*
 * This work is licensed under the Creative Commons Attribution-Noncommercial 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc/3.0/us/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
 * Copyright David Marques - dpsmarques@gmail.com
 */

package org.dpsmarques.android.radiostatuswidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

public class RadioStatusWidgetService extends Service {

    private static final int PHONE_STATES = PhoneStateListener.LISTEN_CALL_STATE
        | PhoneStateListener.LISTEN_SERVICE_STATE | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;

    private TelephonyManager    mTelephonyManager;
    private PhoneStateListener  mPhoneStateListener;
    private WidgetUpdateHandler mPhoneStateHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager   = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateHandler  = new WidgetUpdateHandler(this);
        mPhoneStateListener = new RadioStatusListener(mPhoneStateHandler);
        mTelephonyManager.listen(mPhoneStateListener, PHONE_STATES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        mPhoneStateHandler.sendMessage(Message.obtain());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    class WidgetUpdateHandler extends Handler {

        private ComponentName      mComponentName;
        private Context            mContext;
        private WidgetUpdateHolder mHolder;

        public WidgetUpdateHandler(Context context) {
            mContext = context;
            mComponentName = new ComponentName(mContext, RadioStatusWidgetProvider.class);
        }

        @Override
        public void handleMessage(Message msg) {
            WidgetUpdateHolder holder = (WidgetUpdateHolder) msg.obj;
            if (holder != null) {
                mHolder = holder;
            }
            RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);
            if (mHolder != null) {
                updateViews.setTextViewText(R.id.status_text, mHolder.statusText);
                updateViews.setImageViewResource(R.id.status_icon, mHolder.statusIcon);
            } else {
                updateViews.setTextViewText(R.id.status_text, mContext.getString(R.string.no_status));
                updateViews.setImageViewResource(R.id.status_icon, R.drawable.ic_power);
            }

            AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
            manager.updateAppWidget(mComponentName, updateViews);
        }
    }

    static class WidgetUpdateHolder {
        String statusText;
        int    statusIcon;
    }
}
