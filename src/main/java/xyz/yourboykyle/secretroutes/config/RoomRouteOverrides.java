//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import java.util.Locale;
import java.util.Map;

/** Sparse, base-room overrides. Missing entries inherit the global provider. */
public final class RoomRouteOverrides {
    private RoomRouteOverrides() { }

    public static String normalize(String name) {
        if (name == null) return "";
        int separator = name.indexOf(':');
        return (separator < 0 ? name : name.substring(0, separator)).toLowerCase(Locale.ROOT);
    }

    public static RoomRouteProvider get(Map<String, RoomRouteProvider> overrides, String name) {
        if (overrides == null || name == null || normalize(name).equals("f7boss")) return RoomRouteProvider.DEFAULT;
        RoomRouteProvider value = overrides.get(normalize(name));
        return value == null ? RoomRouteProvider.DEFAULT : value;
    }

    public static void set(Map<String, RoomRouteProvider> overrides, String name, RoomRouteProvider value) {
        String key = normalize(name);
        if (key.isEmpty() || key.equals("f7boss")) return;
        if (value == null || value == RoomRouteProvider.DEFAULT) overrides.remove(key);
        else overrides.put(key, value);
    }
}
//#endif
