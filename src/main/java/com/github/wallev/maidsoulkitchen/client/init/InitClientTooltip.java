package com.github.wallev.maidsoulkitchen.client.init;

import com.github.wallev.maidsoulkitchen.client.tooltip.NormalAmountTooltip;
import com.github.wallev.maidsoulkitchen.client.tooltip.RecipeDataTooltip;
import com.github.wallev.maidsoulkitchen.inventory.tooltip.AmountTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class InitClientTooltip {
    @SubscribeEvent
    public static void onRegisterClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AmountTooltip.class, NormalAmountTooltip::new);
        event.register(RecipeDataTooltip.TooltipRecipeData.class, RecipeDataTooltip::new);
//        if (Mods.CP.isLoaded()) {
//            event.register(CrockPotTooltip.class, CrockPotAmountTooltip::new);
//        }
    }
}
