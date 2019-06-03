package com.sasha.matsuqueue.queue;

import com.sasha.matsuqueue.Matsu;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;

public class EventReactions implements Listener {

    @EventHandler
    public void onPreJoin(PreLoginEvent e) {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandler() {
            @Override
            public ServerInfo getServer(ProxiedPlayer player) {
                for (String permission : player.getPermissions()) {
                    if (!permission.contains(".") || !permission.startsWith("matsuqueue")) continue;
                    String[] broken = permission.split(".");
                    if (broken.length != 3) continue;
                    for (Map.Entry<String, IMatsuSlots> slots : Matsu.CONFIG.slotsMap.entrySet()) {
                        if (slots.getValue().getPermission().equals(broken[1])) {
                            if (slots.getValue().needsQueueing()) {
                                return Matsu.INSTANCE.getProxy().getServerInfo(Matsu.CONFIG.queueServerKey);
                            } else {
                                return Matsu.INSTANCE.getProxy().getServerInfo(Matsu.CONFIG.destinationServerKey);
                            }
                        }
                    }
                }
                for (Map.Entry<String, IMatsuSlots> slots : Matsu.CONFIG.slotsMap.entrySet()) {
                    if (slots.getValue().getPermission().equals("default")) {
                        if (slots.getValue().needsQueueing()) {
                            return Matsu.INSTANCE.getProxy().getServerInfo(Matsu.CONFIG.queueServerKey);
                        } else {
                            return Matsu.INSTANCE.getProxy().getServerInfo(Matsu.CONFIG.destinationServerKey);
                        }
                    }
                }
                player.disconnect(new TextComponent("\2476No valid queue server to connect to ;-;"));
                return null;
            }

            @Override
            public void setServer(ProxiedPlayer player) {

            }

            @Override
            public void save() {

            }

            @Override
            public void close() {

            }
        });
    }

    @EventHandler
    public void onProxyJoin(ServerConnectedEvent e) {
        if (!e.getServer().getInfo().getName().equalsIgnoreCase(Matsu.CONFIG.queueServerKey)) return;
        ProxiedPlayer p = e.getPlayer();
        for (String permission : p.getPermissions()) {
            if (!permission.contains(".") || !permission.startsWith("matsuqueue")) continue;
            String[] broken = permission.split(".");
            if (broken.length != 3) continue;
            for (Map.Entry<String, IMatsuSlots> slots : Matsu.CONFIG.slotsMap.entrySet()) {
                if (slots.getValue().getPermission().equals(broken[1])) {
                    slots.getValue().queuePlayer(e.getPlayer());
                    return;
                }
            }
        }
        for (Map.Entry<String, IMatsuSlots> slots : Matsu.CONFIG.slotsMap.entrySet()) {
            if (slots.getValue().getPermission().equals("default")) {
                slots.getValue().queuePlayer(e.getPlayer());
            }
        }
    }
}
