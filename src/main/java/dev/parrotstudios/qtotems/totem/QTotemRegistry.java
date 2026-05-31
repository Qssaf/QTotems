package dev.parrotstudios.qtotems.totem;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.config.ConfigManager;
import dev.parrotstudios.qtotems.utils.QSchedulerManager;
import dev.parrotstudios.qtotems.utils.taskwrappers.QTask;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class QTotemRegistry {
    private static final List<QTotem> qTotems = new ArrayList<>();
    private static final ConcurrentHashMap<UUID, QTotem> activePlayerEquips = new ConcurrentHashMap<>();
    private static QTask TASK;

    public static void startUp() {
        TASK = QSchedulerManager.runTimer(QTotemRegistry::checkActiveEquips, 100L, 100L);
    }

    @Contract(value = " -> new", pure = true)
    public static @NonNull List<QTotem> getQTotems() {
        return new ArrayList<>(qTotems);
    }

    public static void add(QTotem qTotem) {
        qTotems.add(qTotem);
    }

    public static @NonNull @Unmodifiable List<String> getTotemNames() {
        return qTotems.stream().map(QTotem::getName).toList();
    }

    @Contract(pure = true)
    public static @NonNull @Unmodifiable Map<UUID, QTotem> getActivePlayerEquips() {
        return Map.copyOf(activePlayerEquips);
    }

    public static boolean isQTotem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return false;
        PersistentDataContainerView pdc = stack.getPersistentDataContainer();
        return qTotems.stream().map(QTotem::getKey).anyMatch(pdc::has);
    }

    public static QTotem getQTotem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        return qTotems.stream().filter(qTotem -> {
            PersistentDataContainerView pdc = stack.getPersistentDataContainer();
            return pdc.has(qTotem.getKey());
        }).findFirst().orElse(null);
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
        QSchedulerManager.runAtEntity(player, () -> {
            qTotem.provideEquipEffects(player);
        });
        activePlayerEquips.put(player.getUniqueId(), qTotem);
    }

    public static void handlePop(Player player, ItemStack stack) {
        QTotem qTotem = getQTotem(stack);
        if (qTotem == null) return;
        QSchedulerManager.runAtEntity(player, () -> {
            qTotem.providePopEffects(player);
        });
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
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player == null) return;
            ItemStack stack = player.getInventory().getItemInOffHand();
            QTotem active = activePlayerEquips.get(uuid);
            QTotem newTotem = getQTotem(stack);
            if (active == null) {
                clearPastEffects(player);
                return;
            }
            if (newTotem != active) {
                handleEquip(player, stack);
                return;
            }
            QSchedulerManager.runAtEntity(player, () -> {
                active.provideEquipEffects(player);
        });
    });
    }

    public static QTotem getQTotem(String totemName) {
        return getQTotems().stream().filter(qTotem -> qTotem.getName().equalsIgnoreCase(totemName)).findFirst().orElse(null);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void populate() {
        ConfigManager.getSection("totems").getKeys(false).forEach(qTotem -> {
            try {
                if (!ConfigManager.getBoolean("totems." + qTotem + ".enabled", true)) {
                    return;
                }
                QTotem totem = QTotem.create(qTotem)
                        .displayName(ConfigManager.getString("totems." + qTotem + ".name"))
                        .lore(ConfigManager.getStringList("totems." + qTotem + ".lore"));
                List<String> popEffects = ConfigManager.getStringList("totems." + qTotem + ".popEffects");
                List<String> equipEffects = ConfigManager.getStringList("totems." + qTotem + ".equipEffects");
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
        qTotems.clear();
        new ArrayList<>(activePlayerEquips.keySet()).forEach(uuid -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if (player != null) clearPastEffects(player);
        });
    }

    public static void reload() {
        qTotems.clear();
        populate();
        getActivePlayerEquips().forEach((uuid, _) -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if (player != null) {
                handleEquip(player, player.getInventory().getItemInOffHand());
            }
        });
    }
}
