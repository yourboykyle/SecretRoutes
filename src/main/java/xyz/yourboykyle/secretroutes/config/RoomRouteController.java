//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownController;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/** One YACL option, with native checkbox and dropdown widgets editing its pending value. */
public record RoomRouteController(Option<RoomRouteSettings> option) implements Controller<RoomRouteSettings> {
    @Override
    public Component formatValue() {
        RoomRouteSettings value = option.pendingValue();
        return Component.literal(value.enabled() ? "Enabled, " : "Disabled, ").append(value.provider().getDisplayName());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> dimension) {
        return new RoomWidget(this, screen, dimension);
    }

    record Controls(Option<Boolean> enabled, Option<RoomRouteProvider> provider) { }

    static Controls controlsFor(Option<RoomRouteSettings> room) {
        // Each field is a view of the parent pending value, with no separate saved state.
        Option<Boolean> enabled = Option.<Boolean>createBuilder()
                .name(Component.empty())
                .stateManager(new RoomFieldState<>(room, RoomRouteSettings::enabled, RoomRouteSettings::withEnabled))
                .controller(TickBoxControllerBuilder::create)
                .build();
        Option<RoomRouteProvider> provider = Option.<RoomRouteProvider>createBuilder()
                .name(Component.empty())
                .stateManager(new RoomFieldState<>(room, RoomRouteSettings::provider, RoomRouteSettings::withProvider))
                .customController(RoomProviderDropdown::new)
                .build();
        return new Controls(enabled, provider);
    }

    static final class RoomProviderDropdown extends EnumDropdownController<RoomRouteProvider> {
        RoomProviderDropdown(Option<RoomRouteProvider> option) {
            super(option, RoomRouteProvider::getDisplayName);
        }

        @Override
        protected Stream<String> getValidEnumConstants(String value) {
            // This is a three-choice selector: never filter out providers using the current label.
            return getAllowedValues().stream();
        }

        @Override
        public boolean isValueValid(String value) {
            return getAllowedValues().stream().anyMatch(label -> label.equalsIgnoreCase(value));
        }
    }

    private static final class RoomFieldState<T> implements StateManager<T> {
        private final Option<RoomRouteSettings> room;
        private final Function<RoomRouteSettings, T> read;
        private final BiFunction<RoomRouteSettings, T, RoomRouteSettings> write;
        private StateListener<T> listener = StateListener.noop();

        RoomFieldState(Option<RoomRouteSettings> room, Function<RoomRouteSettings, T> read,
                       BiFunction<RoomRouteSettings, T, RoomRouteSettings> write) {
            this.room = room;
            this.read = read;
            this.write = write;
            room.stateManager().addListener((oldValue, newValue) -> {
                T before = read.apply(oldValue), after = read.apply(newValue);
                if (!Objects.equals(before, after)) listener.onStateChange(before, after);
            });
        }

        @Override public void set(T value) { room.requestSet(write.apply(room.pendingValue(), value)); }
        @Override public T get() { return read.apply(room.pendingValue()); }
        @Override public void apply() { /* Only the parent room option commits to config. */ }
        @Override public void sync() { /* The parent owns cancel; get() always reflects it. */ }
        @Override public boolean isSynced() { return true; }
        @Override public boolean isAlwaysSynced() { return true; }
        @Override public boolean isDefault() { return Objects.equals(get(), read.apply(RoomRouteSettings.DEFAULT)); }
        @Override public void resetToDefault(ResetAction action) { set(read.apply(RoomRouteSettings.DEFAULT)); }
        @Override public void addListener(StateListener<T> value) { listener = listener.andThen(value); }
    }

    private static final class RoomWidget extends ControllerWidget<RoomRouteController> implements ContainerEventHandler {
        private final AbstractWidget enabled;
        private final AbstractWidget provider;
        private GuiEventListener focusedChild;
        private boolean dragging;

        RoomWidget(RoomRouteController controller, YACLScreen screen, Dimension<Integer> dimension) {
            super(controller, screen, dimension);
            Controls controls = controlsFor(controller.option());
            enabled = controls.enabled().controller().provideWidget(screen, dimension);
            provider = controls.provider().controller().provideWidget(screen, dimension);
            layout();
        }

        private int providerWidth() {
            return Math.min(110, Math.max(65, getDimension().width() / 3));
        }

        private void layout() {
            if (enabled == null || provider == null) return;
            Dimension<Integer> dim = getDimension();
            int height = Math.max(16, dim.height() - 4);
            int right = dim.xLimit() - 3;
            provider.setDimension(Dimension.ofInt(right - providerWidth(), dim.y() + 2, providerWidth(), height));
            enabled.setDimension(Dimension.ofInt(right - providerWidth() - 26, dim.y() + 2, 22, height));
        }

        @Override
        public void setDimension(Dimension<Integer> dimension) {
            super.setDimension(dimension);
            layout();
        }

        @Override
        protected int getHoveredControlWidth() {
            return providerWidth() + 29;
        }

        @Override
        protected int getUnhoveredControlWidth() {
            return getHoveredControlWidth();
        }

        @Override
        protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            enabled.extractRenderState(graphics, mouseX, mouseY, delta);
            provider.extractRenderState(graphics, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(enabled, provider);
        }

        @Override
        public GuiEventListener getFocused() {
            return focusedChild;
        }

        @Override
        public void setFocused(GuiEventListener child) {
            if (focusedChild == child) return;
            if (focusedChild instanceof AbstractWidget previous) previous.unfocus();
            focusedChild = child;
            focused = child != null;
            if (child != null) child.setFocused(true);
        }

        @Override
        public void setFocused(boolean value) {
            if (!value) unfocus();
            else if (focusedChild == null) setFocused(enabled);
        }

        @Override
        public void unfocus() {
            enabled.unfocus();
            provider.unfocus();
            focusedChild = null;
            focused = false;
            dragging = false;
        }

        @Override
        public boolean isDragging() {
            return dragging;
        }

        @Override
        public void setDragging(boolean value) {
            dragging = value;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return isAvailable() && ContainerEventHandler.super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return ContainerEventHandler.super.mouseReleased(event);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double x, double y) {
            return ContainerEventHandler.super.mouseDragged(event, x, y);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            return isAvailable() && ContainerEventHandler.super.keyPressed(event);
        }

        @Override
        public boolean charTyped(CharacterEvent event) {
            return isAvailable() && ContainerEventHandler.super.charTyped(event);
        }

        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return isAvailable() ? ContainerEventHandler.super.nextFocusPath(event) : null;
        }

        @Override
        public ScreenRectangle getRectangle() {
            Dimension<Integer> dim = getDimension();
            return new ScreenRectangle(dim.x(), dim.y(), dim.width(), dim.height());
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            super.updateNarration(output);
            output.add(NarratedElementType.TITLE, control.option().name().copy().append(": ").append(control.formatValue()));
            output.add(NarratedElementType.USAGE, Component.literal(
                    "Tab between enable checkbox and route provider. Space toggles enable; Enter opens provider choices. Default follows the main Route Type."));
        }
    }
}
//#endif
