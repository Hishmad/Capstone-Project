package com.stockita.newpointofsales.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ContractData;

import java.util.ArrayList;

/**
 * Created by hishmadabubakaralamudi on 5/12/16.
 */
public class ListWidgetService extends RemoteViewsService {

    public static final String KEY_MODE = "mode";
    public static final String MODE_IMAGE = "image";
    public static final String MODE_VIDEO = "video";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        /* Return the data feeding RemoteViewsFactory */
        return new ListRemoteViewsFactory(
                this, intent);

    }


    /**
     * Class for data provider
     */
    private class ListRemoteViewsFactory implements RemoteViewsFactory {


        /* The ID */
        private int mAppWidgetId;

        /* Context */
        private Context mContext;

        /* Database cursor */
        private Cursor mDataCursor;


        /**
         * Constructor
         */
        public ListRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context.getApplicationContext();
            this.mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);


        }


        @Override
        public void onCreate() {

            /* Get the content resolver */
            ContentResolver contentResolver = getContentResolver();

            /* Query */
            mDataCursor = contentResolver.query(ContractData.CategoryEntry.CONTENT_URI, null, null, null, null);

            /* Make the cursor move to first */
            if (mDataCursor != null) {
                mDataCursor.moveToFirst();
            }

        }

        @Override
        public void onDataSetChanged() {

            /* Get the content resolver */
            ContentResolver contentResolver = getContentResolver();

            /* Re-query */
            mDataCursor = contentResolver.query(ContractData.CategoryEntry.CONTENT_URI, null, null, null, null);

        }

        @Override
        public void onDestroy() {

            /* Close the cursor */
            if (mDataCursor != null) {
                mDataCursor.close();
            }

        }

        @Override
        public int getCount() {
            /* Get the number of rows */
            return mDataCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            /* Move to the position */
            mDataCursor.moveToPosition(position);

            /* Initialize the RemoteViews and pass the layout xml for each line */
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.list_widget_item);

            /* Bind the data into the layout */
            remoteViews.setTextViewText(R.id.line1, mDataCursor.getString(ContractData.CategoryEntry.INDEX_COL_CATEGORY_KEY));

            /* Set the color */
            remoteViews.setTextColor(R.id.line1, Color.BLACK);

            /* Return the RemoteViews object */
            return remoteViews;

        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
