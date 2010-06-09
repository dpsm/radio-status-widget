/*
 * This work is licensed under the Creative Commons Attribution-Noncommercial 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc/3.0/us/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
 * Copyright David Marques - dpsmarques@gmail.com
 */
package org.dpsmarques.android.radiostatuswidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RemoteViews;

public class RadioStatusWidgetConfigure extends Activity {

    private AlertDialog mConfigurationDialog;
    private int         mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.config, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                RadioStatusWidgetConfigure.this.finish();
            }
        });
        builder.setPositiveButton(R.string.save, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                configureWidget();
            }
        });

        mConfigurationDialog = builder.create();
        mConfigurationDialog.show();
    }

    private void configureWidget() {
        int widgetConfig = PhoneStateListener.LISTEN_CALL_STATE
                         | PhoneStateListener.LISTEN_SERVICE_STATE
                         | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;

        CheckBox serviceStatusCheck = (CheckBox) mConfigurationDialog.findViewById(R.id.service_status_checkbox);
        if (!serviceStatusCheck.isChecked()) {
            widgetConfig &= ~PhoneStateListener.LISTEN_SERVICE_STATE;
        }

        CheckBox callStatusCheck    = (CheckBox) mConfigurationDialog.findViewById(R.id.call_status_checkbox);
        if (!callStatusCheck.isChecked()) {
            widgetConfig &= ~PhoneStateListener.LISTEN_CALL_STATE;
        }

        CheckBox dataStatusCheck    = (CheckBox) mConfigurationDialog.findViewById(R.id.data_status_checkbox);
        if (!dataStatusCheck.isChecked()) {
            widgetConfig &= ~PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;
        }

        Intent configIntent = new Intent(this, RadioStatusWidgetService.class);
        configIntent.setAction(RadioStatusWidgetService.CONFIGURATION_ACTION_ADD);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        configIntent.putExtra(RadioStatusWidgetService.CONFIGURATION_EXTRA, widgetConfig);
        startService(configIntent);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.main);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        finish();
    }
}
