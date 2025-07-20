package com.github.wallev.maidsoulkitchen.mixin.compat.brewinandchewin;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskMixin;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.ICookBeAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@TaskMixin(value = TaskInfo.BNC_KEY)
@Mixin(value = KegBlockEntity.class, remap = false)
public abstract class KegBlockEntityMixin implements ICookBeAccessor {

    @Shadow
    @Final
    private KegRecipeWrapper recipeWrapper;
    @Shadow
    @Final
    private Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;

    @Shadow
    protected abstract Optional<RecipeHolder<KegFermentingRecipe>> getMatchingRecipe(KegRecipeWrapper inventoryWrapper);

    @Shadow
    public abstract AbstractedItemHandler getInventory();

    @Shadow
    protected abstract boolean canFerment(KegFermentingRecipe recipe, KegBlockEntity keg);

    @Shadow
    public abstract List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos);

    @Override
    public boolean kl$canCook() {
        return this.getMatchingRecipe(this.recipeWrapper)
                .map(r -> this.canFerment(r.value(), this.kl$cast()))
                .orElse(false);
    }

    @Override
    public void kl$getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        this.getUsedRecipesAndPopExperience(level, pos);
    }

    @Override
    public Map<ResourceLocation, Integer> kl$usedRecipeTracker() {
        return usedRecipeTracker;
    }

}
