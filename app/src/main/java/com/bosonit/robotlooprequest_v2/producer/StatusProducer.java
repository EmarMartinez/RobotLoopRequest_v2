package com.bosonit.robotlooprequest_v2.producer;

import android.net.Uri;
import android.os.Environment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.actionbean.Pose;
import com.ainirobot.coreservice.client.robotsetting.RobotSettingApi;
import com.bosonit.robotlooprequest_v2.MainActivity;
import com.bosonit.robotlooprequest_v2.R;
import com.bosonit.robotlooprequest_v2.model.StatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StatusProducer implements Runnable{

    MainActivity ma;
    private static final int SLEEP_TIME_STATUS = 10 * 1000;

    public StatusProducer(MainActivity mainActivity) {
        this.ma = mainActivity;
    }

    @Override
    public void run() {

        System.out.println("StatusProducer: Run....");
        StatusUpdate su = new StatusUpdate();
        boolean gotMapData = false;
        List<Pose> points = new ArrayList<>();
        String mapName = null;
        while(true) {
            try {
                if(!gotMapData){
                    System.out.println("Datos del mapa:");
                    //testdata
//                    Pose pose1 = new Pose();
//                    pose1.setName("Punto1");
//                    pose1.setX((float) 1.555);
//                    pose1.setY((float) 1.555);
//                    pose1.setTheta((float) 1.555);
//                    Pose pose2 = new Pose();
//                    pose2.setName("Punto2");
//                    pose2.setX((float) 1.555);
//                    pose2.setY((float) 1.555);
//                    pose2.setTheta((float) 1.555);
//                    points.add(pose1);
//                    points.add(pose2);
//                    mapName = "MapaTest";
                    //endtestdata
                    points = RobotApi.getInstance().getPlaceList();
                    mapName = RobotApi.getInstance().getMapName();
                    gotMapData = true;
                }

            }
            catch(Exception e) {
                System.out.println("Problema con el mapa:");
                System.out.println(e.getMessage());
            }

            try {
                List<String> listaVideos = getVideoNames();
                su.setListaVideos(listaVideos);
            } catch (Exception e) {
                System.out.println(e.getMessage());;
            }

            String batteryLvl =
                    RobotSettingApi.getInstance().getRobotString(Definition.ROBOT_SETTINGS_BATTERY_INFO);
//            String batteryLvl = (Math.floor(Math.random()*100))+ "";
            su.setListaPuntos(points);
            su.setMapName(mapName);
            su.setBatteryLevel(batteryLvl);
            sendStatus(su);

            try {
                Thread.sleep(SLEEP_TIME_STATUS);

            }
            catch(InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void sendStatus(StatusUpdate su) {
        System.out.println("StatusProducer: sendStatus...");

        try {
            URL url = new URL("http://192.168.10.53:80/api/v1/status");//elliot endpoint
//            URL url = new URL("http://192.168.1.58:8080/api/v1/status");//mi laptop
            System.out.println("Actualizando status....");
            System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            String statusStr = gson.toJson(su);
            System.out.println(statusStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = statusStr.getBytes(StandardCharsets.UTF_8);
                os.write(input);
                System.out.println("enviada actualizacion");
            }
            con.getInputStream();

        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }
    private List<String> getVideoNames() {
        List<String> lista = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for(int count=0; count < fields.length; count++){
            lista.add(fields[count].getName());
        }

        return lista;
    }
}
