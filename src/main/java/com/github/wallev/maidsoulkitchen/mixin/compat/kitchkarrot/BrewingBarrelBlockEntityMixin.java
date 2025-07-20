package com.github.wallev.maidsoulkitchen.mixin.compat.kitchkarrot;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskMixin;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.ICookBeAccessor;
import io.github.tt432.kitchenkarrot.blockentity.BrewingBarrelBlockEntity;
import io.github.tt432.kitchenkarrot.recipes.recipe.BrewingBarrelRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@TaskMixin(task = TaskInfo.KK_BREW_BARREL)
@Mixin(value = BrewingBarrelBlockEntity.class, remap = false)
public abstract class BrewingBarrelBlockEntityMixin implements ICookBeAccessor {
    @Shadow
    public abstract RecipeHolder<BrewingBarrelRecipe> getRecipe();

    @Override
    public boolean kl$canCook() {
        return this.getRecipe() != null;
    }
}
