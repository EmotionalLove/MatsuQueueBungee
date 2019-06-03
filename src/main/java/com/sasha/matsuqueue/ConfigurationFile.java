package com.sasha.matsuqueue;

import com.sasha.matsuqueue.queue.IMatsuQueue;
import com.sasha.matsuqueue.queue.IMatsuSlots;
import com.sasha.matsuqueue.queue.impl.MatsuQueue;
import com.sasha.matsuqueue.queue.impl.MatsuSlots;
import me.someonelove.quickyml.YMLParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * I'm really sorry but i strongly dislike BungeeCord's YML stuff.
 */
public class ConfigurationFile {

    public static final String FILE_NAME = "QueueConfig.yml";

    public String queueServerKey;
    public String destinationServerKey;
    public String serverFullMessage;
    public String positionMessage;
    public String connectingMessage;
    public ConcurrentHashMap<String, IMatsuSlots> slotsMap = new ConcurrentHashMap<>();

    protected ConfigurationFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
                YMLParser parser = new YMLParser(file);
                parser.set("serverFullMessage", "&6Server is full");
                parser.set("connectingMessage", "&6Connecting to the server...");
                parser.set("positionMessage", "&6Your position in queue is &l{pos}");
                parser.set("queueServerKey", "queue");
                parser.set("destinationServerKey", "main");
                // slots
                parser.set("slots.standard.capacity", 150);
                parser.set("slots.standard.permission", "default");
                parser.set("slots.priority.capacity", 50);
                parser.set("slots.priority.permission", "priority");
                // standard queue
                parser.set("queues.standard.priority", 3);
                parser.set("queues.standard.slots", "standard");
                parser.set("queues.standard.permission", "default");
                parser.set("queues.standard.tabHeader", "\\n&dMatsuQueue\\n\\n&6Server is full\\n&6Position in queue: &l{pos}\\n");
                parser.set("queues.standard.tabFooter", "\\n&6You can donate at https://paypal.me/eatsasha for priority access to the server.\\n");
                // priority queue (standard slots)
                parser.set("queues.standardpriority.priority", 2);
                parser.set("queues.standardpriority.slots", "standard");
                parser.set("queues.standardpriority.permission", "faster");
                parser.set("queues.standardpriority.tabHeader", "\\n&dMatsuQueue\\n\\n&6Server is full\\n&6Position in queue: &l{pos}\\n");
                parser.set("queues.standardpriority.tabFooter", "\\n&6Thank you for being a standard donator. You have standard priority queue status.\\n");
                // priority queue (priority slots)
                parser.set("queues.priority.priority", 1);
                parser.set("queues.priority.slots", "priority");
                parser.set("queues.priority.permission", "reserved");
                parser.set("queues.priority.tabHeader", "\\n&dMatsuQueue\\n\\n&6Server is full\\n&6Position in queue: &l{pos}\\n");
                parser.set("queues.priority.tabFooter", "\\n&6Thank you for being a premium donator. You have access to the reserved slots on the server.\\n");

                ArrayList<String> queues = new ArrayList<>();
                queues.add("standard");
                queues.add("standardpriority");
                queues.add("priority");
                parser.set("queuenames", queues);
                ArrayList<String> slots = new ArrayList<>();
                slots.add("standard");
                slots.add("priority");
                parser.set("slotnames", slots);
                parser.save();
            } catch (IOException e) {
                e.printStackTrace();
                Matsu.INSTANCE.getLogger().log(Level.SEVERE, "Couldn't initialise default configuration file!!! Cannot continue!");
                Matsu.INSTANCE.getProxy().stop();
                return;
            }
        }
        YMLParser parser = new YMLParser(file);
        serverFullMessage = parser.getString("serverFullMessage", "&6Server is full");
        queueServerKey = parser.getString("queueServerKey", "queue");
        positionMessage = parser.getString("positionMessage", "&6Your position in queue is &l{pos}");
        connectingMessage = parser.getString("connectingMessage", "&6Connecting to the server...");
        destinationServerKey = parser.getString("destinationServerKey", "main");
        final List<String> slots = parser.getStringList("slotnames");
        for (final String slot : slots) {
            if (!parser.exists("slots." + slot)) continue;
            final int capacity = parser.getInt("slots." + slot + ".capacity");
            final String permission = parser.getString("slots." + slot + ".permission");
            slotsMap.put(slot, new MatsuSlots(slot, capacity, permission));
            Matsu.INSTANCE.getLogger().log(Level.INFO, "Discovered valid slot type " + slot);
        }
        final List<String> queues = parser.getStringList("queuenames");
        for (final String queue : queues) {
            if (!parser.exists("queues." + queue)) continue;
            final int priority = parser.getInt("queues." + queue + ".priority");
            final String slot = parser.getString("queues." + queue + ".slots");
            if (!slots.contains(slot)) continue;
            final String permission = parser.getString("queues." + queue + ".permission");
            IMatsuQueue q = new MatsuQueue(queue, priority, slot, permission);
            q.setTabText(parser.getString("queues.priority.tabHeader", "\n&dMatsuQueue\n\n&6Server is full\n&6Position in queue: &l{pos}\n"),
                    parser.getString("queues.priority.tabFooter", "\n&6You can donate at https://paypal.me/eatsasha for priority access to the server.\n"));
            slotsMap.get(slot).associateQueue(q);
            Matsu.INSTANCE.getLogger().log(Level.INFO, "Discovered valid queue " + queue + " associated to slot type " + slot);
        }
    }


}
