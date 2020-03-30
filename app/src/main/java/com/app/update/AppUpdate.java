package com.app.update;

public class AppUpdate {

    private static AppUpdate instance=new AppUpdate();

    private NetManager netManager =new NetMangetImpl();

    public NetManager getNetManager() {
        return netManager;
    }

    public void setNetManager(NetManager netManager) {
        this.netManager = netManager;
    }

    public static AppUpdate getInstance(){
        return instance;
    }
}
