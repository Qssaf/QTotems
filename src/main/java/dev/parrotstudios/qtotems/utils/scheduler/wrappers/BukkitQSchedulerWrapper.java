package dev.parrotstudios.qtotems.utils.scheduler.wrappers;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.utils.scheduler.QScheduler;
import dev.parrotstudios.qtotems.utils.scheduler.QTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitQSchedulerWrapper implements QScheduler {

    private static final JavaPlugin plugin;

    static {
        plugin = QTotems.getInstance();
    }

    @Override
    public QTask runLater(Runnable task, long delay) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskLater(plugin, task, delay));
    }

    @Override
    public QTask runTimer(Runnable task, long initialDelay, long period) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskTimer(plugin, task, initialDelay, period));
    }

    @Override
    public QTask run(Runnable task) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTask(plugin, task));
    }

    @Override
    public QTask runAsync(Runnable task) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, task));
    }

    @Override
    public QTask runAtLocation(Runnable task, Location location) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTask(plugin, task));
    }

    @Override
    public QTask runAtLocationLater(Runnable task, Location location, long delay) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskLater(plugin, task, delay));
    }

    @Override
    public QTask runAtEntity(Entity entity, Runnable task) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTask(plugin, task));
    }

    @Override
    public QTask runAtEntityLater(Entity entity, Runnable task, long delay) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskLater(plugin, task, delay));
    }

    @Override
    public QTask runAtEntityTimer(Entity entity, Runnable task, long initialDelay, long period) {
        return new BukkitQTaskWrapper(plugin.getServer().getScheduler()
                .runTaskTimer(plugin, task, initialDelay, period));
    }
}
