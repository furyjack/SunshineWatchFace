package com.wear.android.sunshine.utilities;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class WearableDataSync extends IntentService implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mapiclient;

    public WearableDataSync() {
        super("WearableDataSync");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mapiclient=new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mapiclient.connect();

    }

    private void senddata(int high,int low) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wearData");
        putDataMapReq.getDataMap().putInt("high", high);
        putDataMapReq.getDataMap().putInt("low", low);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mapiclient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                if(!dataItemResult.getStatus().isSuccess())
                    Toast.makeText(WearableDataSync.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            int high=intent.getIntExtra("high",26);
            int low=intent.getIntExtra("low",16);

            senddata(high,low);


        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
