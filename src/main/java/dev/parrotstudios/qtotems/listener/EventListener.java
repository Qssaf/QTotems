package dev.parrotstudios.qtotems.listener;

import dev.parrotstudios.qtotems.totem.QTotemRegistry;
import dev.parrotstudios.qtotems.utils.scheduler.QSchedulerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    @EventHandler
    public void onPop(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getHand() == null) return;
        QTotemRegistry.handlePop(player, player.getInventory().getItem(event.getHand()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() != 40) return;
        if (event.getClick() == ClickType.SWAP_OFFHAND || event.getClick() == ClickType.MIDDLE) return;
        if (event.isCancelled()) return;
        Player player = (Player) event.getWhoClicked();
        QSchedulerManager.getScheduler().runLater(() ->
                QTotemRegistry.handleEquip(player, player.getInventory().getItemInOffHand()),1L);
    }

    @EventHandler
    public void onSwapInInventory(InventoryClickEvent event) {
        if (event.getClick() != ClickType.SWAP_OFFHAND) return;
        if (event.getSlot() == 40) return;
        ItemStack stack = event.getCurrentItem();
        QTotemRegistry.handleEquip((Player) event.getWhoClicked(), stack);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        ItemStack itemToOffhand = event.getOffHandItem();
        QTotemRegistry.handleEquip(event.getPlayer(), itemToOffhand);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        QTotemRegistry.handleLeave(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        QTotemRegistry.handleJoin(event.getPlayer());
    }

//    @EventHandler
//    public void onSlotChange(PlayerInventorySlotChangeEvent event) {
//        if (event.getSlot() != 40) return;
//        QTotemRegistry.handleEquip(event.getPlayer(), event.getPlayer().getInventory().getItemInOffHand());
//    }
}
