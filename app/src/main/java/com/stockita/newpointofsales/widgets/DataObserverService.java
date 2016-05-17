package com.stockita.newpointofsales.widgets;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ContractData;

/**
 * Created by hishmadabubakaralamudi on 5/13/16.
 */
public class DataObserverService extends Service {

    private ContentObserver mContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        /* Create and register a new observer on the database */
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                /* Update all the widgets currently attached to our AppWidgetProvider */
                AppWidgetManager manager =
                        AppWidgetManager.getInstance(DataObserverService.this);
                ComponentName provider = new ComponentName(DataObserverService.this,
                        ListAppWidget.class);
                int[] appWidgetIds = manager.getAppWidgetIds(provider);
                /* This method triggers onDataSetChanged() in the RemoteViewsService */
                manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lists);

            }
        };

        /* Register the cursor */
        getContentResolver().registerContentObserver(ContractData.CategoryEntry.CONTENT_URI, true, mContentObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Unregister */
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
