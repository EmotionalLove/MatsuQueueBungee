package com.sasha.matsuqueue.queue.impl;

import com.sasha.matsuqueue.Matsu;
import com.sasha.matsuqueue.queue.IMatsuQueue;
import com.sun.istack.internal.Nullable;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;

public class MatsuQueue implements IMatsuQueue {

    public final String name;
    public final int priority;
    public final String permission;
    public final String slots;

    private LinkedList<UUID> queue = new LinkedList<>();

    public MatsuQueue(String name, int priority, String slots, @Nullable String permission) {
        this.name = name;
        this.priority = priority;
        this.permission = permission;
        this.slots = slots;
    }

    @Override
    public void addPlayerToQueue(ProxiedPlayer player) {
        queue.add(player.getUniqueId());
        player.sendMessage(new TextComponent(Matsu.CONFIG.serverFullMessage.replace("&", "\247")));
        Matsu.INSTANCE.getLogger().log(Level.INFO, player.getName() + " placed in queue " + this.name);
    }

    @Override
    public void removePlayerFromQueue(ProxiedPlayer player) {
        queue.remove(player.getUniqueId());
    }

    @Override
    public void connectFirstPlayerToDestinationServer() {
        if (queue.isEmpty()) return;
        ProxiedPlayer player = Matsu.INSTANCE.getProxy().getPlayer(queue.getFirst());
        player.sendMessage(new TextComponent(Matsu.CONFIG.connectingMessage.replace("&", "\247")));
        player.connect(Matsu.INSTANCE.getProxy().getServerInfo(Matsu.CONFIG.destinationServerKey));
        Matsu.INSTANCE.getLogger().log(Level.INFO, player.getName() + " transferred to destination server");
        Matsu.CONFIG.slotsMap.get(slots).occupySlot(player);
        queue.remove(queue.getFirst());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public final LinkedList<UUID> getQueue() {
        return queue;
    }


}
