package dev.parrotstudios.qtotems.totem;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.utils.scheduler.QSchedulerManager;
import dev.parrotstudios.qtotems.utils.scheduler.QTask;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class QTotemRegistry {
    private static final ConcurrentHashMap<NamespacedKey, QTotem> qTotemMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, QTotem> activePlayerEquips = new ConcurrentHashMap<>();
    private static QTask TASK;

    public static void startUp() {
        TASK = QSchedulerManager.getScheduler().runTimer(QTotemRegistry::checkActiveEquips, 100L, 100L);
    }

    @Contract(value = " -> new", pure = true)
    public static @NonNull HashSet<QTotem> getQTotems() {
        return new HashSet<>(qTotemMap.values());
    }

    public static void add(QTotem qTotem) {
        qTotemMap.put(qTotem.getKey(), qTotem);
    }

    public static @NonNull @Unmodifiable List<String> getTotemNames() {
        return qTotemMap.values().stream().map(QTotem::getName).toList();
    }

    @Contract(pure = true)
    public static @NonNull @Unmodifiable Map<UUID, QTotem> getActivePlayerEquips() {
        return Map.copyOf(activePlayerEquips);
    }

    public static boolean isQTotem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return false;
        PersistentDataContainerView pdc = stack.getPersistentDataContainer();
        if(pdc.isEmpty()) return false;
        return pdc.getKeys().stream().anyMatch(qTotemMap::containsKey);
    }

    public static QTotem getQTotem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        PersistentDataContainerView pdc = stack.getPersistentDataContainer();
        if(pdc.isEmpty()) return null;
        for(NamespacedKey key : pdc.getKeys()) {
            if(qTotemMap.containsKey(key)) return qTotemMap.get(key);
        }
        return null;
    }

    public static void clearPastEffects(@NonNull Player player) {
        QTotem active = activePlayerEquips.remove(player.getUniqueId());
        if (active != null) {
            active.removeEquipEffects(player);
        }
    }

    public static void handleEquip(Player player, ItemStack stack) {

        QTotem qTotem = getQTotem(stack);
        if (qTotem == null){
            clearPastEffects(player);
            return;
        }
        QTotem active = activePlayerEquips.get(player.getUniqueId());
        if (active != qTotem) {
            clearPastEffects(player);
        }
        QSchedulerManager.getScheduler().runAtEntity(player, () -> qTotem.provideEquipEffects(player));
        activePlayerEquips.put(player.getUniqueId(), qTotem);
    }

    public static void handlePop(Player player, ItemStack stack) {
        QTotem qTotem = getQTotem(stack);
        if (qTotem == null) return;
        QSchedulerManager.getScheduler().runAtEntity(player, () -> qTotem.providePopEffects(player));
        activePlayerEquips.remove(player.getUniqueId(), qTotem);
    }

    public static void handleLeave(Player player) {
        clearPastEffects(player);
    }

    public static void handleJoin(Player player) {
        handleEquip(player, player.getInventory().getItemInOffHand());
    }

    public static void checkActiveEquips() {
        new ArrayList<>(activePlayerEquips.keySet()).forEach(uuid -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if (player == null) return;
            ItemStack stack = player.getInventory().getItemInOffHand();
            QTotem active = activePlayerEquips.get(uuid);
            QTotem newTotem = getQTotem(stack);
            if(newTotem != active) {
                handleEquip(player, stack);
                return;
            }
            QSchedulerManager.getScheduler().runAtEntity(player, () -> active.provideEquipEffects(player));
    });
    }

    public static QTotem getQTotem(String totemName) {
        return getQTotems().stream().filter(qTotem -> qTotem.getName().equalsIgnoreCase(totemName)).findFirst().orElse(null);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void populate() {
        FileConfiguration config = QTotems.getConfigManager().getMainConfig().getConfig();
        ConfigurationSection section = config.getConfigurationSection("totems");
        if (section == null) {
            QTotems.getInstance().getLogger().warning("No totems found in configuration.");
            return;
        }
        section.getKeys(false).forEach(qTotem -> {
            try {
                if (!section.getBoolean("enabled", true)) {
                    return;
                }
                QTotem totem = QTotem.create(qTotem)
                        .displayName(section.getString("name"))
                        .lore(section.getStringList("lore"));
                List<String> popEffects = section.getStringList("popEffects");
                List<String> equipEffects = section.getStringList("equipEffects");
                popEffects.forEach(effect -> {
                    String[] split = effect.split(";");
                    totem.addPopEffect(
                            split[0].toLowerCase(),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[2]),
                            Boolean.parseBoolean(split[3]),
                            Boolean.parseBoolean(split[4]),
                            Boolean.parseBoolean(split[5]));
                });
                equipEffects.forEach(effect -> {
                    String[] split = effect.split(";");
                    totem.addEquipEffect(split[0].toLowerCase(),
                            Integer.parseInt(split[1]),
                            Boolean.parseBoolean(split[2]),
                            Boolean.parseBoolean(split[3]),
                            Boolean.parseBoolean(split[4]));
                });
                totem.register();
                QTotems.getInstance().getLogger().info("Registered Qtotem: " + qTotem);
            } catch (Exception e) {
                QTotems.getInstance().getLogger().warning("Invalid configuration for Qtotem: " + qTotem);
                e.printStackTrace();
            }
        });
    }

    public static void handleDisable() {
        if (TASK != null) TASK.cancel();
        qTotemMap.clear();
        new ArrayList<>(activePlayerEquips.keySet()).forEach(uuid -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if (player != null) clearPastEffects(player);
        });
    }

    public static void reload() {
        qTotemMap.clear();
        populate();
        getActivePlayerEquips().forEach((uuid, _) -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if (player != null) {
                handleEquip(player, player.getInventory().getItemInOffHand());
            }
        });
    }
}
