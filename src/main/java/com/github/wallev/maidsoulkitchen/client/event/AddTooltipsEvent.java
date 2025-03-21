package com.github.wallev.maidsoulkitchen.client.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID, value = Dist.CLIENT)
public class AddTooltipsEvent {

    @SubscribeEvent
    public static void addTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> components = event.getToolTip();

        if (itemStack.is(MkItems.BURN_PROTECT_BAUBLE.get())) {
            components.add(Component.empty());
            components.add(Component.translatable("tooltips.maidsoulkitchen.burn_protect_bauble.desc.function").withStyle(ChatFormatting.GREEN));
            components.add(Component.translatable("tooltips.maidsoulkitchen.burn_protect_bauble.desc.function.1").withStyle(ChatFormatting.GRAY));
        }
    }

}
