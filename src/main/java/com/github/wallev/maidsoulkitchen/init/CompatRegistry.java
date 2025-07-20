package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.compat.cloth.ClothCompat;
import com.github.wallev.maidsoulkitchen.compat.jade.JadeCompat;
import com.github.wallev.maidsoulkitchen.compat.patchouli.PatchouliCompat;
import com.github.wallev.maidsoulkitchen.compat.top.TopCompat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class CompatRegistry {
    @SubscribeEvent
    public static void onEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(() -> checkModLoad(Mods.CLOTH_CONFIG, ClothCompat::init));
        event.enqueueWork(() -> checkModLoad(Mods.PATCHOULI, PatchouliCompat::init));
        event.enqueueWork(() -> checkModLoad(Mods.JADE, JadeCompat::init));
        event.enqueueWork(() -> checkModLoad(Mods.TOP, TopCompat::init));
    }

    private static void checkModLoad(Mods mod, Runnable runnable) {
        if (mod.load()) {
            runnable.run();
        }
    }
}
