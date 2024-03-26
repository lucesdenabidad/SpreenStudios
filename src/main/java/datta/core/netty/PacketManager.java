package datta.core.netty;

import com.google.gson.JsonObject;
import datta.core.Core;
import org.bukkit.entity.Player;

public class PacketManager{

    private final Core plugin;

    public PacketManager(Core plugin) {
        this.plugin = plugin;
    }

    public void sendPacket(Player player, JsonObject jsonObject) {
        FriendlyByteBuf buffer = new FriendlyByteBuf();
        buffer.writeUtf(jsonObject.toString());
        player.sendPluginMessage(plugin, "spreengames:global", buffer.array());
    }

    public void sendGlobalPacket(Player player, String action, JsonObject jsonObject) {
        jsonObject.addProperty("action", action);
        sendPacket(player, jsonObject);
    }
}