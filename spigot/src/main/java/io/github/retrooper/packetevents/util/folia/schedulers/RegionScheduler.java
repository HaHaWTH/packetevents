package io.github.retrooper.packetevents.util.folia.schedulers;

import com.github.retrooper.packetevents.util.reflection.Reflection;
import io.github.retrooper.packetevents.util.folia.FoliaCompatUtil;
import io.github.retrooper.packetevents.util.folia.TaskWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Represents a scheduler for executing tasks asynchronously.
 */
public class RegionScheduler {
    private static final boolean isFolia = FoliaCompatUtil.isFolia();

    private static Object regionScheduler;

    private static Method regionExecuteWorldMethod;
    private static Method regionExecuteLocationMethod;
    private static Method regionRunWorldMethod;
    private static Method regionRunLocationMethod;
    private static Method regionRunDelayedWorldMethod;
    private static Method regionRunDelayedLocationMethod;
    private static Method regionRunAtFixedRateWorldMethod;
    private static Method regionRunAtFixedRateLocationMethod;

    static {
        try {
            if (isFolia) {
                Method getRegionSchedulerMethod = Reflection.getMethod(Server.class, "getRegionScheduler", 0);
                regionScheduler = getRegionSchedulerMethod.invoke(Bukkit.getServer());
                Class<?> regionSchedulerClass = regionScheduler.getClass();

                regionExecuteWorldMethod = regionSchedulerClass.getMethod("execute", Plugin.class, World.class, Runnable.class);
                regionExecuteLocationMethod = regionSchedulerClass.getMethod("execute", Plugin.class, Location.class, Runnable.class);
                regionRunWorldMethod = regionSchedulerClass.getMethod("run", Plugin.class, World.class, Consumer.class);
                regionRunLocationMethod = regionSchedulerClass.getMethod("run", Plugin.class, Location.class, Consumer.class);
                regionRunDelayedWorldMethod = regionSchedulerClass.getMethod("runDelayed", Plugin.class, World.class, Consumer.class, long.class);
                regionRunDelayedLocationMethod = regionSchedulerClass.getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, long.class);
                regionRunAtFixedRateWorldMethod = regionSchedulerClass.getMethod("runAtFixedRate", Plugin.class, World.class, Consumer.class, long.class, long.class);
                regionRunAtFixedRateLocationMethod = regionSchedulerClass.getMethod("runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class);
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedules a task to be executed on the region which owns the location.
     *
     * @param plugin The plugin that owns the task
     * @param world  The world of the region that owns the task
     * @param run    The task to execute
     */
    public static void execute(@NotNull Plugin plugin, @NotNull World world, @NotNull Runnable run) {
        if (!isFolia) {
            Bukkit.getScheduler().runTask(plugin, run);
        }

        try {
            regionExecuteWorldMethod.invoke(regionScheduler, plugin, world, run);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedules a task to be executed on the region which owns the location.
     *
     * @param plugin   The plugin that owns the task
     * @param location The location at which the region executing should own
     * @param run      The task to execute
     */
    public static void execute(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable run) {
        if (!isFolia) {
            Bukkit.getScheduler().runTask(plugin, run);
        }

        try {
            regionExecuteLocationMethod.invoke(regionScheduler, plugin, location, run);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param plugin The plugin that owns the task
     * @param world  The world of the region that owns the task
     * @param task   The task to execute
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper run(@NotNull Plugin plugin, @NotNull World world, @NotNull Consumer<Object> task) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
        }

        try {
            return new TaskWrapper(regionRunWorldMethod.invoke(regionScheduler, plugin, world, task));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param plugin   The plugin that owns the task
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper run(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
        }

        try {
            return new TaskWrapper(regionRunLocationMethod.invoke(regionScheduler, plugin, location, task));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param plugin     The plugin that owns the task
     * @param world      The world of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull World world, @NotNull Consumer<Object> task, long delayTicks) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
        }

        try {
            return new TaskWrapper(regionRunDelayedWorldMethod.invoke(regionScheduler, plugin, world, task, delayTicks));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param plugin     The plugin that owns the task
     * @param location   The location at which the region executing should own
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long delayTicks) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
        }

        try {
            return new TaskWrapper(regionRunDelayedLocationMethod.invoke(regionScheduler, plugin, location, task, delayTicks));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the specified period.
     *
     * @param plugin            The plugin that owns the task
     * @param world             The world of the region that owns the task
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull World world, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        try {
            return new TaskWrapper(regionRunAtFixedRateWorldMethod.invoke(regionScheduler, plugin, world, task, initialDelayTicks, periodTicks));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the specified period.
     *
     * @param plugin            The plugin that owns the task
     * @param location          The location at which the region executing should own
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public static TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        try {
            return new TaskWrapper(regionRunAtFixedRateLocationMethod.invoke(regionScheduler, plugin, location, task, initialDelayTicks, periodTicks));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}