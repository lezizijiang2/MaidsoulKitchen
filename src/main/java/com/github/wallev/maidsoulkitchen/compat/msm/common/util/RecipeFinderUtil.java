package com.github.wallev.maidsoulkitchen.compat.msm.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class RecipeFinderUtil {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R extends Recipe<? extends Container>> List<R> getCutterRecipe(Level level, ResourceLocation recipeType, ItemStack itemStack) {
        RecipeType value = BuiltInRegistries.RECIPE_TYPE.get(recipeType);

        if (value != null) {
            return level.getRecipeManager().getRecipesFor(
                    value,
                    convert2Container(itemStack),
                    level
            );
        }

        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static <C extends Container> C convert2Container(ItemStack itemStack) {
        return (C) new SimpleContainer(itemStack);
    }

}
