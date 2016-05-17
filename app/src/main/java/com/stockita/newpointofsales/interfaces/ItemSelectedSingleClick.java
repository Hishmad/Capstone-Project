package com.stockita.newpointofsales.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * This is an interface for callbacks method to pass data from the
 * Adapter to the activity,  we are using this for two pane mode.
 */
public interface ItemSelectedSingleClick {

    void setSingleClick(View view, int position, Intent intent);
}
