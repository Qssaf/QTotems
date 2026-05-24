# 🛡️ QTotems

Hey there! Welcome to **QTotems**—a highly customizable Minecraft plugin that lets you configure custom Totems of Undying with their own unique passive (equipped) buffs and active (pop) effects. Whether you want to give players a speed boost while holding an Ice Totem or resurrect them with giant explosions of absorption hearts, QTotems handles it all directly via a simple config file.

No hardcoded effects. No messy setups. Just customize, reload, and start popping!

---

## ✨ Features at a Glance

*   **Config-Driven Custom Totems:** Define as many custom totems as you like directly under `totems:` in `config.yml`.
*   **Dual-State Potion Effects:**
    *   **Equip Effects (Passive):** Buffs applied continuously to the player while they are holding/equipping the totem in their hand.
    *   **Pop Effects (Active):** Custom potion effects that trigger immediately when a player pops their totem to cheat death.
*   **Beautiful Lore & Name Formatting:** Full support for modern Adventure/MiniMessage styling (colors, gradients, bolding) and legacy Minecraft formatting (`&` color codes).
*   **Dynamic Registries:** Instantly enable or disable totems using the `enabled: true/false` config flag.
*   **Seamless Reloading:** Run `/totems reload` in-game or from the console to apply configuration changes instantly without server restarts.
*   **Failsafe Mechanics:** Safely handles missing config parameters, prints clear warnings to console instead of crashing, and safely resets player effects on disconnect.

---

## 🛠️ Command Reference

The plugin registers the main command `/totems` with aliases `/qtotems` and `/qtotem`. 

| Command | Permission | Description |
|:---|:---|:---|
| `/totems` | `qtotems.command` | Displays plugin usage message. |
| `/totems reload` | `qtotems.command` | Reloads the configuration and re-registers all active totems. *(Can be run from console!)* |
| `/totems <totem>` | `qtotems.command` | Spawns one custom totem item into your own inventory. |
| `/totems <totem> <player>` | `qtotems.command` | Spawns one custom totem item into target player's inventory. |

### Tab Completion
The plugin supports complete tab completion:
*   First argument: Completes active totem IDs registered in the system (and `reload`).
*   Second argument: Completes online player names (only if a valid totem ID is selected).

---

## 🔑 Permissions

*   `qtotems.command`
    *   **Description:** Allows access to the main command structure (`/totems`, reload, and giving/spawning totems).
    *   **Default:** `op` (Admin only by default; can be assigned via LuckPerms or other permission managers).

---

## ⚙️ Configuration Guide (`config.yml`)

The plugin configuration is located at [src/main/resources/config.yml](file:///home/qsssaf/IdeaProjects/QTotems/src/main/resources/config.yml) (or `plugins/QTotems/config.yml` on a live server).

### Main Sections

1.  **`prefix`**: The message prefix prefixed to all plugin messages. Supports MiniMessage XML formatting.
2.  **`totems`**: A dictionary map of totem IDs containing:
    *   `enabled`: If `false`, the totem is ignored and not registered.
    *   `name`: The display name of the item.
    *   `lore`: List of lines shown on the item tooltip.
    *   `popEffects`: Format `"effect_name;amplifier;duration_in_ticks"`.
    *   `equipEffects`: Format `"effect_name;amplifier"`.
3.  **`messages`**: Customizable translation messages for plugin feedback.

### Format Details

> [!NOTE]
> *   **Amplifiers:** In Minecraft/Spigot, amplifiers are **0-indexed**. This means `0` represents Level I, `1` represents Level II, etc.
> *   **Durations:** Pop durations are measured in **server ticks** (20 ticks = 1 second).

---

## 🛠️ Build and Development

To compile the plugin from source, simply build the project using Gradle.

```bash
./gradlew build -x test
```

The compiled jar file will be output to the build directory:
👉 `build/libs/QTotems-x.x.x.jar`

Copy the jar file to your Minecraft server's `/plugins` folder and run `/reload` or restart the server.
