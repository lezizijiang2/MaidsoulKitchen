package com.github.wallev.maidsoulkitchen.task.cook.v1.common.cbaccessor;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public interface IVcCbeAccessor<R extends Recipe<? extends RecipeInput>> {

    Optional<R> getMatchingRecipe$tlma(RecipeWrapper inventoryWrapper);

    boolean canCook$tlma(R recipe);

}
