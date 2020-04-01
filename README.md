### Notice
This plugin's development is suspended indefinitely until further notice. Take a look at the [1.15 branch](https://github.com/EmotionalLove/MatsuQueueBungee/tree/pull/12) for fixes to bugs that are present in the master branch. 

# MatsuQueue (待つQueue)
A queue plugin for controlling the player count of survival servers (or any type of server, for that matter).

## About
Running a single-world survival server with a large player count can be resource intensive. Instead of just reducing the max player limit and letting your players play the "reconnect lottery", why not create a queue to wait in? This plugin allows that, and it's simple to set up, and your users don't have to touch anything to queue up. If the server's full, they'll be automatically moved to the queue server, and automatically transferred to the main server when it's their turn.

### Donations
You can also set up your config to allow players to donate for faster access to the server!

# Setup
- Download a JAR file from our [releases](https://github.com/EmotionalLove/MatsuQueueBungee/releases), and move it to your Bungeecord plugins folder.

- Run the Bungeecord instance to generate the config, and then stop it.

- Edit the config to your needs, ensuring that the names of the servers match with that in your Bungeecord configuration.

- If needed, install the [MatsuQueueBukkit Companion Plugin](https://github.com/EmotionalLove/MatsuQueueBukkit) into the Bukkit server(s) that are serving as queuing servers.

- Run your Bukkit and Bungeecord servers and ensure your config is working as intended. (You can test by setting the MatsuQueue player limit to 1 or 2 and opening multiple instances of Minecraft.)

## Commented Default Config
warning: this may be chonky and confusing

```yml
# The first message to be displayed when the player enters the queue server, notifying them that the server is full.
serverFullMessage: '&6Server is full'
# The message displayed when the player finishes queueing and is being redirected to the main server.
connectingMessage: '&6Connecting to the server...'
# The message displayed every 10 seconds in chat that shows the position in queue the player is in.
positionMessage: '&6Your position in queue is &l{pos}'
# The name of the queue server in your bungeecord config
queueServerKey: queue
# The name of the main server in your bungeecord config
destinationServerKey: main
# These determine how many slots in total are available on your main server. The below default config will allow at the absolute maximum 200 players at once on the server.
# 50 of those players MUST have the permission for priority, 150 of those players MUST have the default permission.
# in this plugin, the permission identifier is split up into 2 levels. "matsuqueue.slot_tier.queue_tier" (ex. matsuqueue.default.faster will give the player access to the standardpriority queue)
# The "permission" value below will be the one in the middle.
# Feel free to create as many slot tiers as you want, or modify the existing ones.

# Important note: you must ALWAYS have a default slot tier, and a default queue tied to the default slot tier for players that don't have any special permissions to get in faster.

slots:
  standard:
    capacity: 150
    permission: default
  priority:
    capacity: 50 # These can act as "reserved" slots for your top donators. Even if the 150 standard slots are occupied, people who have the permission for this tier will get in instantly.
    permission: priority
# These are the individual queues. These are tied to the slot tiers above. When a player finishes queueing in one of these queues, they will then occupy a slot in the specified slot tier
# For queues that occupy the same slot tier, use the priority integer to determine which queue has more priority.
# Queues on the same slot tier that have a lower priority integer will have higher priority over the others. When a user joins this queue, they will be placed behind members of queues with lower integers, at the end of the line of their own queue, but in front of members of queues with higher integers (their number will bump up by 1).
# You can also add customer tab heater/footers for your different queues, to help users identify which level of priority they have.
queues:
  standard:
    priority: 3 # This is the default queue that everyone will be placed into, unless they have a special permission to go in a faster queue
    slots: standard
    permission: default
    tabHeader: '\n&dMatsuQueue\n\n&6Server is full\n&6Position in queue: &l{pos}\n'
    tabFooter: \n&6You can donate at https://paypal.me/eatsasha for priority access
      to the server.\n
  standardpriority:
    priority: 2 # This queue has higher priority over the standard queue, meaning when a member joins this queue, the members of the lower priority queue will be bumped up one spot. Take note how both of these queues have the same slot designation
    slots: standard
    permission: faster
    tabHeader: '\n&dMatsuQueue\n\n&6Server is full\n&6Position in queue: &l{pos}\n'
    tabFooter: \n&6Thank you for being a standard donator. You have standard priority
      queue status.\n
  priority:
    priority: 1 # This queue has the most priority, however it uses the "priority" slots, meaning it won't effect the other queues.
    slots: priority
    permission: reserved
    tabHeader: '\n&dMatsuQueue\n\n&6Server is full\n&6Position in queue: &l{pos}\n'
    tabFooter: \n&6Thank you for being a premium donator. You have access to the reserved
      slots on the server.\n
# All of the names of the queues you specified must go here. The names MUST match the YAML key you used (https://i.imgur.com/7WKDv7n.png)
queuenames:
- standard
- standardpriority
- priority
# Same deal here, but for slot names (https://i.imgur.com/AUC3O37.png)
slotnames:
- standard
- priority
```

## //todo
- ping the destination server before moving the player in case it's down. if it's down just keep periodically trying to join it and broadcast a notification in chat to all waiting players that the queue will begin moving again when the server is back up.

- maybe make a way to hold you spot for a few seconds in case you accidentally disconnected/crashed/etc?

- add priority integer to slot groups
