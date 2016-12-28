package com.Sunshine;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class datasyncService extends WearableListenerService  {


    @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {

            for(DataEvent dataevent:dataEventBuffer)
            {

                if(dataevent.getType()==DataEvent.TYPE_CHANGED)
                {
                    DataMap map= DataMapItem.fromDataItem(dataevent.getDataItem()).getDataMap();
                    String path= dataevent.getDataItem().getUri().getPath();
                    if(path.equals("/wearData"))
                    {
                        String h=""+map.getInt("high");
                        String l=""+map.getInt("low");
                        int wid =map.getInt("icon");
                        PreferenceManager manager=new PreferenceManager(getApplicationContext());
                        manager.setPrefString("hightemp",h);
                        manager.setPrefString("lowtemp",l);
                        manager.setPrefInt("icon_id",wid);



                    }


                }



            }



        }



}
