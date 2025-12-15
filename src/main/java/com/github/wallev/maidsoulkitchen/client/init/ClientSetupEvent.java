package com.github.wallev.maidsoulkitchen.client.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.client.event.SlotRenderAndTipsHandler;
import com.github.wallev.maidsoulkitchen.client.overlay.CulinaryHubOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.CROSSHAIR;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = MaidsoulKitchen.MOD_ID)
public final class ClientSetupEvent {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SlotRenderAndTipsHandler::init);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(CROSSHAIR, ResourceLocation.fromNamespaceAndPath(MaidsoulKitchen.MOD_ID, "culinary_hub_tips"), new CulinaryHubOverlay());
    }
}