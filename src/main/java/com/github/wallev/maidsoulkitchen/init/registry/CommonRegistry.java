package com.github.wallev.maidsoulkitchen.init.registry;

import com.github.wallev.maidsoulkitchen.debug.EnableMaidDebug;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class CommonRegistry {
    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
//        event.enqueueWork(NetworkHandler::init);
        event.enqueueWork(() -> {
            if (!FMLEnvironment.production) {
                NeoForge.EVENT_BUS.register(new EnableMaidDebug());
            }
        });
    }
}