package datta.core.paper.task;

import datta.core.paper.Core;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;

import static datta.core.paper.utilities.Color.format;

public class BroadcastSenderTask {
    static int time = 300; // 5 minutos
    public static List<BroadcastMessage> msg = List.of(new BroadcastMessage(new String[]{"&f", "&f               &#ffa91f&lHOLY HOSTING", "&fAdquiere un servidor de calidad al mejor precio en el", "&fmejor proveedor de hosting para Minecraft", "&f            &#ffc31fhttps://www.holy.gg/", "&f"}), new BroadcastMessage(new String[]{"&f", "&f               &#ffa91f&lHOLY HOSTING", "&fObtén un servidor de Minecraft al mejor precio", "&fy obtén el mejor rendimiento del mercado", "&f            &#ffc31fhttps://www.holy.gg/", "&f"}));
    public static void start(){
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), new Runnable() {
            public void run() {
                msg();
            }
        }, 0L, 20L * time);
    }

    public static void msg() {
        BroadcastMessage broadcastMessage = (BroadcastMessage) msg.get((new Random()).nextInt(msg.size()));
        broadcastMessage.send();
    }

    public static class BroadcastMessage {
        public String[] lines;

        public BroadcastMessage(String... lines) {
            this.lines = lines;
        }

        public void send() {
            String[] var1 = this.lines;
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                String s = var1[var3];
                Bukkit.broadcastMessage(format(s));
            }

        }
    }
}