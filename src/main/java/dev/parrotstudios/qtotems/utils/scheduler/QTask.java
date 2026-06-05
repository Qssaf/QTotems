package dev.parrotstudios.qtotems.utils.scheduler;



import org.bukkit.plugin.Plugin;

public interface QTask {
    /**
     * Cancels the task.
     */
    void cancel();

    /**
     * Returns true if the task has been canceled.
     */
    boolean isCancelled();

    /**
     * Gets the plugin that owns this task.
     */
    Plugin getOwner();

    /**
     * Returns the raw, underlying task object (BukkitTask or ScheduledTask).
     * You can use this to cast to the specific platform's task if you need
     * a platform-exclusive feature (like Bukkit's getTaskId()).
     */
    Object getNativeTask();


}