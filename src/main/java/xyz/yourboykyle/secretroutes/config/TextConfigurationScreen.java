//#if FABRIC
package xyz.yourboykyle.secretroutes.config;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static xyz.yourboykyle.secretroutes.config.SRMConfig.TextColor;

final class TextConfigurationScreen {
    private TextConfigurationScreen() {
    }

    static ButtonOption button(String componentName, Binding<Boolean> show, Binding<TextColor> color, Binding<Float> size) {
        return button(componentName, show, color, size, null);
    }

    static ButtonOption button(String componentName, Binding<Boolean> show, Binding<TextColor> color, Binding<Float> size, Binding<Boolean> numbering) {
        return ButtonOption.createBuilder()
                .name(Component.literal("Text Configuration"))
                .description(OptionDescription.of(Component.literal("Opens text settings for " + componentName + " waypoints")))
                .text(Component.literal("Configure..."))
                .action((parent, option) -> {
                    // Build fresh pending values on each visit, leaving the parent's pending box settings intact.
                    var screen = YetAnotherConfigLib.createBuilder()
                            .title(Component.literal(componentName + " Text Configuration"))
                            .category(category(componentName, show, color, size, numbering))
                            .save(SRMConfig.HANDLER::save)
                            .build()
                            .generateScreen(parent);
                    Minecraft.getInstance().setScreenAndShow(screen);
                })
                .build();
    }

    static ConfigCategory category(String componentName, Binding<Boolean> show, Binding<TextColor> color, Binding<Float> size, Binding<Boolean> numbering) {
        var category = ConfigCategory.createBuilder()
                .name(Component.literal(componentName + " Text"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show"))
                        .description(OptionDescription.of(Component.literal("Shows labels on " + componentName + " waypoints")))
                        .binding(show)
                        .controller(TickBoxControllerBuilder::create)
                        .build());
        if (numbering != null) {
            category.option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Numbering"))
                    .description(OptionDescription.of(Component.literal("Adds a sequence number to each route label")))
                    .binding(numbering)
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        }
        return category
                .option(Option.<TextColor>createBuilder()
                        .name(Component.literal("Color"))
                        .description(OptionDescription.of(Component.literal("Sets the color of " + componentName + " labels")))
                        .binding(color)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class))
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.literal("Size"))
                        .description(OptionDescription.of(Component.literal("Controls the size of " + componentName + " labels")))
                        .binding(size)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f))
                        .build())
                .build();
    }
}
//#endif
