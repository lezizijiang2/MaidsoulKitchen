package com.github.wallev.maidsoulkitchen.compat.cloth;

import com.github.wallev.maidsoulkitchen.compat.cloth.event.AddClothConfigEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class ClothCompat {
    public static void init(ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MenuIntegration.registerModsPage(modContainer);
            NeoForge.EVENT_BUS.register(new AddClothConfigEvent());
        }
    }
}
