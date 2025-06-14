package com.github.wallev.maidsoulkitchen.mixin.minecraft;

import com.github.wallev.maidsoulkitchen.task.cook.common.cbaccessor.IAbstractFurnaceAccessor;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.ICookBeAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = AbstractFurnaceBlockEntity.class, remap = true)
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity implements IAbstractFurnaceAccessor, ICookBeAccessor {
    @Shadow
    protected NonNullList<ItemStack> items;
    @Shadow
    @Final
    private Object2IntOpenHashMap<ResourceLocation> recipesUsed;
    @Shadow(remap = false)
    @Final
    private RecipeType<? extends AbstractCookingRecipe> recipeType;
    @Shadow
    @Final
    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Shadow
    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, AbstractFurnaceBlockEntity furnace) {
        return false;
    }

    @Shadow
    public abstract List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec);

    @Shadow
    protected abstract boolean isLit();

    public RecipeType<AbstractCookingRecipe> tlmk$getRecipeType() {
        return (RecipeType<AbstractCookingRecipe>) this.recipeType;
    }

    @Override
    public void tlmk$awardExperience(Entity entity) {
        this.getRecipesToAwardAndPopExperience((ServerLevel) entity.level, entity.position());
        this.recipesUsed.clear();
    }

    @Override
    public boolean tlmk$isLit() {
        return this.isLit();
    }

    @Override
    public boolean tlmk$innerCanCook() {
        if (level == null) {
            return false;
        }
        RecipeHolder<?> recipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(this.getItem(0)), level).orElse(null);
        if (recipe == null) {
            return false;
        }
        int maxStackSize = this.getMaxStackSize();
        return canBurn(level.registryAccess(), recipe, this.items, maxStackSize, (AbstractFurnaceBlockEntity) (Object) this);
    }

    @Override
    public boolean kl$canCook() {
        return this.tlmk$innerCanCook();
    }

    @Override
    public boolean kl$matchCookState() {
        return this.tlmk$isLit();
    }
}
