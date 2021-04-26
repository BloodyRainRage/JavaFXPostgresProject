package com.baddragon.manager;

import java.util.HashMap;
import java.util.Map;

public class AppManager {

    static AppManager manager = new AppManager();

    private Map<String, Object> instances= new HashMap();

    public Object findInstance(String controllerName){
        return instances.get(controllerName);
    }

    public void addController(String controllerName, Object controller){

        instances.put(controllerName, controller);

    }

    private AppManager(){}

    public static AppManager getInstance(){
        return manager;
    }

}
