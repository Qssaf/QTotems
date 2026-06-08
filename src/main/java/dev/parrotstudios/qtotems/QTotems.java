package dev.parrotstudios.qtotems;

import dev.parrotstudios.qtotems.command.QTotemsCommand;
import dev.parrotstudios.qtotems.config.ConfigManager;
import dev.parrotstudios.qtotems.listener.EventListener;
import dev.parrotstudios.qtotems.totem.QTotemRegistry;
import dev.parrotstudios.qtotems.utils.scheduler.QSchedulerManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class QTotems extends JavaPlugin {

    @Getter
    private static final ConfigManager configManager = new ConfigManager();

    @Getter
    private static QTotems instance;

    @Override
    public void onEnable() {
        instance = this;
        String schedulersUsed = QSchedulerManager.isFolia() ? "Using Folia Schedulers" : "Using Bukkit Schedulers";
        getLogger().info(schedulersUsed);
        configManager.loadConfigs();
        getLogger().info("Loaded configs.");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Objects.requireNonNull(getCommand("totems")).setExecutor(new QTotemsCommand());
        QTotemRegistry.populate();
        //dumbahh logic if some autistic guy decides to use plugMan(very unadvised)
        getServer().getOnlinePlayers().forEach(player ->
                QTotemRegistry.handleEquip(player, player.getInventory().getItemInOffHand()));
        QTotemRegistry.startUp();
        getLogger().info("Plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        QTotemRegistry.handleDisable();
        getLogger().info("Plugin has been disabled.");
    }
}
