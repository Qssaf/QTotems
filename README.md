# QTotems

<p align="center">
  <strong>A highly customizable Minecraft plugin for custom Totems of Undying.</strong><br>
  Configure unique passive buffs and active resurrection effects directly through configuration.
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#commands">Commands</a> •
  <a href="#permissions">Permissions</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#build">Build</a>
</p>


<p align="center">
  <strong>A highly customizable Minecraft plugin for custom Totems of Undying.</strong><br>
  Configure unique passive buffs and active resurrection effects directly through configuration.
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#commands">Commands</a> •
  <a href="#permissions">Permissions</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#build">Build</a>
</p>

---

## Features

### Config-Driven Custom Totems

Define an unlimited number of custom totems under the `totems:` section in `config.yml`.

### Dual-State Potion Effects

Each totem supports two independent effect systems:

* **Equip Effects (Passive)** — Continuous buffs applied while the totem is held.
* **Pop Effects (Active)** — Effects triggered immediately when the totem activates.

### Rich Formatting Support

Supports both modern Adventure/MiniMessage formatting and legacy `&` color codes for names, lore, and messages.

### Dynamic Registries

Enable or disable individual totems at any time using:

```yaml
enabled: true
```

### Live Reloading

Apply configuration changes instantly without restarting the server using:

```text
/totems reload
```

### Failsafe Mechanics

QTotems is designed to fail gracefully by:

* Handling missing configuration values safely.
* Logging clear warnings to the console.
* Resetting temporary player effects on disconnect.

---

## Commands

QTotems registers the `/totems` command with the aliases `/qtotems` and `/qtotem`.

| Command                    | Permission        | Description                                                                      |
| -------------------------- | ----------------- | -------------------------------------------------------------------------------- |
| `/totems`                  | `qtotems.command` | Displays the plugin usage message.                                               |
| `/totems reload`           | `qtotems.command` | Reloads the configuration and re-registers active totems. *(Console compatible)* |
| `/totems <totem>`          | `qtotems.command` | Gives a custom totem to yourself.                                                |
| `/totems <totem> <player>` | `qtotems.command` | Gives a custom totem to another player.                                          |

### Tab Completion

* **First argument:** Active totem IDs and `reload`.
* **Second argument:** Online player names when a valid totem ID is specified.

---

## Permissions

| Permission        | Default | Description                                                                     |
| ----------------- | ------- | ------------------------------------------------------------------------------- |
| `qtotems.command` | `op`    | Grants access to all QTotems commands, including reloading and spawning totems. |

---

## Configuration

The configuration file is located at:

* **Production:** `plugins/QTotems/config.yml`
* **Source:** `src/main/resources/config.yml`

### Main Sections

| Section    | Description                                                           |
| ---------- | --------------------------------------------------------------------- |
| `prefix`   | Prefix prepended to plugin messages. Supports MiniMessage formatting. |
| `totems`   | Collection of custom totem definitions.                               |
| `messages` | Customizable messages used throughout the plugin.                     |

### Effect Formats

#### Pop Effects

```text
effect_name;amplifier;duration_in_ticks;ambient;particles;icon
```

#### Equip Effects

```text
effect_name;amplifier;ambient;particles;icon
```

> **Note:** Potion amplifiers are **0-indexed** (`0` = Level I, `1` = Level II, and so on). Pop effect durations use server ticks (`20 ticks = 1 second`).

---

## Build

Compile the plugin using Gradle:

```bash
./gradlew build -x test
```

The compiled JAR will be generated at:

```text
build/libs/QTotems-x.x.x.jar
```

Copy the generated JAR into your server's `/plugins` directory, then restart the server or reload your plugin setup.

---

<p align="center">
  <sub>Built for Spigot and Paper servers. Fully configurable. No hardcoded effects. No unnecessary complexity.</sub>
</p>
