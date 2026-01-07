package xyz.yourboykyle.secretroutes.utils.skyblocker;

/*
 * This file incorporates code from Skyblocker, licensed under the GNU Lesser General Public License v3.0.
 * Original Copyright: de.hysky (Skyblocker)
 * License: https://www.gnu.org/licenses/lgpl-3.0.en.html
 */

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

public class DungeonConstants {
    public static final byte RED_COLOR = 18;
    public static final byte GREEN_COLOR = 30;

    public static final Object2ByteMap<String> NUMERIC_ID = new Object2ByteOpenHashMap<>();

    static {
        NUMERIC_ID.defaultReturnValue((byte) 0);
        NUMERIC_ID.put("minecraft:stone", (byte) 1);
        NUMERIC_ID.put("minecraft:diorite", (byte) 2);
        NUMERIC_ID.put("minecraft:polished_diorite", (byte) 3);
        NUMERIC_ID.put("minecraft:andesite", (byte) 4);
        NUMERIC_ID.put("minecraft:polished_andesite", (byte) 5);
        NUMERIC_ID.put("minecraft:grass_block", (byte) 6);
        NUMERIC_ID.put("minecraft:dirt", (byte) 7);
        NUMERIC_ID.put("minecraft:coarse_dirt", (byte) 8);
        NUMERIC_ID.put("minecraft:cobblestone", (byte) 9);
        NUMERIC_ID.put("minecraft:bedrock", (byte) 10);
        NUMERIC_ID.put("minecraft:oak_leaves", (byte) 11);
        NUMERIC_ID.put("minecraft:gray_wool", (byte) 12);
        NUMERIC_ID.put("minecraft:stone_slab", (byte) 13);
        NUMERIC_ID.put("minecraft:smooth_stone", (byte) 13);
        NUMERIC_ID.put("minecraft:smooth_stone_slab", (byte) 13);
        NUMERIC_ID.put("minecraft:double_stone_slab", (byte) 13); // Kept for safety
        NUMERIC_ID.put("minecraft:mossy_cobblestone", (byte) 14);
        NUMERIC_ID.put("minecraft:clay", (byte) 15);
        NUMERIC_ID.put("minecraft:stone_bricks", (byte) 16);
        NUMERIC_ID.put("minecraft:mossy_stone_bricks", (byte) 17);
        NUMERIC_ID.put("minecraft:chiseled_stone_bricks", (byte) 18);
        NUMERIC_ID.put("minecraft:gray_terracotta", (byte) 19);
        NUMERIC_ID.put("minecraft:cyan_terracotta", (byte) 20);
        NUMERIC_ID.put("minecraft:black_terracotta", (byte) 21);
    }
}