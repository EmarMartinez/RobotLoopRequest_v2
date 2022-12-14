package com.bosonit.robotlooprequest_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.VideoView;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.RobotApi;
import com.bosonit.robotlooprequest_v2.application.ModuleCallback;
import com.bosonit.robotlooprequest_v2.consumer.OrderConsumer;
import com.bosonit.robotlooprequest_v2.producer.StatusProducer;

public class MainActivity extends AppCompatActivity {

    private int checkTimes;
    public VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        video = (VideoView)findViewById(R.id.videoView);
        connectApi();
        checkInit();
        startActivity();
    }
    private void checkInit(){
        checkTimes++;
        if(checkTimes > 10){
            System.out.println("Fallo al iniciar");;
        }
        else if(RobotApi.getInstance().isApiConnectedService()){
            System.out.println("Conectado");;
        }else
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Intentando conectar....");
                    checkInit();
                }
            },300);
        }
    }
    private void connectApi() {
        RobotApi.getInstance().connectServer(this, new ApiListener() {
            @Override
            public void handleApiDisabled() {
                System.out.println("ConnectApiListener: disabled");
            }
            @Override
            public void handleApiConnected() {
                System.out.println("ConnectApiListener: Connected");
                RobotApi.getInstance().setCallback(new ModuleCallback());
            }
            @Override
            public void handleApiDisconnected() {
                System.out.println("ConnectApiListener: Disconnected");
            }
        });
    }
    private void startActivity() {
        System.out.println("MainActivity: Iniciando servicio");
        Thread orderRequestThread = new Thread(new OrderConsumer(this));
        orderRequestThread.start();
        Thread statusUpdaterThread = new Thread(new StatusProducer(this));
        statusUpdaterThread.start();

    }
}