package com.github.wallev.maidsoulkitchen.init.registry;

import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.handler.initializer.CookContainerSerializerRulesManager;
import com.github.wallev.maidsoulkitchen.handler.initializer.CookRecIngredientSerializerManager;
import com.github.wallev.maidsoulkitchen.handler.initializer.CookRecRecipeInitializerManager;
import com.github.wallev.maidsoulkitchen.util.AnnotatedInstanceUtil;

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
        MaidsoulKitchen.EXTENSIONS = AnnotatedInstanceUtil.getModExtensions();
        CookRecRecipeInitializerManager.register();
        CookRecIngredientSerializerManager.register();
        CookContainerSerializerRulesManager.register();
    }
}