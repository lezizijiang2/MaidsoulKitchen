package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.item.CookBagContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.BerryFarmConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CompatMelonConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.CookConfigContainer;
import com.github.wallev.maidsoulkitchen.inventory.container.maid.FruitFarmConfigContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MkContainer {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(BuiltInRegistries.MENU, MaidsoulKitchen.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CookConfigContainer>> COOK_CONTAINER = CONTAINER_TYPE.register("cook_config_container", () -> CookConfigContainer.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<FruitFarmConfigContainer>> FRUIT_FARM_CONTAINER = CONTAINER_TYPE.register("fruit_farm_config_container", () -> FruitFarmConfigContainer.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<BerryFarmConfigContainer>> BERRY_FARM_CONTAINER = CONTAINER_TYPE.register("berry_farm_config_container", () -> BerryFarmConfigContainer.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<CompatMelonConfigContainer>> COMPAT_MELON_CONFIG_CONTAINER = CONTAINER_TYPE.register("compat_melon_config_container", () -> CompatMelonConfigContainer.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<CookBagContainer>> COOK_BAG_CONTAINER = CONTAINER_TYPE.register("culinary_hub_container", () -> CookBagContainer.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<CookBagConfigContainer>> COOK_BAG_CONFIG_CONTAINER = CONTAINER_TYPE.register("culinary_hub_config_container", () -> CookBagConfigContainer.TYPE);
}
