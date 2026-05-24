package dev.parrotstudios.qtotems.totem;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class QTotemRegistry {
    private static final List<QTotem> qTotems = new ArrayList<>();

    private static final HashMap<UUID,QTotem> activePlayerEquips = new HashMap<>();

    public static List<QTotem> getQTotems(){
        return new ArrayList<>(qTotems);
    }

    public static void add(QTotem qTotem){
        qTotems.add(qTotem);
    }

    public static List<String> getTotemNames(){
        return qTotems.stream().map(QTotem::getName).toList();
    }

    public static Map<UUID,QTotem> getActivePlayerEquips(){
        return Map.copyOf(activePlayerEquips);
    }

    public static boolean isQTotem(ItemStack stack){
        if (stack == null || !stack.hasItemMeta()) return false;
        PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
        return qTotems.stream().map(QTotem::getKey).anyMatch(pdc::has);
    }

    private static NamespacedKey getActiveEffectsKey() {
        return new NamespacedKey(QTotems.getInstance(), "active_effects");
    }

    public static void clearPastEffects(Player player){
        QTotem active = activePlayerEquips.get(player.getUniqueId());
        activePlayerEquips.remove(player.getUniqueId());
        if (active != null) {
            try{
                active.removeEquipEffects(player);
            }
            catch(Exception e){
                player.clearActivePotionEffects();
                e.printStackTrace();
            }
        }
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = getActiveEffectsKey();
        if (pdc.has(key, PersistentDataType.STRING)) {
            String activeEffectsStr = pdc.get(key, PersistentDataType.STRING);
            if (activeEffectsStr != null && !activeEffectsStr.isEmpty()) {
                String[] effectNames = activeEffectsStr.split(";");
                for (String name : effectNames) {
                    PotionEffectType type = org.bukkit.Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(name.toLowerCase()));
                    if (type != null && player.hasPotionEffect(type)) {
                        PotionEffect activeEffect = player.getPotionEffect(type);
                        if (activeEffect != null && activeEffect.getDuration() > 720000) {
                            player.removePotionEffect(type);
                        }
                    }
                }
            }
            pdc.remove(key);
        }
    }

    public static void handleEquip(Player player, ItemStack stack){
        clearPastEffects(player);
        if(!isQTotem(stack)) {
            return;
        }
        Optional<QTotem> qTotem = qTotems.stream().filter(qTotem1 -> {
            PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
            return pdc.has(qTotem1.getKey(), PersistentDataType.BOOLEAN);
        }).findFirst();
        if(qTotem.isEmpty()) return;

        QTotem totem = qTotem.get();
        totem.provideEquipEffects(player);
        activePlayerEquips.put(player.getUniqueId(), totem);

        List<String> effectNames = new ArrayList<>();
        totem.getEquipEffects().forEach(effect -> effectNames.add(effect.getType().getKey().getKey()));
        if (!effectNames.isEmpty()) {
            player.getPersistentDataContainer().set(getActiveEffectsKey(), PersistentDataType.STRING, String.join(";", effectNames));
        }
    }

    public static void handlePop(Player player, ItemStack stack){
        if(!isQTotem(stack)) {
            return;
        }
        Optional<QTotem> qTotem = qTotems.stream().filter(qTotem1 -> {
            PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
            return pdc.has(qTotem1.getKey(), PersistentDataType.BOOLEAN);
        }).findFirst();
        if(qTotem.isEmpty()) return;
        QTotems.getInstance().getServer().getScheduler().runTaskLater(QTotems.getInstance(),()->
                qTotem.get().providePopEffects(player),1L);
        activePlayerEquips.remove(player.getUniqueId(),qTotem.get());
    }

    public static void handleLeave(Player player){
        clearPastEffects(player);
        activePlayerEquips.remove(player.getUniqueId());
    }

    public static void handleJoin(Player player){
        clearPastEffects(player);
        ItemStack stack = player.getInventory().getItemInOffHand();
        if(!isQTotem(stack)) {
            return;
        }
        Optional<QTotem> qTotem = qTotems.stream().filter(qTotem1 -> {
            PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
            return pdc.has(qTotem1.getKey(), PersistentDataType.BOOLEAN);
        }).findFirst();
        if(qTotem.isEmpty()) return;
        qTotem.get().provideEquipEffects(player);
        activePlayerEquips.put(player.getUniqueId(), qTotem.get());

    }

    public static void handleEffectChange(Player player){
        if(!activePlayerEquips.containsKey(player.getUniqueId())) return;
        activePlayerEquips.remove(player.getUniqueId());
        ItemStack stack = player.getInventory().getItemInOffHand();
        if(!isQTotem(stack)) {
            return;
        }
        Optional<QTotem> qTotem = qTotems.stream().filter(qTotem1 -> {
            PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
            return pdc.has(qTotem1.getKey(), PersistentDataType.BOOLEAN);
        }).findFirst();
        if(qTotem.isEmpty()) return;
        QTotems.getInstance().getServer().getScheduler().runTaskLater(QTotems.getInstance(),()->
                qTotem.get().provideEquipEffects(player),1L);
        activePlayerEquips.put(player.getUniqueId(), qTotem.get());
    }

    public static QTotem getTotem(String totemName){
        return getQTotems().stream().filter(qTotem ->  qTotem.getName().equals(totemName)).findFirst().orElse(null);
    }

    public static void populate(){
        ConfigManager.getSection("totems").getKeys(false).forEach(qTotem -> {
            try{
                if(!ConfigManager.getBoolean("totems."+qTotem+".enabled",true)) {
                    return;
                }
                QTotem totem = QTotem.create(qTotem)
                        .displayName(ConfigManager.getString("totems."+qTotem+".name"))
                        .lore(ConfigManager.getStringList("totems."+qTotem+".lore"));
                List<String> popEffects =  ConfigManager.getStringList("totems."+qTotem+".popEffects");
                List<String> equipEffects =  ConfigManager.getStringList("totems."+qTotem+".equipEffects");
                popEffects.forEach(effect -> {
                    String[] split = effect.split(";");
                    totem.addPopEffect(split[0].toLowerCase(), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                });
                equipEffects.forEach(effect -> {
                    String[] split = effect.split(";");
                    totem.addEquipEffect(split[0].toLowerCase(), Integer.parseInt(split[1]));
                });
                totem.register();
                QTotems.getInstance().getLogger().info("Registered Qtotem: "+ qTotem);
            } catch (Exception e) {
                QTotems.getInstance().getLogger().warning("Invalid configuration for Qtotem: "+ qTotem);
                e.printStackTrace();
            }

        });
    }

    public static void handleDisable(){
        qTotems.clear();
        getActivePlayerEquips().forEach((uuid, qTotem) -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if(player != null){
                qTotem.removeEquipEffects(player);
            }
        });
        activePlayerEquips.clear();
    }

    public static void reload(){
        qTotems.clear();
        populate();
        getActivePlayerEquips().forEach((uuid, _) -> {
            Player player = QTotems.getInstance().getServer().getPlayer(uuid);
            if(player != null){
                handleEquip(player,player.getInventory().getItemInOffHand());
            }
        });

    }
}
