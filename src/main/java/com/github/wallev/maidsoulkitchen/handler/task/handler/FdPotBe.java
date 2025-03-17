package com.github.wallev.maidsoulkitchen.handler.task.handler;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.task.cook.handler.MaidRecipesManager;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class FdPotBe<B extends BlockEntity, R extends Recipe<? extends Container>, MR extends MaidRecipesManager<R>> {
    private B blockEntity;
    private IItemHandler inventory;
    private final MR maidRecipesManager;
    private final EntityMaid maid;
    private final Builder<B> cookPotBuilder;

    public FdPotBe(MR maidRecipesManager, Builder<B> cookPotBuilder) {
        this.maidRecipesManager = maidRecipesManager;
        this.cookPotBuilder = cookPotBuilder;

        this.maid= maidRecipesManager.getMaid();
    }

    public boolean canMoveToCookBe() {
        if (this.hasResultItem()) {
            return true;
        }



        return false;
    }

    public void processCookMake() {

    }

    public MR getMaidRecipesManager() {
        return maidRecipesManager;
    }

    public ItemStack getResultItem() {
        return this.cookPotBuilder.getResultItem.apply(blockEntity, inventory);
    }

    public boolean hasResultItem() {
        return !this.getResultItem().isEmpty();
    }

    public ItemStack getDisplayItem() {
        return this.cookPotBuilder.getDisplayItem.apply(blockEntity, inventory);
    }

    public boolean hasDisplayItem() {
        return !this.getDisplayItem().isEmpty();
    }

    public ItemStack getContainerItem() {
        return this.cookPotBuilder.getContainerItem.apply(blockEntity, inventory);
    }

    public boolean hasContainerItem() {
        return !this.getContainerItem().isEmpty();
    }

    public boolean isHeated() {
        return this.cookPotBuilder.isHeated.test(blockEntity);
    }

    public boolean innerCanCook() {
        return this.cookPotBuilder.innerCanCook.test(blockEntity, inventory);
    }

    public boolean hasInputItems() {
        return this.cookPotBuilder.hasInputItems.test(blockEntity, inventory);
    }

    public void initBlockEntity(B blockEntity) {
        this.blockEntity = blockEntity;
        this.inventory = this.cookPotBuilder.getInventory.apply(blockEntity);
    }

    public B getBlockEntity() {
        return blockEntity;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    {
        Builder<CookingPotBlockEntity> fdCookPotBuilder = Builder.<CookingPotBlockEntity>build()
                .setGetInventory(be -> be.getInventory())
                .setGetResultItem((be, inv) -> {
                    return inv.getStackInSlot(CookingPotBlockEntity.OUTPUT_SLOT);
                })
                .setGetDisplayItem((be, inv) -> {
                    return inv.getStackInSlot(CookingPotBlockEntity.MEAL_DISPLAY_SLOT);
                })
                .setGetContainer((be, inv) -> {
                    return be.getContainer();
                })
                .setGetContainerItem((be, inv) -> {
                    return inv.getStackInSlot(CookingPotBlockEntity.CONTAINER_SLOT);
                })
                .setIsHeated(be -> be.isHeated())
                .setInnerCanCook((be, inv) -> {
                    return false;
                })
                .setHasInputItems((be, inv) -> {
                    for (int i = 0; i < 6; i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            return true;
                        }
                    }
                    return false;
                });
    }


    public static class Builder<B extends BlockEntity> {
        private Function<B, IItemHandler> getInventory;
        private BiFunction<B, IItemHandler, ItemStack> getResultItem;
        private BiFunction<B, IItemHandler, ItemStack> getDisplayItem;
        private BiFunction<B, IItemHandler, ItemStack> getContainerItem;
        private BiFunction<B, IItemHandler, ItemStack> getContainer;
        private BiPredicate<B, IItemHandler> hasInputItems;
        private BiPredicate<B, IItemHandler> innerCanCook;
        private Predicate<B> isHeated;
        

        public static <B extends BlockEntity> Builder<B> build() {
            return new Builder<>();
        }

        public Builder<B> setGetInventory(Function<B, IItemHandler> getInventory) {
            this.getInventory = getInventory;
            return this;
        }

        public Builder<B> setGetResultItem(BiFunction<B, IItemHandler, ItemStack> getResultItem) {
            this.getResultItem = getResultItem;
            return this;
        }

        public Builder<B> setGetDisplayItem(BiFunction<B, IItemHandler, ItemStack> getDisplayItem) {
            this.getDisplayItem = getDisplayItem;
            return this;
        }

        public Builder<B> setGetContainer(BiFunction<B, IItemHandler, ItemStack> getContainer) {
            this.getContainer = getContainer;
            return this;
        }

        public Builder<B> setGetContainerItem(BiFunction<B, IItemHandler, ItemStack> getContainerItem) {
            this.getContainerItem = getContainerItem;
            return this;
        }

        public Builder<B> setHasInputItems(BiPredicate<B, IItemHandler> hasInputItems) {
            this.hasInputItems = hasInputItems;
            return this;
        }

        public Builder<B> setInnerCanCook(BiPredicate<B, IItemHandler> innerCanCook) {
            this.innerCanCook = innerCanCook;
            return this;
        }

        public Builder<B> setIsHeated(Predicate<B> isHeated) {
            this.isHeated = isHeated;
            return this;
        }
    }
}
