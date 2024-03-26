package datta.core.hooks.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class VoiceChatPlugin implements VoicechatPlugin {
    public static Permission PERMISSION = new Permission("spreenstudios.voicechat", PermissionDefault.OP);
    @Override
    public String getPluginId() {
        return "spreenstudios-voicechat";
    }

    @Override
    public void initialize(VoicechatApi voicechatApi) {}

    @Override
    public void registerEvents(EventRegistration eventRegistration) {
        eventRegistration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        if (event.getSenderConnection() == null)
            return;

        if (!(event.getSenderConnection().getPlayer().getPlayer() instanceof Player player))
            return;

        player.sendMessage("0");

        boolean muted = false;
        if(player.hasMetadata("voicestick"))
            muted = player.getMetadata("voicestick").get(0).asBoolean();

        player.sendMessage("1 -> "+muted);

        if(muted && !player.hasPermission(PERMISSION)){
            event.cancel();
            player.sendMessage("2 -> cancel");
        }else{
            player.sendMessage("3 -> no cancel");
        }
        player.sendMessage("muted: "+muted+" permission: "+player.hasPermission(PERMISSION));
    }
}
