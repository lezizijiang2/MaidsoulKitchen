package com.github.wallev.maidsoulkitchen.item;

import com.github.tartaricacid.touhoulittlemaid.item.ItemDamageableBauble;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemBurnProtectBauble extends ItemDamageableBauble {
    public ItemBurnProtectBauble() {
        super(128);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
