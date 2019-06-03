package com.sasha.matsuqueue;

import me.someonelove.quickyml.YMLParser;

import java.io.File;
import java.io.IOException;

public class ConfigurationFile {

    public static final String FILE_NAME = "QueueConfig.yml";

    public ConfigurationFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
                YMLParser parser = new YMLParser(file);
                parser.set("serverFullMessage", "&6Server is full");
                parser.set("queueServerKey", "queue");
                parser.set("destinationServerKey", "main");
                // slots
                parser.set("slots.standard", 150);
                parser.set("slots.priority", 50);
                // standard queue
                parser.set("queues.standard.priority", 3);
                parser.set("queues.standard.slots", "standard");
                parser.set("queues.standard.permission", "default");
                // priority queue (standard slots)
                parser.set("queues.standardpriority.priority", 2);
                parser.set("queues.standardpriority.slots", "standard");
                parser.set("queues.standardpriority.permission", "queue.faster");
                // priority queue (priority slots)
                parser.set("queues.priority.priority", 1);
                parser.set("queues.priority.slots", "priority");
                parser.set("queues.priority.permission", "queue.reserved");
                parser.save();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
