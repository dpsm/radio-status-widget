/*
 * This work is licensed under the Creative Commons Attribution-Noncommercial 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc/3.0/us/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
 * Copyright David Marques - dpsmarques@gmail.com
 */

package org.dpsmarques.android.radiostatuswidget;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

public class RadioStatusWidgetService extends Service {

    public static final String CONFIGURATION_ACTION_ADD = "CONFIGURATION_ACTION_ADD";
    public static final String CONFIGURATION_ACTION_DEL = "CONFIGURATION_ACTION_DEL";
    public static final String CONFIGURATION_EXTRA  = "CONFIGURATION_EXTRA";

    private static final int PHONE_STATES = PhoneStateListener.LISTEN_CALL_STATE
        | PhoneStateListener.LISTEN_SERVICE_STATE | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;
    private static final CharSequence FORMAT = "kk:mm:ss AA";

    private TelephonyManager    mTelephonyManager;
    private PhoneStateListener  mPhoneStateListener;
    private WidgetUpdateHandler mPhoneStateHandler;

    private Hashtable<Integer, Integer> mRemoteWidgets;

    @Override
    public void onCreate() {
        super.onCreate();
        mRemoteWidgets      = new Hashtable<Integer, Integer>();
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
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(CONFIGURATION_ACTION_ADD)) {
                int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, Integer.MIN_VALUE);
                int config   = intent.getIntExtra(CONFIGURATION_EXTRA, Integer.MIN_VALUE);
                addWidgetToCache(widgetId, config);
            } else
            if (action.equals(CONFIGURATION_ACTION_DEL)) {
                int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
                removeWidgetsFromCache(widgetIds);
            }
        } else {
            mPhoneStateHandler.sendMessage(Message.obtain());
        }
    }

    private void addWidgetToCache(int widgetId, int config) {
        if (widgetId != Integer.MIN_VALUE && config != Integer.MIN_VALUE) {
            mRemoteWidgets.put(widgetId, config);
        }
    }

    private void removeWidgetsFromCache(int[] widgetIds) {
        if (widgetIds != null) {
            for (int i = 0; i < widgetIds.length; i++) {
                mRemoteWidgets.remove(widgetIds[i]);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    class WidgetUpdateHandler extends Handler {

        private Context            mContext;
        private WidgetUpdateHolder mHolder;

        public WidgetUpdateHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            WidgetUpdateHolder holder = (WidgetUpdateHolder) msg.obj;
            if (holder != null) {
                mHolder = holder;
            }

            LinkedList<Integer> widgetsToUpdate = new LinkedList<Integer>();

            Hashtable<Integer, Integer> remoteWidgets = mRemoteWidgets;
            Enumeration<Integer> widgetIds = remoteWidgets.keys();
            while (widgetIds.hasMoreElements()) {
                Integer widgetId = widgetIds.nextElement();
                if ((remoteWidgets.get(widgetId) & mHolder.statusType) != 0x00) {
                    widgetsToUpdate.add(widgetId);
                }
            }
            updateRemoteWidgets(widgetsToUpdate);
        }

        private void updateRemoteWidgets(LinkedList<Integer> widgetsToUpdate) {
            if (widgetsToUpdate.size() > 0) {
                RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);
                if (mHolder != null) {
                    updateViews.setTextViewText(R.id.status_text, mHolder.statusText);

                    String statusTime = "Updated: " + DateFormat.format(FORMAT, mHolder.statusTime);
                    updateViews.setTextViewText(R.id.update_time, statusTime);
                    updateViews.setImageViewResource(R.id.status_icon, mHolder.statusIcon);
                } else {
                    updateViews.setTextViewText(R.id.status_text, mContext.getString(R.string.no_status));
                    updateViews.setTextViewText(R.id.update_time, mContext.getString(R.string.empty));
                    updateViews.setImageViewResource(R.id.status_icon, R.drawable.ic_power);
                }

                AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
                for (Integer integer : widgetsToUpdate) {
                    manager.updateAppWidget(integer, updateViews);
                }
            }
        }


    }

    static class WidgetUpdateHolder {
        int    statusType;
        String statusText;
        int    statusIcon;
        long   statusTime;
    }
}
