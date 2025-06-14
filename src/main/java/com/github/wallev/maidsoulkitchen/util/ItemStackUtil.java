package com.github.wallev.maidsoulkitchen.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemStackUtil {
    private static final Map<Item, ItemStack> CACHE;
    private static List<ItemStack> DEFAULT_FUELS;

    static {
        Map<Item, ItemStack> cache = new HashMap<>();
        for (Item value : BuiltInRegistries.ITEM) {
            ItemStack itemStack = value.getDefaultInstance();
            cache.put(value, itemStack);
        }
        CACHE = cache;
    }

    private ItemStackUtil() {
    }

    public static void init() {
    }

    /**
     * 仅仅获取ItemStack实例，万万不能修改，只作用于不用修改ItemStack的情况下。
     */
    public static ItemStack getItemStack(Item item) {
        return CACHE.computeIfAbsent(item, ItemStack::new);
    }

    public static boolean isItem(List<ItemStack> itemStacks, Item item) {
        for (ItemStack itemStack : itemStacks) {
            if (itemStack.is(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isItem(List<ItemStack> itemStacks, ItemStack itemStack) {
        return isItem(itemStacks, itemStack.getItem());
    }

    public static ItemStack item2Inv(ItemStack itemStack, IItemHandler inv) {
        ItemStack copy = itemStack.copy();
        ItemStack leftStack = ItemHandlerHelper.insertItemStacked(inv, copy, false);
        itemStack.shrink(copy.getCount() - leftStack.getCount());
        return itemStack;
    }

    public static List<ItemStack> getDefaultFuels() {
        if (DEFAULT_FUELS == null) {
            int lastBurnTime = 0;
            List<ItemStack> fuels = new ArrayList<>();
            for (Item value : BuiltInRegistries.ITEM) {
                ItemStack itemStack = value.getDefaultInstance();
                int burnTime = itemStack.getBurnTime(null);
                if (burnTime > 0) {
                    if (burnTime > lastBurnTime) {
                        fuels.add(0, itemStack);
                    } else {
                        fuels.add(itemStack);
                    }
                    lastBurnTime = burnTime;
                }
            }
            DEFAULT_FUELS = fuels;
        }

        return DEFAULT_FUELS;
    }

    public static List<ItemStack> getFurnaceFuels() {
        int lastBurnTime = 0;
        List<ItemStack> fuels = new ArrayList<>();
        for (Item value : BuiltInRegistries.ITEM) {
            ItemStack itemStack = value.getDefaultInstance();
            int burnTime = itemStack.getBurnTime(null);
            if (burnTime > 0) {
                if (burnTime > lastBurnTime) {
                    fuels.add(0, itemStack);
                } else {
                    fuels.add(itemStack);
                }
                lastBurnTime = burnTime;
            }
        }
        return fuels;
    }
}
