package dev.parrotstudios.qtotems.totem;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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


    public static boolean isQTotem(ItemStack stack){
        if (stack == null || !stack.hasItemMeta()) return false;
        PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
        return qTotems.stream().map(QTotem::getKey).anyMatch(pdc::has);
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
        qTotem.get().provideEquipEffects(player);
        activePlayerEquips.put(player.getUniqueId(), qTotem.get());

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

    public static void reload(){
        qTotems.clear();
        activePlayerEquips.clear();
        populate();
    }


}
