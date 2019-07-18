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

## //todo
- ping the destination server before moving the player in case it's down. if it's down just keep periodically trying to join it and broadcast a notification in chat to all waiting players that the queue will begin moving again when the server is back up.

- maybe make a way to hold you spot for a few seconds in case you accidentally disconnected/crashed/etc?

- add priority integer to slot groups
