package com.github.wallev.maidsoulkitchen;

import com.github.wallev.maidsoulkitchen.config.GeneralConfig;
import com.github.wallev.maidsoulkitchen.init.MkContainer;
import com.github.wallev.maidsoulkitchen.init.MkEffects;
import com.github.wallev.maidsoulkitchen.init.MkEntities;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.github.wallev.maidsoulkitchen.network.NetworkHandler;
import com.github.wallev.maidsoulkitchen.util.modanalysis.ModCompatibilityAnalyzer;
import com.github.wallev.maidsoulkitchen.util.modutility.Mods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MaidsoulKitchen.MOD_ID)
public final class MaidsoulKitchen {
    public static boolean DEBUG = !FMLEnvironment.production;
    public static final String MOD_ID = "maidsoulkitchen";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public MaidsoulKitchen(IEventBus modEventBus, ModContainer modContainer) {
        initRegister(modEventBus, modContainer);
        initConfigureRegister(modEventBus, modContainer);
        
        // Initialize enhanced mod compatibility system from upstream 1.20.1
        modEventBus.addListener(this::onCommonSetup);
    }
    
    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Initializing enhanced mod compatibility analysis system");
            
            // Initialize mod detection and analysis
            Mods.init();
            
            // Log mod compatibility status
            logModCompatibilityStatus();
            
            // Initialize legacy compatibility if needed
            initLegacyCompatibility();
        });
    }
    
    private void logModCompatibilityStatus() {
        LOGGER.info("=== Enhanced Mod Compatibility Status ===");
        LOGGER.info("Kitchen Karrot: {} (Legacy: {})", Mods.KK.isLoaded, Mods.KK_LEGACY.versionLoaded);
        LOGGER.info("Youkai's Homecoming: {} (Legacy: {})", Mods.YHCD.isLoaded, Mods.YHCD_LEGACY.versionLoaded);
        LOGGER.info("Brewin' and Chewin': {} (Legacy: {})", Mods.BNCD.isLoaded, Mods.BNCD_LEGACY.versionLoaded);
        LOGGER.info("Farmer's Delight: {}", Mods.FD.isLoaded);
        LOGGER.info("Barbeque's Delight: {}", Mods.BD.isLoaded);
    }
    
    private void initLegacyCompatibility() {
        // Check if any legacy mods need special handling
        boolean hasLegacyMods = Mods.KK_LEGACY.versionLoaded || 
                               Mods.YHCD_LEGACY.versionLoaded || 
                               Mods.BNCD_LEGACY.versionLoaded;
        
        if (hasLegacyMods) {
            LOGGER.info("Legacy mod compatibility enabled - enhanced analysis active");
            
            // Pre-analyze common classes for better performance
            if (Mods.KK_LEGACY.versionLoaded) {
                ModCompatibilityAnalyzer.analyzeClass("io.github.tt432.kitchenkarrot.blockentity.BrewingBarrelBlockEntity");
            }
        }
    }

    private static void initRegister(IEventBus modEventBus, ModContainer modContainer) {
        MkItems.ITEMS.register(modEventBus);
        MkEffects.EFFECTS.register(modEventBus);
        MkContainer.CONTAINER_TYPE.register(modEventBus);
        MkEntities.MEMORY_MODULE_TYPES.register(modEventBus);
        ItemCulinaryHub.DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(NetworkHandler::registerPacket);
    }

    private static void initConfigureRegister(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, GeneralConfig.init());
    }
}
