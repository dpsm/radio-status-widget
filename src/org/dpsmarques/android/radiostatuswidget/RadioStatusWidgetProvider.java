/*
 * This work is licensed under the Creative Commons Attribution-Noncommercial 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc/3.0/us/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
 * Copyright David Marques - dpsmarques@gmail.com
 */

package org.dpsmarques.android.radiostatuswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class RadioStatusWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent updateIntent = new Intent(context, RadioStatusWidgetService.class);
        context.startService(updateIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent disableIntent = new Intent(context, RadioStatusWidgetService.class);
        context.stopService(disableIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Intent deleteIntent = new Intent(context, RadioStatusWidgetService.class);
        deleteIntent.setAction(RadioStatusWidgetService.CONFIGURATION_ACTION_DEL);
        deleteIntent.putExtra(RadioStatusWidgetService.CONFIGURATION_EXTRA, appWidgetIds);
        context.startService(deleteIntent);
    }
}
