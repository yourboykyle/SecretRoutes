package xyz.yourboykyle.secretroutes.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.oneconfig.api.config.v1.Config;

public class NewSRMConfig extends Config {
    public NewSRMConfig(@NotNull String id, @Nullable String iconPath, @NotNull String title, @Nullable Category category) {
        super(id, iconPath, title, category);
    }
}
