package com.github.wallev.maidsoulkitchen.init.registry;

import com.github.wallev.maidsoulkitchen.network.NetworkHandler;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class CommonRegistry {
    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(CommonRegistry::modApiInit);
    }

    private static void modApiInit() {
    }
}