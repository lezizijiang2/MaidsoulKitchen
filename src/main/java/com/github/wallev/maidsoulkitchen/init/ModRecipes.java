package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.Mods;
import com.github.wallev.maidsoulkitchen.recipe.itemuse.ItemUseRecipe;
import com.github.wallev.maidsoulkitchen.recipe.water.ConsumeWaterRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MaidsoulKitchen.MOD_ID);

    public static Supplier<RecipeSerializer<ConsumeWaterRecipe>> CONSUME_WATER_SERIALIZER;
    public static Supplier<RecipeSerializer<ItemUseRecipe>> ITEM_USE_SERIALIZER;

    public static RecipeType<ConsumeWaterRecipe> CONSUME_WATER_RECIPE;
    public static RecipeType<ItemUseRecipe> ITEM_USE_RECIPE;

    static {
        if (Mods.MAID_STORAGE_MANAGER.versionLoad()) {
            CONSUME_WATER_SERIALIZER = RECIPE_SERIALIZERS.register("consume_water", ConsumeWaterRecipe.Serializer::new);
            ITEM_USE_SERIALIZER = RECIPE_SERIALIZERS.register("item_use", ItemUseRecipe.Serializer::new);
        }
    }

    @SubscribeEvent
    public static void register(RegisterEvent evt) {
        if (evt.getRegistryKey().equals(Registries.RECIPE_SERIALIZER) && Mods.MAID_STORAGE_MANAGER.versionLoad()) {
            CONSUME_WATER_RECIPE = RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MaidsoulKitchen.MOD_ID, "consume_water"));
            ITEM_USE_RECIPE = RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MaidsoulKitchen.MOD_ID, "item_use"));
        }
    }
}
