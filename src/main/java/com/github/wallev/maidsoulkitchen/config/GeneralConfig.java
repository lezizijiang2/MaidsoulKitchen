package com.github.wallev.maidsoulkitchen.config;

import com.github.wallev.maidsoulkitchen.config.subconfig.RegisterConfig;
import com.github.wallev.maidsoulkitchen.config.subconfig.TaskConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GeneralConfig {
    public static ModConfigSpec init() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        TaskConfig.init(builder);
        RegisterConfig.init(builder);
        return builder.build();
    }
}
