package dev.parrotstudios.qtotems.utils.scheduler.wrappers;


import dev.parrotstudios.qtotems.utils.scheduler.QTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

public class FoliaQTaskWrapper implements QTask {

    private final ScheduledTask task;

    public FoliaQTaskWrapper(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {return task.isCancelled() || task.getExecutionState() == ScheduledTask.ExecutionState.CANCELLED;}

    @Override
    public Plugin getOwner() {
        return task.getOwningPlugin();
    }

    @Override
    public Object getNativeTask() {
        return task;
    }

}