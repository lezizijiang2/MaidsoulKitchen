package com.github.wallev.maidsoulkitchen.compat.patchouli;

import com.github.wallev.maidsoulkitchen.compat.patchouli.event.OpenPatchouliBookEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class PatchouliCompat {
    public static void init() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new OpenPatchouliBookEvent());
        }
    }
}
