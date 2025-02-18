package com.github.wallev.maidsoulkitchen.task.cook.v1.common.bestate;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public interface IBaseCookItemHandlerBe<B extends BlockEntity, R extends Recipe<? extends RecipeInput>> {
    Optional<R> getMatchingRecipe(B be, RecipeWrapper recipeWrapper);

    boolean canCook(B be, R recipe);
}
