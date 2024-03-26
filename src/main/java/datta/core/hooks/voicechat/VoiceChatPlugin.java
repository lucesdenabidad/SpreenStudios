package datta.core.hooks.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.entity.Player;

public class VoiceChatPlugin implements VoicechatPlugin {
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

        boolean muted = false;
        if(player.hasMetadata("voicestick"))
            muted = player.getMetadata("voicestick").get(0).asBoolean();

        if(muted && !player.hasPermission("spreenstudios.voicechat"))
            event.cancel();
    }
}
