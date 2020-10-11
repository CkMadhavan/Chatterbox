package android.sabertechnologies.com.android;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by madhav on 1/14/19.
 */

public class ServiceClass extends Service {

    boolean isRunning;
    Context context;
    Thread backgroundThread;

    @Override
    public IBinder onBind(Intent i){
        return null;
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext() , "Hi" , Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    };

    @Override
    public void onCreate(){
        this.isRunning = false;
        this.context = this;
    }

    @Override
    public void onDestroy(){
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent inten , int flags , int startId){
        if (!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return  START_STICKY;
    }

}
