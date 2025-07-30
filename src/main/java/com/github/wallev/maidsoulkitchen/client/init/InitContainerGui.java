package com.github.wallev.maidsoulkitchen.client.init;

import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.cook.CookConfigGuiV1;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.BerryFarmConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.CompatMelonConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.farm.FruitFarmConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.item.CookBagConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.item.CookBagContainerGui;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.BerryFarmConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CompatMelonConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CookConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.FruitFarmConfigContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class InitContainerGui {
    @SubscribeEvent
    public static void clientSetup(RegisterMenuScreensEvent evt) {
        evt.register(CookConfigContainer.TYPE, CookConfigGuiV1::new);
        evt.register(BerryFarmConfigContainer.TYPE, BerryFarmConfigGui::new);
        evt.register(FruitFarmConfigContainer.TYPE, FruitFarmConfigGui::new);
        evt.register(CompatMelonConfigContainer.TYPE, CompatMelonConfigGui::new);
        evt.register(CookBagContainer.TYPE, CookBagContainerGui::new);
        evt.register(CookBagConfigContainer.TYPE, CookBagConfigGui::new);
    }
}
