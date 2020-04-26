package com.project.sense4.appgpsmp2018;

import android.content.Context;
import android.view.View;

public class CancelRequestBtnListener implements View.OnClickListener{

    private GPSActivity locationActivity;
    private Context context;

    @Override
    public void onClick(View view) {
        this.context = view.getContext();
        this.locationActivity = (GPSActivity) context;
        locationActivity.cancelRequest();
    }
        //annulla la ricerca del gps

}
