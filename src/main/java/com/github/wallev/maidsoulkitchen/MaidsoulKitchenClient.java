package com.github.wallev.maidsoulkitchen;

import com.github.tartaricacid.touhoulittlemaid.init.registry.CompatRegistry;
import com.github.wallev.maidsoulkitchen.compat.cloth.ClothCompat;
import com.github.wallev.maidsoulkitchen.compat.cloth.MenuIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MaidsoulKitchen.MOD_ID, dist = Dist.CLIENT)
public class MaidsoulKitchenClient {
    public MaidsoulKitchenClient(IEventBus modEventBus, ModContainer modContainer) {
        this.registerClientOnly();
        this.registerConfigMenu(modContainer);
    }

    private void registerClientOnly() {
        // 这个仅用于客户端，所以不需要在服务端注册
    }

    private void registerConfigMenu(ModContainer modContainer) {
        ModFileInfo clothConfigInfo = LoadingModList.get().getModFileById(CompatRegistry.CLOTH_CONFIG);
        if (clothConfigInfo != null) {
            MenuIntegration.registerModsPage(modContainer);
            ClothCompat.init(modContainer);
        } else {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
