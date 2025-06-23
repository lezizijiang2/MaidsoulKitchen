package com.github.wallev.maidsoulkitchen.compat.jade;

import com.github.wallev.maidsoulkitchen.compat.jade.event.AddTaskInfoJadeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class JadeCompat {
    public static void init() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new AddTaskInfoJadeEvent());
        }
    }
}
