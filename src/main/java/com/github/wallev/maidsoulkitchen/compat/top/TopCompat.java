package com.github.wallev.maidsoulkitchen.compat.top;

import com.github.wallev.maidsoulkitchen.compat.top.event.AddTaskInfoTopEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class TopCompat {
    public static void init() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new AddTaskInfoTopEvent());
        }
    }
}
