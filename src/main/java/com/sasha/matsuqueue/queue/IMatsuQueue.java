package com.sasha.matsuqueue.queue;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Interface that sets up an easy way to manage this queue
 */
public interface IMatsuQueue {

    void addPlayerToQueue(ProxiedPlayer player);

    void removePlayerFromQueue(ProxiedPlayer player);

    void connectFirstPlayerToDestinationServer();

    String getName();

    int getPriority();

    String getPermission();

    LinkedList<UUID> getQueue();

    //void broadcast(String str);

}
