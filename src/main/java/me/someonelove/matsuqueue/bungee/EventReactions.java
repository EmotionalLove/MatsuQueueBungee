package me.someonelove.matsuqueue.bungee;

import me.someonelove.matsuqueue.bungee.queue.IMatsuSlotCluster;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
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
                    if (!permission.matches("matsuqueue\\..*\\..*")) continue;
                    String[] broken = permission.split("\\.");
                    if (broken.length != 3) continue;
                    String cache = broken[0] + "." + broken[1] + ".";
                    IMatsuSlotCluster slot = Matsu.CONFIG.slotsMap.get(Matsu.slotPermissionCache.get(cache));
                    if (slot == null) {
                        System.err.println(permission + " returns a null slot tier");
                        continue;
                    }
                    if (slot.needsQueueing()) {
                        return Matsu.queueServerInfo;
                    } else {
                        return Matsu.destinationServerInfo;
                    }
                }
                IMatsuSlotCluster slots = Matsu.CONFIG.slotsMap.get(Matsu.slotPermissionCache.get("matsuqueue.default."));
                if (slots == null) {
                    player.disconnect(new TextComponent("\2476No valid queue server to connect to ;-;"));
                    return null;
                }
                if (slots.needsQueueing()) {
                    return Matsu.queueServerInfo;
                } else {
                    return Matsu.destinationServerInfo;
                }
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
    public void preLogin(PreLoginEvent e) {
        if (!Matsu.destinationServerOk) {
            e.setCancelReason(new TextComponent("\2474The main server is unreachable."));
            e.setCancelled(true);
        }
        if (!Matsu.queueServerOk) {
            e.setCancelReason(new TextComponent("\2474The queue server is unreachable."));
            e.setCancelled(true);
        }
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
            if (!permission.matches("matsuqueue\\..*\\..*")) continue;
            String[] broken = permission.split("\\.");
            if (broken.length != 3) continue;
            String cache = broken[0] + "." + broken[1] + ".";
            IMatsuSlotCluster slot = Matsu.CONFIG.slotsMap.get(Matsu.slotPermissionCache.get(cache));
            if (slot == null) {
                System.err.println(permission + " returns a null slot tier");
                continue;
            }
            slot.queuePlayer(p);
            return;
        }
        IMatsuSlotCluster slots = Matsu.CONFIG.slotsMap.get(Matsu.slotPermissionCache.get("matsuqueue.default."));
        slots.queuePlayer(p);
    }
}
