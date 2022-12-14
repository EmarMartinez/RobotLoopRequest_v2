package com.bosonit.robotlooprequest_v2.model;

import com.ainirobot.coreservice.client.actionbean.Pose;

import java.util.List;

public class StatusUpdate {
    public String batteryLevel;
    public List<Pose> listaPuntos;
    public String mapName;
    public List<String> listaVideos;

    public List<String> getListaVideos() {
        return listaVideos;
    }

    public void setListaVideos(List<String> listaVideos) {
        this.listaVideos = listaVideos;
    }

    public StatusUpdate(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public StatusUpdate() {
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public List<Pose> getListaPuntos() {
        return listaPuntos;
    }

    public void setListaPuntos(List<Pose> listaPuntos) {
        this.listaPuntos = listaPuntos;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}


