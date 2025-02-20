package com.github.wallev.maidsoulkitchen.task.cook.v1.common.cbaccessor;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.Optional;

public interface IFdCbeAccessor<R extends Recipe<? extends RecipeInput>> {

    Optional<R> tlmk$getMatchingRecipe(RecipeWrapper inventoryWrapper);

    boolean tlmk$canCook(R recipe);

}
