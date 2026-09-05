//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

/** Both controls share one YACL pending value, so apply/cancel/reset are atomic per row. */
public record RoomRouteSettings(boolean enabled, RoomRouteProvider provider) {
    public static final RoomRouteSettings DEFAULT = new RoomRouteSettings(true, RoomRouteProvider.DEFAULT);

    public RoomRouteSettings {
        if (provider == null) provider = RoomRouteProvider.DEFAULT;
    }

    public RoomRouteSettings withEnabled(boolean value) {
        return new RoomRouteSettings(value, provider);
    }

    public RoomRouteSettings withProvider(RoomRouteProvider value) {
        return new RoomRouteSettings(enabled, value);
    }
}
//#endif
