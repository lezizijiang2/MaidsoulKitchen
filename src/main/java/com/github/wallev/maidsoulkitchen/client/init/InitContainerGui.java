package com.github.wallev.maidsoulkitchen.client.init;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.EmptyBackpackContainerScreen;
import com.github.tartaricacid.touhoulittlemaid.init.InitContainer;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.cook.CookConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.BerryFarmConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.CompatMelonConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.FruitFarmConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.item.CookBagConfigContainerGui;
import com.github.wallev.maidsoulkitchen.client.gui.item.CookBagGui;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.BerryFarmConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CompatMelonConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CookConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.FruitFarmConfigContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class InitContainerGui {
    @SubscribeEvent
    public static void clientSetup(RegisterMenuScreensEvent evt) {
        evt.register(CookConfigContainer.TYPE, CookConfigGui::new);
        evt.register(BerryFarmConfigContainer.TYPE, BerryFarmConfigGui::new);
        evt.register(FruitFarmConfigContainer.TYPE, FruitFarmConfigGui::new);
        evt.register(CompatMelonConfigContainer.TYPE, CompatMelonConfigGui::new);
        evt.register(CookBagContainer.TYPE, CookBagGui::new);
        evt.register(CookBagConfigContainer.TYPE, CookBagConfigContainerGui::new);
    }
}
