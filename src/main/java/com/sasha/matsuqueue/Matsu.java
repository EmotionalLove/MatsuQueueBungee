package com.sasha.matsuqueue;

import com.sasha.matsuqueue.queue.EventReactions;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public final class Matsu extends Plugin {

    public static ConfigurationFile CONFIG;
    public static Matsu INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().log(Level.INFO, "MatsuQueue is loading.");
        CONFIG = new ConfigurationFile();
        this.getProxy().getPluginManager().registerListener(this, new EventReactions());
        getLogger().log(Level.INFO, "MatsuQueue has loaded.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
