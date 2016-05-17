package com.stockita.newpointofsales.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.service.media.MediaBrowserService;
import android.widget.RemoteViews;

import com.stockita.newpointofsales.R;

/**
 * This class is the app widget provider
 */
public class ListAppWidget extends AppWidgetProvider {



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        /* Update each widget created by this provider */
        for (int i=0; i < appWidgetIds.length; i++) {
                        /*
            Set up the intent that starts the background service, which will
            provide the views for this collection.
            */
            Intent intent = new Intent(context, ListWidgetService.class);
            /* Add the app widget ID to the intent extras. */
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


            /* Instantiate the RemoteViews object for the app widget layout. */
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_widget_layout);

            /* Set the title view based on the widget configuration*/
            SharedPreferences pref = context.getSharedPreferences(String.valueOf(appWidgetIds[i]),
                    Context.MODE_PRIVATE);



            /*
            Set up the RemoteViews object to use a RemoteViews adapter.
            This adapter connects
            to a RemoteViewsService  through the specified intent.
            This is how you populate the data.
            */
            views.setRemoteAdapter(appWidgetIds[i], R.id.lists, intent);

            /*
            The empty view is displayed when the collection has no items.
            It should be in the same layout used to instantiate the RemoteViews
            object above.
            */
            views.setEmptyView(R.id.list, R.id.list_empty);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);

        }

    }

    /**
     * Called when the first widget is added to the provider
     */
    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, DataObserverService.class));
    }

    /**
     * Called when the all widgets have been removed from this provider
     */
    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, DataObserverService.class));
    }

    /**
     * Called when one or more widgets attached to this provider are removed
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            context.getSharedPreferences(String.valueOf(appWidgetId),
                    Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
        }
    }
}
