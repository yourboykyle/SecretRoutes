//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;

public enum RoomRouteProvider implements NameableEnum {
    DEFAULT("Default"), FLAME_OF_WAR("FlameOfWar"), THREE_PPOPKA("3ppopka");

    private final String label;

    RoomRouteProvider(String label) {
        this.label = label;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(label);
    }

    public SRMConfig.RouteType resolve(SRMConfig.RouteType global) {
        return switch (this) {
            case DEFAULT -> global == null ? SRMConfig.RouteType.ROUTE_FOW : global;
            case FLAME_OF_WAR -> SRMConfig.RouteType.ROUTE_FOW;
            case THREE_PPOPKA -> SRMConfig.RouteType.ROUTE_3ppopka;
        };
    }
}
//#endif
