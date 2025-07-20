package com.github.wallev.maidsoulkitchen.task.cook.brewinandchewin.keg;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.FluidRecSerializerManager;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.RecSerializerManager;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.mkrec.MKRecipe;
import com.github.wallev.maidsoulkitchen.util.classana.clazz.TaskClassAnalyzer;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;
import umpaz.brewinandchewin.client.utility.BnCFluidItemDisplays;
import umpaz.brewinandchewin.common.crafting.FluidIngredientWithAmount;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.util.*;

@TaskClassAnalyzer(TaskInfo.BNC_KEY)
public class KegRecSerializerManager extends FluidRecSerializerManager<KegFermentingRecipe> {
    private static final KegRecSerializerManager INSTANCE = new KegRecSerializerManager();

    protected KegRecSerializerManager() {
        super(BnCRecipeTypes.FERMENTING);
    }

    private static @NotNull List<ItemStack> getOrCreateFluidItems(Level level, Optional<FluidIngredientWithAmount> fluidInOpt, Map<Fluid, List<Pair<ItemStack, Integer>>> fluidItems1) {
        FluidIngredientWithAmount fluidIn = fluidInOpt.get();
        AbstractedFluidIngredient fluidIngredient = fluidIn.ingredient();
        List<AbstractedFluidStack> fluidStacks = fluidIngredient.displayStacks();

        List<ItemStack> fluidItems = new ArrayList<>();
        for (AbstractedFluidStack stack : fluidStacks) {
            ItemStack otherFluidItem = BnCFluidItemDisplays.getFluidItemDisplay(level.registryAccess(), stack);
            if (!otherFluidItem.isEmpty()) {
                if (fluidItems.stream().noneMatch(itemStack -> itemStack.is(otherFluidItem.getItem()))) {
                    fluidItems.add(otherFluidItem.copy());
                }
            } else {
                fluidItems1.forEach((fluid, itemStacks) -> {
                    if (fluid.isSame(stack.fluid())) {
                        for (Pair<ItemStack, Integer> fluidStackPair : itemStacks) {
                            ItemStack outputFluidItem = fluidStackPair.getFirst().copy();
                            int amount = fluidStackPair.getSecond();
                            int amountTotal = Math.toIntExact(stack.amount());
                            outputFluidItem.setCount(Math.max(1, amountTotal / amount));

                            if (fluidItems.stream().noneMatch(itemStack -> itemStack.is(outputFluidItem.getItem()) && itemStack.getCount() == outputFluidItem.getCount())) {
                                fluidItems.add(outputFluidItem);
                            }
                        }
                    }
                });
            }
        }
        return fluidItems;
    }

    public static RecSerializerManager<KegFermentingRecipe> getInstance() {
        return INSTANCE;
    }

    @Override
    protected void initFluidRecs(Level level) {
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

        List<RecipeHolder<KegPouringRecipe>> kettlePouringRecipes = level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING);
        List<RecipeHolder<KegFermentingRecipe>> kettleRecipes = level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.FERMENTING);
        for (RecipeHolder<KegPouringRecipe> kettlePouringRecipe : kettlePouringRecipes) {
            Fluid rawFluid = kettlePouringRecipe.value().getRawFluid().fluid();
            if (rawFluid instanceof EmptyFluid) {
                continue;
            }

            ItemStack itemStack = kettlePouringRecipe.value().getOutput().copy();
            if (!itemStack.isEmpty()) {
                if (fluidItems1.containsKey(rawFluid)) {
                    List<Pair<ItemStack, Integer>> itemStacks = fluidItems1.getOrDefault(rawFluid, Collections.emptyList());
                    if (itemStacks.stream().noneMatch(itemStack1 -> itemStack1.getFirst().is(itemStack.getItem()))) {
                        itemStacks.add(Pair.of(itemStack, Math.toIntExact(kettlePouringRecipe.value().getLoaderAmount())));
                    }
                } else {
                    fluidItems1.put(rawFluid, Lists.newArrayList(Pair.of(itemStack, Math.toIntExact(kettlePouringRecipe.value().getLoaderAmount()))));
                }
            }

            ItemStack container = kettlePouringRecipe.value().getContainer().copyWithCount(1);
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
        this.fluidContainers = fluidContainers1;

        List<MKRecipe<KegFermentingRecipe>> mkRecipes = new ArrayList<>();
        for (RecipeHolder<KegFermentingRecipe> recipe : kettleRecipes) {
            List<ItemStack> inFluids = Collections.emptyList();

            // 输入的流体
            Optional<FluidIngredientWithAmount> fluidIn = recipe.value().getFluidIngredient();
            inFluids = getOrCreateFluidItems(level, fluidIn, fluidItems1);

            mkRecipes.add(this.createMKRecipe(recipe, inFluids));
        }
        this.recipes = mkRecipes;
    }

    @Override
    protected FluidRecipeInfoProvider<KegFermentingRecipe> createRecipeInfoProvider() {
        return new KegFermentationRecipeInfoProvider();
    }

    public static class KegFermentationRecipeInfoProvider extends FluidRecipeInfoProvider<KegFermentingRecipe> {

        @Override
        public Fluid getOutputFluid(RecSerializerManager<KegFermentingRecipe> rsm, KegFermentingRecipe rec) {
            return rec.getResult().left().map(AbstractedFluidStack::fluid).orElse(null);
        }
    }
}
