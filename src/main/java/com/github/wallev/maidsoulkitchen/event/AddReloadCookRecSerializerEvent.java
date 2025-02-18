package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.handler.initializer.CookRecRecipeInitializerManager;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;


@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID)
public class AddReloadCookRecSerializerEvent {

    @SubscribeEvent
    public static void addReloadCookRecSerializer(AddReloadListenerEvent event) {

//        CookRecipeSerializerManager.initialSerializerData();
    }

    @SubscribeEvent
    public static void loadWorldIn(ServerStartedEvent event) {
        CookRecRecipeInitializerManager.initializerData(event.getServer().getLevel(Level.OVERWORLD));
        int a = 1;
//        CookRecipeSerializerManager.initialSerializerData();
    }
}
