package com.sasha.matsuqueue.queue.impl;

import com.sasha.matsuqueue.queue.IMatsuQueue;
import com.sasha.matsuqueue.queue.IMatsuSlots;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
}
