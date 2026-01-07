/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
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

import net.minecraft.client.MinecraftClient;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public class HashingUtils {
    private static UUID hashedUUID = null;

    public static UUID getHashedUUID() {
        if (hashedUUID == null) {
            try {
                hashedUUID = bytesToUUID(computeSHA256(uuidToBytes(MinecraftClient.getInstance().player.getUuid())));
            } catch (NoSuchAlgorithmException e) {
                LogUtils.error(e);
            } catch (NullPointerException e) {
                LogUtils.info("HOW ??????");
                LogUtils.error(e);
            }

        }
        return hashedUUID;
    }


    private static byte[] uuidToBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] uuidBytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (msb >>> 8 * (7 - i));
            uuidBytes[8 + i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return uuidBytes;
    }

    private static byte[] computeSHA256(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input);
    }

    private static UUID bytesToUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
            lsb = (lsb << 8) | (bytes[8 + i] & 0xff);
        }
        return new UUID(msb, lsb);
    }
}
