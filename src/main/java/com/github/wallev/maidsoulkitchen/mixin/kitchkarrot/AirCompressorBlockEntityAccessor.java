package com.github.wallev.maidsoulkitchen.mixin.kitchkarrot;

import io.github.tt432.kitchenkarrot.blockentity.AirCompressorBlockEntity;
import io.github.tt432.kitchenkarrot.recipes.recipe.AirCompressorRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AirCompressorBlockEntity.class, remap = false)
public interface AirCompressorBlockEntityAccessor {
    @Invoker
    RecipeHolder<AirCompressorRecipe> callGetRecipeFromItems();

    @Invoker
    boolean callIsStarted();

    @Invoker
    boolean callHasEnergy();
}
