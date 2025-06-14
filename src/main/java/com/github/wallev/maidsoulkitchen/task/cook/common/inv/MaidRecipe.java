package com.github.wallev.maidsoulkitchen.task.cook.common.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * MaidRecipe class represents a recipe along with the available ingredients.
 *
 * @param recipe               the recipe holder
 * @param availableIngredients a map of available ingredients with their quantities
 * @param <R>                  the type of recipe, extending Recipe
 */
public record MaidRecipe<R extends Recipe<?>>(RecipeHolder<R> recipe, List<Pair<Item, Integer>> availableIngredients) {
    public static <R extends Recipe<?>> MaidRecipe<R> empty() {
        return new MaidRecipe<>(null, List.of());
    }

    public boolean isEmpty() {
        return recipe == null || availableIngredients.isEmpty();
    }

    /**
     * 转换为旧格式的 Pair，用于向后兼容
     */
    public Pair<List<Integer>, List<Item>> toLegacyFormat() {
        if (isEmpty()) {
            return Pair.of(List.of(), List.of());
        }

        List<Integer> counts = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        for (Pair<Item, Integer> entry : availableIngredients) {
            items.add(entry.getFirst());
            counts.add(entry.getSecond());
        }

        return Pair.of(counts, items);
    }

}
