package com.github.wallev.maidsoulkitchen.config.subconfig;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RenderConfig {
    public static ModConfigSpec.BooleanValue LD_BANNER_RENDER_ENABLED;

    public static void init(ModConfigSpec.Builder builder) {
        builder.push("Render");

        builder.comment("Maid can render LdBanner.");
        LD_BANNER_RENDER_ENABLED = builder.define("LdBannerRenderEnabled", true);

        builder.pop();
    }
}
