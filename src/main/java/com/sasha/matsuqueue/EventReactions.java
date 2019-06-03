package com.sasha.matsuqueue;

import com.sasha.matsuqueue.queue.IMatsuSlots;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class EventReactions implements Listener {

    private static ArrayList<UUID> toDo = new ArrayList<>();

    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
        Matsu.CONFIG.slotsMap.forEach((name, slot) -> {
            slot.onPlayerLeave(e.getPlayer());
        });
    }

    @EventHandler
    public void onPreJoin(PreLoginEvent e) {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandler() {
            @Override
            public ServerInfo getServer(ProxiedPlayer player) {
                for (String permission : player.getPermissions()) {
                    if (!permission.contains(".") || !permission.startsWith("matsuqueue")) continue;
                    String[] broken = permission.split("\\.");
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
    public void postLogin(PostLoginEvent e) {
        toDo.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onProxyJoin(ServerConnectedEvent e) {
        if (!toDo.contains(e.getPlayer().getUniqueId())) return;
        toDo.remove(e.getPlayer().getUniqueId());
        ProxiedPlayer p = e.getPlayer();
        for (String permission : p.getPermissions()) {
            if (!permission.contains(".") || !permission.startsWith("matsuqueue")) continue;
            String[] broken = permission.split("\\.");
            if (broken.length != 3) continue;
            for (Map.Entry<String, IMatsuSlots> slots : Matsu.CONFIG.slotsMap.entrySet()) {
                if (slots.getValue().getPermission().equalsIgnoreCase(broken[1])) {
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
