package datta.core.services;

import datta.core.Core;

import java.util.ArrayList;
import java.util.List;

public class CommandService {
    public Core instance;
    public List<Service> serviceList = new ArrayList<>();

    public CommandService(Core instance) {
        this.instance = instance;
    }

    public void registerService(Service service) {
        serviceList.add(service);
    }

    public void unregisterService(Service service) {
        serviceList.remove(service);
    }

    public Service serviceFromName(String ServiceName) {
        for (Service service : serviceList) {
            if (service != null) {
                String name = service.name();
                if (name != null && (name.contains(ServiceName) || name.equalsIgnoreCase(ServiceName))) {
                    return service;
                }
            }
        }
        return null;
    }


    public void loadServices() {
        for (Service service : serviceList) {
            service.onLoad();
        }
    }

    public void unloadServices() {

    }
}
