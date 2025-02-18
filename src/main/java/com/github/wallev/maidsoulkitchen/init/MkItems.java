package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.github.tartaricacid.touhoulittlemaid.item.ItemDamageableBauble;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MkItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MaidsoulKitchen.MOD_ID);

//    public static RegistryObject<Item> OLD_MAID_BACKPACK_BIG = ITEMS.register("old_maid_backpack_big", ItemMaidBackpack::new);
    public static DeferredItem<Item> BURN_PROTECT_BAUBLE = ITEMS.register("burn_protect_bauble", () -> new ItemDamageableBauble(128));
    public static DeferredItem<Item> CULINARY_HUB = ITEMS.register("culinary_hub", ItemCulinaryHub::new);
}
