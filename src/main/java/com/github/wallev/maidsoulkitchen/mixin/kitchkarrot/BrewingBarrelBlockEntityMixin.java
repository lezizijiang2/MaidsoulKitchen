package com.github.wallev.maidsoulkitchen.mixin.kitchkarrot;

import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.ICookBeAccessor;
import io.github.tt432.kitchenkarrot.blockentity.BrewingBarrelBlockEntity;
import io.github.tt432.kitchenkarrot.recipes.recipe.BrewingBarrelRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BrewingBarrelBlockEntity.class, remap = false)
public abstract class BrewingBarrelBlockEntityMixin implements ICookBeAccessor {
    @Shadow
    public abstract RecipeHolder<BrewingBarrelRecipe> getRecipe();

    @Override
    public boolean kl$canCook() {
        return this.getRecipe() != null;
    }
}
