package com.github.wallev.maidsoulkitchen;

import com.github.wallev.maidsoulkitchen.chest.FarmDelightCabinet;
import com.github.wallev.maidsoulkitchen.chest.HandCraftedContainer;
import com.github.wallev.maidsoulkitchen.config.GeneralConfig;
import com.github.wallev.maidsoulkitchen.foundation.utility.Mods;
import com.github.wallev.maidsoulkitchen.init.MkContainer;
import com.github.wallev.maidsoulkitchen.init.MkEffects;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import com.github.wallev.maidsoulkitchen.init.MkMemories;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.github.wallev.maidsoulkitchen.network.NetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MaidsoulKitchen.MOD_ID)
public final class MaidsoulKitchen {
    public static final String MOD_ID = "maidsoulkitchen";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public MaidsoulKitchen(IEventBus modEventBus, ModContainer modContainer) {
        initRegister(modEventBus);
        initConfigureRegister(modContainer);
    }

    private void initRegister(IEventBus modEventBus) {
        MkItems.ITEMS.register(modEventBus);
        MkEffects.EFFECTS.register(modEventBus);
        MkContainer.CONTAINER_TYPE.register(modEventBus);
        MkMemories.MEMORY_MODULE_TYPES.register(modEventBus);
        ItemCulinaryHub.DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(NetworkHandler::registerPacket);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void initConfigureRegister(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, GeneralConfig.init());
    }

    // 箱子兼容
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // 农夫乐事橱柜
        if (Mods.FD.isLoaded()) {
            FarmDelightCabinet.registerCapabilities(event);
        }
        // handcrafted容器
        if (Mods.HANDCRAFTED.isLoaded()) {
            HandCraftedContainer.registerCapabilities(event);
        }
    }

}
