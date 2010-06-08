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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class RadioStateWidgetConfigure extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        setVisible(false);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.config, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                RadioStateWidgetConfigure.this.finish();
            }
        });
        builder.setPositiveButton(R.string.save, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                configureWidget();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configureWidget() {
        // TODO Auto-generated method stub
    }
}
