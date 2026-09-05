//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoomRouteSettingsTest {
    @Test
    void unconfiguredRoomsFollowEitherGlobalProvider() {
        var overrides = new HashMap<String, RoomRouteProvider>();
        assertEquals(RoomRouteSettings.DEFAULT, new RoomRouteSettings(true, null));
        for (var global : SRMConfig.RouteType.values()) {
            assertEquals(global, RoomRouteOverrides.get(overrides, "room").resolve(global));
            assertEquals(global, RoomRouteOverrides.get(null, "room").resolve(global));
        }
        assertTrue(overrides.isEmpty());
    }

    @Test
    void overridesAreSharedByRoomVariantsAndSurviveGlobalChanges() {
        var overrides = new HashMap<String, RoomRouteProvider>();
        RoomRouteOverrides.set(overrides, "Withermancers-4:1", RoomRouteProvider.THREE_PPOPKA);
        assertEquals(Map.of("withermancers-4", RoomRouteProvider.THREE_PPOPKA), overrides);
        for (var global : SRMConfig.RouteType.values()) {
            assertEquals(SRMConfig.RouteType.ROUTE_3ppopka, RoomRouteOverrides.get(overrides, "WITHERMANCERS-4:2").resolve(global));
            assertEquals(global, RoomRouteOverrides.get(overrides, "other room").resolve(global));
        }
    }

    @Test
    void defaultRemovesTheOverrideAndResumesInheritance() {
        var overrides = new HashMap<String, RoomRouteProvider>();
        RoomRouteOverrides.set(overrides, "room", RoomRouteProvider.FLAME_OF_WAR);
        RoomRouteOverrides.set(overrides, "ROOM:2", RoomRouteProvider.DEFAULT);
        assertTrue(overrides.isEmpty());
        assertEquals(SRMConfig.RouteType.ROUTE_3ppopka,
                RoomRouteOverrides.get(overrides, "room").resolve(SRMConfig.RouteType.ROUTE_3ppopka));
    }

    @Test
    void enableDisableAndProviderResetPreserveTheOtherChoice() {
        var selected = new RoomRouteSettings(true, RoomRouteProvider.THREE_PPOPKA);
        assertEquals(selected, selected.withEnabled(false).withEnabled(true));
        assertEquals(new RoomRouteSettings(false, RoomRouteProvider.DEFAULT),
                selected.withEnabled(false).withProvider(RoomRouteProvider.DEFAULT));
    }

    @Test
    void savedOverridesRoundTripAndUnknownEnumValuesFallBackToDefault() {
        Gson gson = new Gson();
        var type = new TypeToken<Map<String, RoomRouteProvider>>() { }.getType();
        Map<String, RoomRouteProvider> restored = gson.fromJson(gson.toJson(Map.of("room", RoomRouteProvider.FLAME_OF_WAR)), type);
        assertEquals(RoomRouteProvider.FLAME_OF_WAR, RoomRouteOverrides.get(restored, "Room"));
        restored = gson.fromJson("{\"room\":\"FUTURE_PROVIDER\"}", type);
        assertEquals(RoomRouteProvider.DEFAULT, RoomRouteOverrides.get(restored, "room"));
    }

    @Test
    void bossRoutesContinueUsingTheGlobalProvider() {
        var overrides = new HashMap<String, RoomRouteProvider>();
        RoomRouteOverrides.set(overrides, "f7boss", RoomRouteProvider.THREE_PPOPKA);
        assertTrue(overrides.isEmpty());
        overrides.put("f7boss", RoomRouteProvider.THREE_PPOPKA);
        assertEquals(SRMConfig.RouteType.ROUTE_FOW,
                RoomRouteOverrides.get(overrides, "f7boss").resolve(SRMConfig.RouteType.ROUTE_FOW));
    }

    @Test
    void changingInheritanceWithoutChangingEffectiveProviderNeedsNoReload() {
        var global = SRMConfig.RouteType.ROUTE_FOW;
        assertEquals(RoomRouteProvider.DEFAULT.resolve(global), RoomRouteProvider.FLAME_OF_WAR.resolve(global));
        assertNotEquals(RoomRouteProvider.DEFAULT.resolve(global), RoomRouteProvider.THREE_PPOPKA.resolve(global));
        assertEquals(RoomRouteProvider.FLAME_OF_WAR.resolve(global),
                RoomRouteProvider.FLAME_OF_WAR.resolve(SRMConfig.RouteType.ROUTE_3ppopka));
    }
}
//#endif
