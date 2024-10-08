/*
 * Copyright (C) 2022 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.utils.autoupdate;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.Executor;


public class MinecraftExecutor implements Executor {

    public static MinecraftExecutor INSTANCE = new MinecraftExecutor();

    private MinecraftExecutor() {}

    @Override
    public void execute(@NotNull Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}