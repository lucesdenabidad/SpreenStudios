package datta.core.hooks.voicechat;

import datta.core.Core;
import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class VoiceChatHook {

    private VoiceChatPlugin voicechatPlugin;
    private final Logger LOGGER;
    public VoiceChatHook(Core core){
        this.LOGGER = core.getLogger();
    }

    public VoiceChatHook enable(){
        BukkitVoicechatService service = Bukkit.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voicechatPlugin = new VoiceChatPlugin();
            service.registerPlugin(voicechatPlugin);
            LOGGER.info("Successfully registered voicechat hook");
        } else {
            LOGGER.info("Failed to register voicechat hook");
        }
        return this;
    }

    public void disable(){
        if (voicechatPlugin != null) {
            Bukkit.getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered voicechat hook");
        }
    }
}
