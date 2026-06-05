package dev.parrotstudios.qtotems.utils.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface QScheduler {

    QTask runLater(Runnable task, long delay);

    QTask runTimer(Runnable task, long initialDelay, long period);

    QTask run(Runnable task);

    QTask runAsync(Runnable task);

    QTask runAtLocation(Runnable task, Location location);

    QTask runAtLocationLater(Runnable task, Location location, long delay);

    QTask runAtEntity(Entity entity, Runnable task);

    QTask runAtEntityLater(Entity entity, Runnable task, long delay);

    QTask runAtEntityTimer(Entity entity, Runnable task, long initialDelay, long period);

}
