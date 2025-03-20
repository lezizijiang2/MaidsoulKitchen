package com.github.wallev.maidsoulkitchen.task.cook.v1.brewinandchewin;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.api.task.v1.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.init.registry.CommonRegistry;
import com.github.wallev.maidsoulkitchen.init.touhoulittlemaid.RegisterData;
import com.github.wallev.maidsoulkitchen.task.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.ai.MaidCookMakeTask;
import com.github.wallev.maidsoulkitchen.task.cook.handler.MaidRecipesManager;
import com.github.wallev.maidsoulkitchen.task.cook.v1.common.cbaccessor.ICbeAccessor;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import umpaz.brewinandchewin.client.utility.BnCFluidItemDisplays;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCBlocks;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.util.*;
import java.util.stream.Collectors;

import static umpaz.brewinandchewin.common.block.entity.KegBlockEntity.isValidTemp;

public class TaskBncKeg implements ICookTask<KegBlockEntity, KegFermentingRecipe> {
    // 配方所需的流体对应的itemStacks和原材料
    protected static final Map<KegFermentingRecipe, MaidKegRecipe> KEG_RECIPE_INGREDIENTS = new HashMap<>();
    // 流体容器
    protected static final Map<Fluid, List<ItemStack>> FLUID_CONTAINERS = new HashMap<>();

    private static BlockPos getSearchPos(EntityMaid maid) {
        return maid.hasRestriction() ? maid.getRestrictCenter() : maid.blockPosition().below();
    }

    @Override
    public boolean isCookBE(BlockEntity blockEntity) {
        return blockEntity instanceof KegBlockEntity;
    }

    @Override
    public RecipeType<KegFermentingRecipe> getRecipeType() {
        return BnCRecipeTypes.FERMENTING;
    }

    @Override
    public boolean shouldMoveTo(ServerLevel serverLevel, EntityMaid maid, KegBlockEntity kegBlockEntity, MaidRecipesManager<KegFermentingRecipe> recManager) {
        AbstractedItemHandler inventory = kegBlockEntity.getInventory();
        KegFermentingRecipesManager kegFermentingRecipesManager = (KegFermentingRecipesManager) recManager;

        // 输出槽有未取出的物品
        if (!inventory.getStackInSlot(KegBlockEntity.OUTPUT_SLOT).isEmpty()) {
            return true;
        }

        boolean innerCanCook = ((ICbeAccessor) kegBlockEntity).tlmk$innerCanCook();

        // 存在输出流体，待容器取出
        Fluid outputFluid = kegBlockEntity.getOutput().fluid();
        List<ItemStack> outputFluidContainers = FLUID_CONTAINERS.getOrDefault(outputFluid, Collections.emptyList());
        if (!innerCanCook && recManager.hasOutputAdditionItem(itemStack -> outputFluidContainers.stream().anyMatch(stack -> stack.is(itemStack.getItem())))) {
            return true;
        }

        // 容器内部没有符合烹饪的原材料&&仓库存在可以烹饪的原材料

        if (!innerCanCook && kegFermentingRecipesManager.hasRecipeIngredientsWithTemp(kegBlockEntity.getTemperature())) {
            return true;
        }

        // 容器内部没有符合烹饪的原材料&&容器内部存在余下的材料
        boolean hasInput = false;
        if (!innerCanCook) {
            for (int i = 0; i < inventory.getSlotCount(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    hasInput = true;
                    break;
                }
            }
        }
        if (!innerCanCook && hasInput) {
            return true;
        }

        // 容器内部有流体并且仓库存在流体容器以及没在烹饪
        Fluid fluid = kegBlockEntity.getFluidTank().getAbstractedFluid().fluid();
        List<ItemStack> fluidContainers = FLUID_CONTAINERS.getOrDefault(fluid, Collections.emptyList());
        if (!innerCanCook && recManager.hasOutputAdditionItem(itemStack -> fluidContainers.stream().anyMatch(stack -> stack.is(itemStack.getItem())))) {
            return true;
        }

        return false;
    }

    @Override
    public void processCookMake(ServerLevel serverLevel, EntityMaid maid, KegBlockEntity kegBlockEntity, MaidRecipesManager<KegFermentingRecipe> recManager) {
        KegFermentingRecipesManager kegFermentingRecipesManager = (KegFermentingRecipesManager) recManager;

        AbstractedItemHandler inventory = kegBlockEntity.getInventory();
        IItemHandlerModifiable inputInv = recManager.getInputInv();
        IItemHandlerModifiable outputInv = recManager.getOutputInv();
        IItemHandlerModifiable outputAdditionInv = recManager.getOutputAdditionInv();

        // 输出槽有未取出的物品
        ItemStack output = inventory.getStackInSlot(KegBlockEntity.OUTPUT_SLOT);
        if (!output.isEmpty()) {
            ItemStack outputCopy = output.copy();
            ItemStack leftItemStack = ItemHandlerHelper.insertItemStacked(outputInv, outputCopy, false);
            output.shrink(outputCopy.getCount() - leftItemStack.getCount());

            kegBlockEntity.setChanged();
        }

        // 存在容器
        ItemStack container = inventory.getStackInSlot(KegBlockEntity.CONTAINER_SLOT);
        if (!container.isEmpty()) {
            ItemStack containerCopy = container.copy();
            ItemStack leftItemStack = ItemHandlerHelper.insertItemStacked(outputAdditionInv, containerCopy, false);
            container.shrink(containerCopy.getCount() - leftItemStack.getCount());

            kegBlockEntity.setChanged();
        }

        boolean innerCanCook = ((ICbeAccessor) kegBlockEntity).tlmk$innerCanCook();

        // 存在输出流体，待容器取出
        Fluid outputFluid = kegBlockEntity.getOutput().fluid();
        List<ItemStack> outputFluidContainers = FLUID_CONTAINERS.getOrDefault(outputFluid, Collections.emptyList());
        ItemStack outputAdditionItem = recManager.findOutputAdditionItem(itemStack -> outputFluidContainers.stream().anyMatch(stack -> stack.is(itemStack.getItem())));
        if (!innerCanCook && !outputAdditionItem.isEmpty()) {

            ItemStack copy = outputAdditionItem.copy();
            outputAdditionItem.setCount(0);
            List<ItemStack> extracted = kegBlockEntity.extractInWorld(copy, copy.getCount(), false);
            extracted.add(copy);
            for (ItemStack stack : extracted) {
                ItemStack leftInsertedStack = ItemHandlerHelper.insertItemStacked(inputInv, stack, false);
                if (!leftInsertedStack.isEmpty()) {
                    maid.spawnAtLocation(leftInsertedStack);
                }
            }
        }

//         容器内部没有符合烹饪的原材料&&容器内部存在余下的材料
        boolean hasInput = false;
        if (!innerCanCook) {
            for (int i = 0; i < inventory.getSlotCount(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ItemStack copy = stack.copy();
                    ItemStack leftItemStack = ItemHandlerHelper.insertItemStacked(inputInv, copy, false);
                    stack.shrink(copy.getCount() - leftItemStack.getCount());
                    if (!stack.isEmpty()) {
                        hasInput = true;
                    }
                }
            }

            kegBlockEntity.setChanged();
        }
        pickupAction(maid);

//         容器内部没有符合烹饪的原材料&&仓库存在可以烹饪的原材料
        if (!innerCanCook && !hasInput && kegBlockEntity.getFluidTank().isEmpty() && kegFermentingRecipesManager.hasRecipeIngredientsWithTemp(kegBlockEntity.getTemperature())) {
            Pair<List<Integer>, List<List<ItemStack>>> recipeIngredient = kegFermentingRecipesManager.getRecipeIngredient(kegBlockEntity.getTemperature());
            if (hasEnoughIngredient(recipeIngredient.getFirst(), recipeIngredient.getSecond())) {

                int amount = recipeIngredient.getFirst().get(0);
                for (ItemStack itemStack : recipeIngredient.getSecond().get(0)) {
                    if (itemStack.isEmpty()) continue;
                    int count = itemStack.getCount();

                    if (count >= amount) {
                        List<ItemStack> extracted = kegBlockEntity.extractInWorld(itemStack.copyWithCount(amount), amount, false);
                        if (extracted.isEmpty()) return;

                        itemStack.shrink(amount);
                        for (ItemStack stack : extracted) {
                            ItemStack leftInsertedStack = ItemHandlerHelper.insertItemStacked(inputInv, stack, false);
                            if (!leftInsertedStack.isEmpty()) {
                                maid.spawnAtLocation(leftInsertedStack);
                            }
                        }
                        break;
                    } else {
                        List<ItemStack> extracted = kegBlockEntity.extractInWorld(itemStack.copyWithCount(count), count, false);
                        itemStack.shrink(count);
                        for (ItemStack stack : extracted) {
                            ItemStack leftInsertedStack = ItemHandlerHelper.insertItemStacked(inputInv, stack, false);
                            if (!leftInsertedStack.isEmpty()) {
                                maid.spawnAtLocation(leftInsertedStack);
                            }
                        }
                        amount -= count;
                        if (amount <= 0) {
                            break;
                        }
                    }
                }

                this.insertInputsStack(inventory, inputInv, kegBlockEntity, recipeIngredient);
                kegBlockEntity.setChanged();

                pickupAction(maid);
            }
        }
    }

    private boolean hasEnoughIngredient(List<Integer> amounts, List<List<ItemStack>> ingredients) {
        boolean canInsert = true;

        int i = 0;
        for (List<ItemStack> ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            int actualCount = amounts.get(i++);
            for (ItemStack itemStack : ingredient) {
                if (itemStack.isEmpty()) continue;

                actualCount -= itemStack.getCount();
                if (actualCount <= 0) {
                    break;
                }
            }

            if (actualCount > 0) {
                canInsert = false;
                break;
            }
        }

        return canInsert;
    }

    private void insertInputsStack(AbstractedItemHandler beInv, IItemHandlerModifiable ingreInputsInv, KegBlockEntity be, Pair<List<Integer>, List<List<ItemStack>>> ingredientPair) {
        List<Integer> amounts = ingredientPair.getFirst();
        List<List<ItemStack>> ingredients = ingredientPair.getSecond();

        for (int i = 0, j = 0; i < ingredients.size() - 1; i++, j++) {
            insertAndShrink(beInv, amounts.get(j + 1), ingredients, j + 1, i);
        }
        be.setChanged();
    }

    private void insertAndShrink(AbstractedItemHandler beInv, Integer amount, List<List<ItemStack>> ingredient, int ingredientIndex, int slotIndex) {
        for (ItemStack itemStack : ingredient.get(ingredientIndex)) {
            if (itemStack.isEmpty()) continue;
            int count = itemStack.getCount();

            if (count >= amount) {
                ItemStack leftInsertedStack = beInv.insertItem(slotIndex, itemStack.copyWithCount(amount), false);
                itemStack.shrink(amount - leftInsertedStack.getCount());
                break;
            } else {
                ItemStack leftInsertedStack = beInv.insertItem(slotIndex, itemStack.copyWithCount(count), false);
                itemStack.shrink(count - leftInsertedStack.getCount());
                amount -= count;
                if (amount <= 0) {
                    break;
                }
            }
        }
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        if (maid.level.isClientSide) {
            return Collections.emptyList();
        }

        MaidRecipesManager<KegFermentingRecipe> cookingPotRecipeMaidRecipesManager = getRecipesManager(maid);
        MaidFermentingMoveTask maidCookMoveTask = new MaidFermentingMoveTask(this, (KegFermentingRecipesManager) cookingPotRecipeMaidRecipesManager);
        MaidCookMakeTask<KegBlockEntity, KegFermentingRecipe> maidCookMakeTask = new MaidCookMakeTask<>(this, cookingPotRecipeMaidRecipesManager);
        return Lists.newArrayList(Pair.of(5, maidCookMoveTask), Pair.of(6, maidCookMakeTask));
    }

    @Override
    public List<KegFermentingRecipe> getRecipes(Level level) {
        if (KEG_RECIPE_INGREDIENTS.isEmpty()) {
            FLUID_CONTAINERS.clear();

            Map<Fluid, List<Pair<ItemStack, Integer>>> fluidItems1 = new HashMap<>();
            Map<Fluid, List<ItemStack>> fluidContainers1 = new HashMap<>();
            for (Fluid fluid : BuiltInRegistries.FLUID) {
                if (fluid instanceof EmptyFluid) continue;

                ItemStack container = fluid.getBucket().getDefaultInstance().getCraftingRemainingItem();
                if (!container.isEmpty()) {
                    if (fluidContainers1.containsKey(fluid)) {
                        List<ItemStack> itemStacks = fluidContainers1.getOrDefault(fluid, Collections.emptyList());
                        if (itemStacks.stream().noneMatch(itemStack1 -> itemStack1.is(container.getItem()))) {
                            itemStacks.add(container);
                        }
                    } else {
                        fluidContainers1.put(fluid, Lists.newArrayList(container));
                    }
                }
            }
            for (Item item : BuiltInRegistries.ITEM) {
                ItemStack defaultInstance = item.getDefaultInstance().copy();
                IFluidHandlerItem iFluidHandlerItem = defaultInstance.getCapability(Capabilities.FluidHandler.ITEM);
                if (iFluidHandlerItem instanceof FluidBucketWrapper fluidBucketWrapper) {
                    FluidStack fluidStack = fluidBucketWrapper.getFluid();
                    Fluid rawFluid = fluidStack.getFluid();

                    if (!fluidStack.isEmpty() && !(rawFluid instanceof EmptyFluid)) {

                        if (fluidItems1.containsKey(rawFluid)) {
                            List<Pair<ItemStack, Integer>> fluidItems2 = fluidItems1.getOrDefault(rawFluid, Collections.emptyList());
                            if (fluidItems2.stream().noneMatch(pair1 -> pair1.getFirst().is(defaultInstance.getItem()))) {
                                fluidItems1.get(rawFluid).add(Pair.of(defaultInstance, fluidStack.getAmount()));
                            }
                        } else {
                            fluidItems1.put(rawFluid, Lists.newArrayList(Pair.of(defaultInstance, fluidStack.getAmount())));
                        }

                        ItemStack container = fluidBucketWrapper.getContainer().getCraftingRemainingItem();
                        if (!container.isEmpty()) {
                            if (fluidContainers1.containsKey(rawFluid)) {
                                List<ItemStack> itemStacks = fluidContainers1.getOrDefault(rawFluid, Collections.emptyList());
                                if (itemStacks.stream().noneMatch(itemStack1 -> itemStack1.is(container.getItem()))) {
                                    itemStacks.add(container);
                                }
                            } else {
                                fluidContainers1.put(rawFluid, Lists.newArrayList(container));
                            }
                        }
                    }
                }
            }

            List<KegPouringRecipe> kegPouringRecipes = level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value).toList();
            List<KegFermentingRecipe> KegFermentingRecipes = level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.FERMENTING).stream().map(RecipeHolder::value).toList();
            for (KegPouringRecipe kegPouringRecipe : kegPouringRecipes) {
                Fluid rawFluid = kegPouringRecipe.getRawFluid().fluid();
                if (rawFluid instanceof EmptyFluid) {
                    continue;
                }

                ItemStack itemStack = kegPouringRecipe.getOutput().copy();
                if (!itemStack.isEmpty()) {
                    if (fluidItems1.containsKey(rawFluid)) {
                        List<Pair<ItemStack, Integer>> itemStacks = fluidItems1.getOrDefault(rawFluid, Collections.emptyList());
                        if (itemStacks.stream().noneMatch(itemStack1 -> itemStack1.getFirst().is(itemStack.getItem()))) {
                            itemStacks.add(Pair.of(itemStack, Math.toIntExact(kegPouringRecipe.getLoaderAmount())));
                        }
                    } else {
                        fluidItems1.put(rawFluid, Lists.newArrayList(Pair.of(itemStack, Math.toIntExact(kegPouringRecipe.getLoaderAmount()))));
                    }
                }

                ItemStack container = kegPouringRecipe.getContainer().copyWithCount(1);
                if (!container.isEmpty()) {
                    if (fluidContainers1.containsKey(rawFluid)) {
                        List<ItemStack> itemStacks = fluidContainers1.getOrDefault(rawFluid, Collections.emptyList());
                        if (itemStacks.stream().noneMatch(itemStack1 -> itemStack1.is(container.getItem()))) {
                            itemStacks.add(container);
                        }

                    } else {
                        fluidContainers1.put(rawFluid, Lists.newArrayList(container));
                    }
                }
            }
            FLUID_CONTAINERS.putAll(fluidContainers1);

            for (KegFermentingRecipe kegFermentingRecipe : KegFermentingRecipes) {
                // 输入的流体
                kegFermentingRecipe.getFluidIngredient().ifPresentOrElse(
                        fluidIngredientWithAmount -> {
                            List<AbstractedFluidStack> fluidIn = fluidIngredientWithAmount.ingredient().displayStacks();
                            if (!fluidIn.isEmpty()) {
                                List<ItemStack> fluidItems = new ArrayList<>();
                                if (fluidItems1.keySet().stream().anyMatch(fluid -> fluidIn.stream().anyMatch(abstractedFluidStack -> fluid.isSame(abstractedFluidStack.fluid())))) {
                                    fluidItems1.forEach((fluid, itemStacks) -> {
                                        if (fluidIn.stream().anyMatch(abstractedFluidStack -> fluid.isSame(abstractedFluidStack.fluid()))) {
                                            for (Pair<ItemStack, Integer> fluidStackPair : itemStacks) {
                                                ItemStack outputFluidItem = fluidStackPair.getFirst().copy();
                                                int amount = fluidStackPair.getSecond();
                                                int amountTotal = fluidIn.stream().mapToInt(abstractedFluidStack -> Math.toIntExact(abstractedFluidStack.amount())).sum();
                                                outputFluidItem.setCount(Math.max(1, amountTotal / amount));

                                                if (fluidItems.stream().noneMatch(itemStack -> itemStack.is(outputFluidItem.getItem()) && itemStack.getCount() == outputFluidItem.getCount())) {
                                                    fluidItems.add(outputFluidItem);
                                                }

                                            }
                                        }
                                    });
                                } else {
                                    List<ItemStack> otherFluidItem = fluidIn.stream().map(fluid -> BnCFluidItemDisplays.getFluidItemDisplay(level.registryAccess(), fluid).copy()).toList();
                                    fluidItems.addAll(otherFluidItem);
                                }

                                MaidKegRecipe maidKegFermentingRecipe = new MaidKegRecipe(fluidItems, kegFermentingRecipe.getIngredients());
                                KEG_RECIPE_INGREDIENTS.put(kegFermentingRecipe, maidKegFermentingRecipe);
                            }
                        }, () -> {
                            MaidKegRecipe maidKegFermentingRecipe = new MaidKegRecipe(Collections.emptyList(), kegFermentingRecipe.getIngredients());
                            KEG_RECIPE_INGREDIENTS.put(kegFermentingRecipe, maidKegFermentingRecipe);
                        }
                );
            }

        }
        return KEG_RECIPE_INGREDIENTS.keySet().stream().toList();
    }

    @Override
    public MaidRecipesManager<KegFermentingRecipe> getRecipesManager(EntityMaid maid) {
        return new KegFermentingRecipesManager(maid, this) {
            @Override
            protected List<KegFermentingRecipe> getFilterRecipes(List<KegFermentingRecipe> rec) {
                Set<Integer> temperates = searchAndCreateTemperate((ServerLevel) maid.level, maid);
                return rec.stream()
                        .filter(kegFermentingRecipe -> {
                            for (Integer temperate : temperates) {
                                if (isValidTemp(temperate, kegFermentingRecipe.getTemperature())) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            }
        };
    }

    @Override
    public ResourceLocation getUid() {
        return TaskInfo.BNC_KEY.uid;
    }

    @Override
    public ItemStack getIcon() {
        return BnCBlocks.KEG.asItem().getDefaultInstance();
    }

    @Override
    public TaskDataKey<CookData> getCookDataKey() {
        return RegisterData.BNC_KEY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients(Recipe<?> recipe) {
        NonNullList<Ingredient> ingredinetNonNullList = NonNullList.create();

        MaidKegRecipe maidKegRecipe = KEG_RECIPE_INGREDIENTS.get((KegFermentingRecipe) recipe);
        if (!maidKegRecipe.inFluids.isEmpty()) {
            ingredinetNonNullList.add(Ingredient.of(maidKegRecipe.inFluids.stream()));
        }
        ingredinetNonNullList.addAll(maidKegRecipe.inItems());

        return ingredinetNonNullList;
    }

    protected Set<Integer> searchAndCreateTemperate(ServerLevel worldIn, EntityMaid maid) {
        Set<Integer> worldBlockEntityTemperates = new HashSet<>();

        BlockPos centrePos = getSearchPos(maid);
        int searchRange = (int) maid.getRestrictRadius();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int y = 0; y <= 2; y = y > 0 ? -y : 1 - y) {
            for (int i = 0; i < searchRange; ++i) {
                for (int x = 0; x <= i; x = x > 0 ? -x : 1 - x) {
                    for (int z = x < i && x > -i ? i : 0; z <= i; z = z > 0 ? -z : 1 - z) {
                        mutableBlockPos.setWithOffset(centrePos, x, y + 1, z);
                        if (maid.isWithinRestriction(mutableBlockPos)) {
                            BlockEntity blockEntity = worldIn.getBlockEntity(mutableBlockPos);
                            if (blockEntity instanceof KegBlockEntity kegBlockEntity && !((ICbeAccessor) kegBlockEntity).tlmk$innerCanCook()) {
                                worldBlockEntityTemperates.add(kegBlockEntity.getTemperature());
                            }
                        }
                    }
                }
            }
        }
        return worldBlockEntityTemperates;
    }

    protected void pickupAction(EntityMaid maid) {
        maid.swing(InteractionHand.MAIN_HAND);
        maid.playSound(SoundEvents.ITEM_PICKUP, 1.0F, maid.getRandom().nextFloat() * 0.1F + 1.0F);
    }

    public record MaidKegRecipe(List<ItemStack> inFluids, List<Ingredient> inItems) {
    }
}
