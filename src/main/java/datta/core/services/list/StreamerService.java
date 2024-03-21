package datta.core.services.list;

import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;

import java.util.ArrayList;
import java.util.List;

public class StreamerService extends Service {

    public Configuration configuration = Core.getInstance().configurationManager.getConfig("streamers.yml");


    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "StreamerWhitelist";
    }

    @Override
    public String[] info() {
        return new String[]{
                "Al cargar el plugin en el servidor se agregaran",
                "los jugadores agregados enm streamers.yml",
                "a la lista de ingreso del servidor"};
    }


    @Override
    public void onLoad() {
        register(false, false);
        addAllToWhitelist();
    }

    @Override
    public void onUnload() {
        register(false, false);
        removeAllFromWhitelist();
    }


    private void addAllToWhitelist() {
        WhitelistService whitelist = (WhitelistService) Core.getInstance().commandService.serviceFromName("Whitelist");

        List<String> strings = loadList();
        for (String s : strings) {
            whitelist.addToWhitelist(s);
        }
    }

    private void removeAllFromWhitelist() {
        WhitelistService whitelist = (WhitelistService) Core.getInstance().commandService.serviceFromName("Whitelist");

        List<String> strings = loadList();
        for (String s : strings) {
            whitelist.removeFromWhitelist(s);
        }
    }

    private List<String> loadList(){
        return configuration.getStringList("streamers", new ArrayList<>());
    }
}
