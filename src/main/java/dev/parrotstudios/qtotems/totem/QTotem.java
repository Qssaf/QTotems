package dev.parrotstudios.qtotems.totem;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
public class QTotem {
    private final String name;
    private final ItemStack totemItem;
    private final ItemMeta totemMeta;
    private final NamespacedKey key;

    private final List<PotionEffect> equipEffects = new ArrayList<>();
    private final List<PotionEffect> popEffects = new ArrayList<>();

    public static QTotem create(String name){
        return new QTotem(name);
    }

    private QTotem(String name){
        this.name = name;
        totemItem = new ItemStack(Material.TOTEM_OF_UNDYING);
        totemMeta = totemItem.getItemMeta();
        key = new NamespacedKey(QTotems.getInstance(), name);
        totemMeta.setEnchantmentGlintOverride(true);
        totemMeta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
    }

    public String getName() {
        return name;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public ItemStack getTotemItem(){
        return totemItem.clone();
    }

    public List<PotionEffect> getEquipEffects() {
        return List.copyOf(equipEffects);
    }

    public List<PotionEffect> getPopEffects() {
        return List.copyOf(popEffects);
    }

    public QTotem displayName(String name){
        totemMeta.displayName(Utils.text(name));
        return this;
    }

    public QTotem lore(List<String> lore){
        List<Component> loreFormat = lore.stream().map(Utils::text).toList();
        totemMeta.lore(loreFormat);
        return this;
    }

    public QTotem addEquipEffect(String potionEffectName, int level){
        PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(potionEffectName));
        if(type == null){
            QTotems.getInstance().getLogger().warning("Invalid pop effect name: " + potionEffectName + " for totem: " + this.getName());
            return this;
        }
        equipEffects.add(new PotionEffect(type, Integer.MAX_VALUE, level,false,false,true));
        return this;
    }

    public QTotem addPopEffect(String potionEffectName, int level, int duration){
        PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(potionEffectName));
        if(type == null){
            QTotems.getInstance().getLogger().warning("Invalid pop effect name: " + potionEffectName + " for totem: " + this.getName());
            return this;
        }
        popEffects.add(new PotionEffect(type, duration, level,false,false,true));
        return this;
    }

    public void provideEquipEffects(Player player){
        this.getEquipEffects().forEach(player::addPotionEffect);
    }

    public void providePopEffects(Player player){
        this.getPopEffects().forEach(player::addPotionEffect);
    }

    public void removeEquipEffects(Player player){
        this.getEquipEffects().forEach(effect -> {
            if(!player.hasPotionEffect(effect.getType())) return;
            if(Objects.requireNonNull(player.getPotionEffect(effect.getType())).getAmplifier() > effect.getAmplifier()) return;
            player.removePotionEffect(effect.getType());
        });
    }

    public void register(){
        totemItem.setItemMeta(totemMeta);
        QTotemRegistry.add(this);
    }
}
