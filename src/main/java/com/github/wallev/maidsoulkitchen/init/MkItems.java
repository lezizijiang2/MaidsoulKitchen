package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.item.ItemBurnProtectBauble;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MkItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MaidsoulKitchen.MOD_ID);

    public static DeferredItem<Item> BURN_PROTECT_BAUBLE = ITEMS.register("burn_protect_bauble", ItemBurnProtectBauble::new);
    public static DeferredItem<Item> CULINARY_HUB = ITEMS.register("culinary_hub", ItemCulinaryHub::new);
}
