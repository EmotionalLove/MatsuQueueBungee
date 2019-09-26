package me.someonelove.matsuqueue.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class Matsu extends Plugin {

    public static ConfigurationFile CONFIG;
    public static Matsu INSTANCE;

    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    /**
     * Used to (hopefully) make the process of choosing a server on-join faster.
     */
    public static LinkedHashMap<String, String> slotPermissionCache = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> queuePermissionCache = new LinkedHashMap<>();
    public static ServerInfo destinationServerInfo;
    public static ServerInfo queueServerInfo;

    public static boolean queueServerOk = true;
    public static boolean destinationServerOk = true;

    @Override
    public void onEnable() {
        slotPermissionCache.clear();
        INSTANCE = this;
        getLogger().log(Level.INFO, "MatsuQueue is loading.");
        CONFIG = new ConfigurationFile();
        this.getProxy().getPluginManager().registerListener(this, new EventReactions());
        executorService.scheduleWithFixedDelay(() -> {
            purgeQueues();
            queueServerOk = isServerUp(queueServerInfo);
            if (!queueServerOk) {
                for (ProxiedPlayer player : getProxy().getPlayers()) {
                    player.disconnect(new TextComponent("\2474The queue server is no longer reachable."));
                }
                return;
            }
            destinationServerOk = isServerUp(destinationServerInfo);
            if (!destinationServerOk) {
                for (ProxiedPlayer player : getProxy().getPlayers()) {
                    player.disconnect(new TextComponent("\2474The main server is no longer reachable."));
                }
                return;
            }
            CONFIG.slotsMap.forEach((name, slot) -> slot.broadcast(CONFIG.positionMessage.replace("&", "\247")));
        }, 10L, 10L, TimeUnit.SECONDS);
        getLogger().log(Level.INFO, "MatsuQueue has loaded.");
    }

    private void purgeQueues() {
        List<UUID> removalList = new ArrayList<>();
        CONFIG.slotsMap.forEach((str, cluster) -> {
            for (UUID slot : cluster.getSlots()) {
                ProxiedPlayer player = this.getProxy().getPlayer(slot);
                if (player == null || !player.isConnected()) {
                    removalList.add(slot);
                }
            }
            removalList.forEach(cluster::onPlayerLeave);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isServerUp(ServerInfo info) {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] up = {true};
        info.ping((result, error) -> {
            if (error != null) {
                up[0] = false;
            }
            latch.countDown();
        });
        try {
            latch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
        return up[0];
    }
}
