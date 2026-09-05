//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import dev.isxander.yacl3.api.Option;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomRouteControllerStateTest {
    @Test
    void dropdownOffersEveryProviderAndSelectsEachWithoutCommittingUntilApply() {
        var saved = new AtomicReference<>(RoomRouteSettings.DEFAULT);
        var room = option(saved);
        var controls = RoomRouteController.controlsFor(room);
        var dropdown = (RoomRouteController.RoomProviderDropdown) controls.provider().controller();
        var labels = List.of("Default", "FlameOfWar", "3ppopka");
        for (var provider : RoomRouteProvider.values()) {
            String label = provider.getDisplayName().getString();
            dropdown.setFromString(label);
            assertEquals(provider, room.pendingValue().provider());
            assertEquals(labels, dropdown.getValidEnumConstants(dropdown.getString()).toList());
            assertTrue(dropdown.isValueValid(label));
            assertEquals(RoomRouteSettings.DEFAULT, saved.get());
        }
        assertFalse(dropdown.isValueValid("missing"));
        room.applyValue();
        assertEquals(RoomRouteProvider.THREE_PPOPKA, saved.get().provider());
    }

    @Test
    void childControlsOnlyCommitWhenParentApplies() {
        var saved = new AtomicReference<>(RoomRouteSettings.DEFAULT);
        var room = option(saved);
        var controls = RoomRouteController.controlsFor(room);
        controls.enabled().requestSet(false);
        controls.provider().requestSet(RoomRouteProvider.THREE_PPOPKA);
        assertEquals(RoomRouteSettings.DEFAULT, saved.get());
        assertEquals(new RoomRouteSettings(false, RoomRouteProvider.THREE_PPOPKA), room.pendingValue());
        assertTrue(room.applyValue());
        assertEquals(room.pendingValue(), saved.get());
        assertFalse(room.applyValue());
    }

    @Test
    void cancelRestoresBothChildControlsWithoutWritingConfig() {
        var initial = new RoomRouteSettings(false, RoomRouteProvider.FLAME_OF_WAR);
        var saved = new AtomicReference<>(initial);
        var room = option(saved);
        var controls = RoomRouteController.controlsFor(room);
        controls.enabled().requestSet(true);
        controls.provider().requestSet(RoomRouteProvider.DEFAULT);
        room.forgetPendingValue();
        assertEquals(initial, saved.get());
        assertFalse(controls.enabled().pendingValue());
        assertEquals(RoomRouteProvider.FLAME_OF_WAR, controls.provider().pendingValue());
    }

    @Test
    void currentRoomShortcutAndGroupRowSharePendingEditsAndBulkChanges() {
        var saved = new AtomicReference<>(RoomRouteSettings.DEFAULT);
        var room = option(saved);
        var shortcut = RoomRouteController.controlsFor(room);
        var group = RoomRouteController.controlsFor(room);
        shortcut.provider().requestSet(RoomRouteProvider.FLAME_OF_WAR);
        assertEquals(RoomRouteProvider.FLAME_OF_WAR, group.provider().pendingValue());
        group.enabled().requestSet(false);
        assertFalse(shortcut.enabled().pendingValue());
        room.requestSet(room.pendingValue().withEnabled(true));
        assertTrue(shortcut.enabled().pendingValue());
        assertTrue(group.enabled().pendingValue());
        assertEquals(RoomRouteProvider.FLAME_OF_WAR, room.pendingValue().provider());
        room.requestSet(room.pendingValue().withProvider(RoomRouteProvider.DEFAULT));
        assertEquals(RoomRouteProvider.DEFAULT, group.provider().pendingValue());
        assertEquals(RoomRouteSettings.DEFAULT, saved.get());
    }

    @Test
    void resetToDefaultUpdatesBothControlsAndStillNeedsApply() {
        var initial = new RoomRouteSettings(false, RoomRouteProvider.THREE_PPOPKA);
        var saved = new AtomicReference<>(initial);
        var room = option(saved);
        var controls = RoomRouteController.controlsFor(room);
        room.requestSetDefault();
        assertTrue(controls.enabled().pendingValue());
        assertEquals(RoomRouteProvider.DEFAULT, controls.provider().pendingValue());
        assertEquals(initial, saved.get());
        room.applyValue();
        assertEquals(RoomRouteSettings.DEFAULT, saved.get());
    }

    private static Option<RoomRouteSettings> option(AtomicReference<RoomRouteSettings> saved) {
        return Option.<RoomRouteSettings>createBuilder()
                .name(Component.literal("Test room"))
                .binding(RoomRouteSettings.DEFAULT, saved::get, saved::set)
                .customController(RoomRouteController::new)
                .build();
    }
}
//#endif
