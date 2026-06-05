package dev.parrotstudios.qtotems.utils.scheduler.wrappers;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.utils.scheduler.QScheduler;
import dev.parrotstudios.qtotems.utils.scheduler.QTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaQSchedulerWrapper implements QScheduler {
    private static final JavaPlugin plugin;

    static {
        plugin = QTotems.getInstance();
    }
    @Override
    public QTask runLater(Runnable task, long delay) {
        return new FoliaQTaskWrapper(plugin.getServer().getGlobalRegionScheduler()
                .runDelayed(plugin, (scheduledTask) -> task.run(), delay));
    }

    @Override
    public QTask runTimer(Runnable task, long initialDelay, long period) {
        return new FoliaQTaskWrapper(plugin.getServer().getGlobalRegionScheduler()
                .runAtFixedRate(plugin, (scheduledTask) -> task.run(), initialDelay, period));
    }

    @Override
    public QTask run(Runnable task) {
        return new FoliaQTaskWrapper(plugin.getServer().getGlobalRegionScheduler()
                .run(plugin, (scheduledTask) -> task.run()));
    }

    @Override
    public QTask runAsync(Runnable task) {
        return new FoliaQTaskWrapper(plugin.getServer().getAsyncScheduler()
                .runNow(plugin, (scheduledTask) -> task.run()));
    }

    @Override
    public QTask runAtLocation(Runnable task, Location location) {
        return new FoliaQTaskWrapper(plugin.getServer().getRegionScheduler()
                .run(plugin, location, (scheduledTask) -> task.run()));
    }

    @Override
    public QTask runAtLocationLater(Runnable task, Location location, long delay) {
        return new FoliaQTaskWrapper(plugin.getServer().getRegionScheduler()
                .runDelayed(plugin, location, (scheduledTask) -> task.run(), delay));
    }

    @Override
    public QTask runAtEntity(Entity entity, Runnable task) {
        return new FoliaQTaskWrapper(entity.getScheduler()
                .run(plugin, (scheduledTask) -> task.run(),null));
    }

    @Override
    public QTask runAtEntityLater(Entity entity, Runnable task, long delay) {
        return new FoliaQTaskWrapper(entity.getScheduler()
                .runDelayed(plugin, (scheduledTask) -> task.run(),null, delay));
    }

    @Override
    public QTask runAtEntityTimer(Entity entity, Runnable task, long initialDelay, long period) {
        return new FoliaQTaskWrapper(entity.getScheduler()
                .runAtFixedRate(plugin, (scheduledTask) -> task.run(),null, initialDelay, period));
    }
}
