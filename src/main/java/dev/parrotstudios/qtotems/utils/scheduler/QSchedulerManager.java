package dev.parrotstudios.qtotems.utils.scheduler;


import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.utils.scheduler.wrappers.BukkitQSchedulerWrapper;
import dev.parrotstudios.qtotems.utils.scheduler.wrappers.FoliaQSchedulerWrapper;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is a manager for all methods and functions related to task schedulers, adding Folia support and removing the need keep using "try catch" statements to use the right scheduler.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class QSchedulerManager {

    @Getter
    private static final QScheduler scheduler;

    @Getter
    private static final boolean isFolia;

    @Getter
    private static final JavaPlugin plugin;

    private QSchedulerManager() {
    }

    private static boolean checkIfHasFoliaSchedulers() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static{
        plugin = QTotems.getInstance();
        isFolia = checkIfHasFoliaSchedulers();
        if(isFolia) {
            scheduler = new FoliaQSchedulerWrapper();
        } else {
            scheduler = new BukkitQSchedulerWrapper();
        }
    }

}