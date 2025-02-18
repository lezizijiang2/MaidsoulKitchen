package com.github.wallev.maidsoulkitchen.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public final class ItemUtil {
    private ItemUtil() {
    }

    /**
     * 获取物品Id
     */
    public static String getId(Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key.toString();
    }

    public static String getId(ItemStack stack) {
        return getId(stack.getItem());
    }
}
