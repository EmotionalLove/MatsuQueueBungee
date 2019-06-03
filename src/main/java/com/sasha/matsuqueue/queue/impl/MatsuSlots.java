package com.sasha.matsuqueue.queue.impl;

import com.sasha.matsuqueue.Matsu;
import com.sasha.matsuqueue.queue.IMatsuQueue;
import com.sasha.matsuqueue.queue.IMatsuSlots;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MatsuSlots implements IMatsuSlots, Listener {

    public final String name;
    public final String permission;
    private int max;
    private List<UUID> slots = new ArrayList<>();
    private ConcurrentHashMap<String, IMatsuQueue> associatedQueues = new ConcurrentHashMap<>();

    public MatsuSlots(String name, int capacity, String permission) {
        this.name = name;
        this.max = capacity;
        this.permission = permission;
    }

    @Override
    public int getAvailableSlots() {
        return max - slots.size();
    }

    @Override
    public int getTotalSlots(boolean global) {
        return max;
    }

    @Override
    public void queuePlayer(ProxiedPlayer player) {
        if (!needsQueueing()) {
            occupySlot(player);
            return;
        }
        for (Map.Entry<String, IMatsuQueue> entry : associatedQueues.entrySet()) {
            for (String permission : player.getPermissions()) {
                if (!permission.contains(".") || !permission.startsWith("matsuqueue")) continue;
                String[] broken = permission.split(".");
                if (broken.length != 3) continue;
                if (entry.getValue().getPermission().equals(broken[2])) {
                    entry.getValue().addPlayerToQueue(player);
                    return;
                }
            }
        }
        // code quality goes to shit after my brain goes numb ;-;
        for (Map.Entry<String, IMatsuQueue> entry : associatedQueues.entrySet()) {
            if (entry.getValue().getPermission().equals("default")) {
                entry.getValue().addPlayerToQueue(player);
            }
        }
    }

    @Override
    public boolean needsQueueing() {
        return getAvailableSlots() == 0;
    }

    @Override
    public void onPlayerLeave(ProxiedPlayer player) {
        if (slots.contains(player.getUniqueId())) {
            releaseSlot(player);
            return;
        }
        this.getAssociatedQueues().forEach((name, queue) -> {
            queue.removePlayerFromQueue(player);
        });
    }


    // these shouldn't be called by public code.

    protected void occupySlot(ProxiedPlayer player) {
        this.occupySlot(player.getUniqueId());
    }

    protected void occupySlot(UUID player) {
        slots.add(player);
    }

    protected void releaseSlot(ProxiedPlayer player) {
        this.releaseSlot(player.getUniqueId());
    }

    protected void releaseSlot(UUID player) {
        slots.remove(player);
        associatedQueues.values().stream().sorted(Comparator.comparingInt(IMatsuQueue::getPriority)).forEach(IMatsuQueue::connectFirstPlayerToDestinationServer);
    }

    @Override
    public void associateQueue(IMatsuQueue queue) {
        if (queue == null) throw new IllegalStateException("null queue! this shouldn't happen.");
        associatedQueues.put(queue.getName(), queue);
    }

    @Override
    public ConcurrentHashMap<String, IMatsuQueue> getAssociatedQueues() {
        return associatedQueues;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void broadcast(String str) {
        AtomicInteger integer = new AtomicInteger(0);
        associatedQueues.values().stream().sorted(Comparator.comparingInt(IMatsuQueue::getPriority)).forEach(queue -> {
            for (UUID uuid : queue.getQueue()) {
                ProxiedPlayer player = Matsu.INSTANCE.getProxy().getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(new TextComponent(str.replace("{pos}", (integer.get() + 1) + "")));
                }
                integer.getAndIncrement();
            }
        });
    }
}
