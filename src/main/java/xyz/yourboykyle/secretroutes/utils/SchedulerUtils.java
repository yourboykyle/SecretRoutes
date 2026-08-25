//#if FABRIC
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Shared scheduler for delayed actions.
 * <p>
 * Previously every delayed action allocated its own {@link Thread} that spent nearly all of its
 * lifetime inside {@link Thread#sleep(long)}. Room loads and chat events can fire many times per
 * run, so that pattern created thousands of short-lived threads per session. A single daemon
 * scheduler does the same job without the per-event thread allocation.
 */
public class SchedulerUtils {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "SecretRoutes-Scheduler");
        thread.setDaemon(true);
        return thread;
    });

    private SchedulerUtils() {
    }

    /**
     * Runs {@code task} after {@code delayMs} milliseconds on the shared scheduler thread.
     * <p>
     * The task is <b>not</b> run on the client thread. Callers that touch game state must hop back
     * themselves, e.g. via {@code Minecraft.getInstance().execute(...)}.
     *
     * @param delayMs delay in milliseconds
     * @param task    the action to run
     */
    public static void schedule(long delayMs, Runnable task) {
        SCHEDULER.schedule(() -> {
            try {
                task.run();
            } catch (Exception ex) {
                LogUtils.error(ex);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }
}
//#endif
