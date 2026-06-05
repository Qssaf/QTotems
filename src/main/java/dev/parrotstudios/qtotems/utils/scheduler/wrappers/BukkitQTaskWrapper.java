package dev.parrotstudios.qtotems.utils.scheduler.wrappers;



import dev.parrotstudios.qtotems.utils.scheduler.QTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitQTaskWrapper implements QTask {

    private final BukkitTask task;

    public BukkitQTaskWrapper(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public Plugin getOwner() {
        return task.getOwner();
    }

    @Override
    public Object getNativeTask() {
        return task;
    }


}