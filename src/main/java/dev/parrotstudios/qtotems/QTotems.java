package dev.parrotstudios.qtotems;

import dev.parrotstudios.qtotems.command.QTotemsCommand;
import dev.parrotstudios.qtotems.config.ConfigManager;
import dev.parrotstudios.qtotems.listener.EventListener;
import dev.parrotstudios.qtotems.totem.QTotemRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class QTotems extends JavaPlugin {

    public static QTotems getInstance(){
        return JavaPlugin.getPlugin(QTotems.class);
    }

    @Override
    public void onEnable() {
        ConfigManager.init(this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Objects.requireNonNull(getCommand("totems")).setExecutor(new QTotemsCommand());
        QTotemRegistry.populate();
        //dumbahh logic if some autistic guy decides to use plugMan(very unadvised)
        getServer().getOnlinePlayers().forEach(player ->
                QTotemRegistry.handleEquip(player, player.getInventory().getItemInOffHand()));

        getLogger().info("Plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        QTotemRegistry.handleDisable();
        getLogger().info("Plugin has been disabled.");
    }
}
