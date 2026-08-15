# ConfigSearcher

A lightweight and backward-compatible Bukkit/Spigot plugin designed to search through configuration files and plugin JARs for specific text or messages directly from the game.

Compatible with EVERY Minecraft Version from old server versions (starting from Minecraft Beta/Bukkit) up to the latest releases.

## Features

* **Async Processing:** Performs all file and archive searches asynchronously to prevent server lag.
* **Backward Compatible:** Written in Java 6/7 compatible syntax to run smoothly on legacy and modern server cores alike.
* **Config Search (`/searchconfigs`):** Recursively scans the entire server root directory (including `bukkit.yml`, `server.properties`, and the `plugins` folder) for `.yml`, `.properties`, `.txt`, and `.json` files containing your search query.
* **JAR Search (`/searchjars`):** Scans inside all plugin `.jar` files within the plugins directory for matching text strings inside configuration or text assets.

## Commands & Permissions

* `/searchconfigs <query>` - Searches all configuration files across the server directory.
* Permission: `configsearcher.use`


* `/searchjars <query>` - Searches inside all plugin JAR files in the plugins folder.
* Permission: `configsearcher.use`
