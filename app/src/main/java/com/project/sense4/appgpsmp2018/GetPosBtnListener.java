package com.project.sense4.appgpsmp2018;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class GetPosBtnListener implements View.OnClickListener {
    GPSActivity locationActivity;
    Context context;
    EditText latitudine;
    EditText longitudine;

    public GetPosBtnListener(EditText latitudine,EditText longitudine){
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    @Override
    public void onClick(View view) {
        latitudine.setText(null);
        longitudine.setText(null);
        //to get the context and location activity
        this.context = view.getContext();
        this.locationActivity = (GPSActivity) context;
        //start listening to location updates
        locationActivity.getLocation(context);
    }
}
