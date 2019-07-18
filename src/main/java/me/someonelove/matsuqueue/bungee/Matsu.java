package me.someonelove.matsuqueue.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class Matsu extends Plugin {

    public static ConfigurationFile CONFIG;
    public static Matsu INSTANCE;

    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().log(Level.INFO, "MatsuQueue is loading.");
        CONFIG = new ConfigurationFile();
        this.getProxy().getPluginManager().registerListener(this, new EventReactions());
        executorService.scheduleWithFixedDelay(() ->
                        CONFIG.slotsMap.forEach((name, slot) -> slot.broadcast(CONFIG.positionMessage.replace("&", "\247"))),
                10L, 10L, TimeUnit.SECONDS);
        getLogger().log(Level.INFO, "MatsuQueue has loaded.");
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
