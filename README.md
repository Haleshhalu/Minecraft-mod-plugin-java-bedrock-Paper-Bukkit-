# BMC5Crossplay

`BMC5Crossplay` is a server-side Paper plugin by SlayerPlayz that recreates
selected BMC5-style RPG, adventure, economy, shop, quest, dungeon, and boss
systems without installing the BMC5 modpack. It is designed for Java players
and Bedrock players connected through Geyser/Floodgate.

## Requirements

- Paper 26.2
- Java 25
- Geyser/Floodgate are optional but recommended for crossplay

## Build

```bash
./gradlew clean build
```

The distributable plugin is `build/libs/BMC5Crossplay-0.1.0.jar`.
The SQLite driver is bundled into the shaded jar.

## Aternos

1. Create or select a Paper server in Aternos.
2. Set the server Java version to Java 25 if available.
3. Install Geyser and Floodgate separately for Bedrock access.
4. Build this project and upload `build/libs/BMC5Crossplay-0.1.0.jar` to the
   server's `plugins` folder.
5. Start once, edit files in `plugins/BMC5Crossplay`, then restart.

## Commands

Player commands: `/balance`, `/bal`, `/money`, `/pay`, `/shop`, `/stats`,
`/skills`, `/iteminfo`, `/backpack`, `/bp`, `/quests`, `/quest <id>`,
`/dungeons`, and `/bosses`.

Admin commands: `/adminmoney give|take|set`, `/shop reload`, `/shopadmin reload`,
`/shopadmin price <item> <buy> <sell>`, `/customitem give`, and
`/boss spawn <id>`.

## Permissions

`bmc5crossplay.balance`, `pay`, `shop`, `stats`, `skills`, `backpack`, `quests`,
`dungeons`, and `bosses` default to true. `bmc5crossplay.admin` defaults to OP.

## Configuration and SQLite

All gameplay content is in separate YAML files. SQLite data is stored in
`plugins/BMC5Crossplay/data.db`, using UUIDs, prepared statements, transactions,
and asynchronous persistence. Backups should be made while the server is
stopped.

## Compatibility and limitations

The plugin uses Paper/Bukkit APIs and Bedrock-friendly inventory interactions;
players do not need Java mods. It does not emulate client-side mod GUIs,
Forge/Fabric mechanics, or install the actual BMC5 modpack. Dungeons currently
provide the configurable discovery/entry framework; room generation, waves,
party matchmaking, and advanced boss abilities are extension points.

## Roadmap

Dungeon room templates and timers, richer objective listeners, party queues,
custom equipment abilities, resource-pack assets, and additional boss phases.