package com.github.wallev.maidsoulkitchen.task.cook.common.inventory;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.api.task.v1.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import com.github.wallev.maidsoulkitchen.inventory.container.item.BagType;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class MaidRecipesManager<R extends Recipe<? extends RecipeInput>> {
    protected final List<R> rec = new ArrayList<>();
    protected final List<R> currentRecs = new ArrayList<>();
    protected final EntityMaid maid;
    protected final Level level;
    protected final ICookTask<?, R> task;
    protected final boolean single;
    protected ICookInventory cookInv;
    protected boolean hasCulinaryHub;
    protected Map<BagType, List<BlockPos>> bindingPoses;
    protected String lastTaskRule;
    protected List<String> recipeIds;
    protected int repeatTimes = 0;
    protected List<MaidRecipe<R>> recipesIngredients = new ArrayList<>();
    protected int tryTime = 0;

    public MaidRecipesManager(EntityMaid maid, ICookTask<?, R> task, boolean single) {
        this(maid, task, single, true);
    }

    public MaidRecipesManager(EntityMaid maid, ICookTask<?, R> task, boolean single, boolean createRecIng) {
        this.maid = maid;
        this.level = maid.level;
        this.single = single;
        this.task = task;

//        if (createRecIng) {
//            this.createRecipesIngredients();
//        }
    }

    public static void makeChanged(BlockEntity tile) {
        tile.setChanged();
        Level world = tile.getLevel();
        if (world != null) {
            world.sendBlockUpdated(tile.getBlockPos(), tile.getBlockState(), tile.getBlockState(), 3);
        }
    }

    private boolean initInvData() {
        if (this.cookInv == null || this.bindingPoses == null || (!this.hasCulinaryHub && !this.findCulinaryHub().isEmpty())) {
            this.hasCulinaryHub = !this.findCulinaryHub().isEmpty();
            this.bindingPoses = ItemCulinaryHub.getBindPoses(this.findCulinaryHub());
            //@todo
            this.cookInv = this.enableHub() ? this.initCookInv() : new MaidInventory(maid);

            return true;
        }
        return false;
    }

    private ICookInventory initCookInv() {
        ItemStack culinaryHub = this.findCulinaryHub();
        return culinaryHub.isEmpty() ? new MaidInventory(maid) : new CookBagInventory(maid.registryAccess(), culinaryHub);
    }

    public ItemStack findCulinaryHub() {
        ItemStack culinaryHubItem = this.maid.getMaidInv().getStackInSlot(4);
        if (culinaryHubItem.is(MkItems.CULINARY_HUB.get())) return culinaryHubItem;
        return ItemStack.EMPTY;
    }

    private void tranCookBag2Chest(BagType bagType, boolean requireHasItem) {
        if (!this.canHub()) return;

        List<BlockPos> ingredientPos = getBindingTypePoses(bagType);
        if (ingredientPos.isEmpty()) return;

        IItemHandlerModifiable itemStackHandler = this.getBagContainerInv(bagType);

        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (BlockPos ingredientPo : ingredientPos) {
                if (isPosZone(ingredientPo)) continue;

                BlockEntity blockEntity = maid.level.getBlockEntity(ingredientPo);
                if (blockEntity == null) continue;
                if (stack.isEmpty()) break;

                // 原版
                for (IChestType type : ChestManager.getAllChestTypes()) {
                    if (!type.isChest(blockEntity)) continue;
                    if (type.getOpenCount(maid.level, ingredientPo, blockEntity) > 0) continue;
                    IItemHandler iItemHandler = maid.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
                    Optional.ofNullable(iItemHandler).ifPresent(beInv -> {
                        ItemStack leftStack = ItemHandlerHelper.insertItemStacked(beInv, stack.copy(), false);
                        stack.shrink(stack.getCount() - leftStack.getCount());
                    });
                    makeChanged(blockEntity);
                    break;
                }
            }
        }
        this.syncInv();
    }

    private List<BlockPos> getBindingTypePoses(BagType bagType) {
        return this.bindingPoses.getOrDefault(bagType, Collections.emptyList());
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public boolean isSingle() {
        return single;
    }

    protected List<R> getRecs() {
        List<R> list = this.getFilterRecipes(this.rec);
        shuffle(list);
        return list;
    }

    protected List<R> getFilterRecipes(List<R> rec) {
        return new ArrayList<>(rec);
    }

    public List<Pair<List<Integer>, List<List<ItemStack>>>> getRecipesIngredients() {
        // 保持向后兼容性 - 转换 MaidRecipe 为旧格式
        return recipesIngredients.stream()
                .map(maidRecipe -> {
                    Pair<List<Integer>, List<Item>> legacy = maidRecipe.toLegacyFormat();
                    List<List<ItemStack>> itemStacks = legacy.getSecond().stream()
                            .map(item -> getCookInv().getInventoryStack().getOrDefault(item, List.of()))
                            .toList();
                    return Pair.of(legacy.getFirst(), itemStacks);
                })
                .toList();
    }

    public Pair<List<Integer>, List<List<ItemStack>>> getRecipeIngredient() {
        // 保持向后兼容性
        if (recipesIngredients.isEmpty()) return Pair.of(Collections.emptyList(), Collections.emptyList());

        MaidRecipe<R> maidRecipe = recipesIngredients.removeFirst();
        Pair<List<Integer>, List<Item>> legacy = maidRecipe.toLegacyFormat();
        List<List<ItemStack>> itemStacks = legacy.getSecond().stream()
                .map(item -> getCookInv().getInventoryStack().getOrDefault(item, List.of()))
                .toList();

        return Pair.of(legacy.getFirst(), itemStacks);
    }

    public RecipeHolder<R> getNextRecipe() {
        return this.recipesIngredients.getFirst().recipe();
    }

    public boolean checkAndCreateRecipesIngredients() {
        //预防隙间转移走烹饪中枢
        if (this.hasCulinaryHub && this.findCulinaryHub().isEmpty() && this.level instanceof ServerLevel serverLevel) {
            this.recipesIngredients = Collections.emptyList();
            this.maid.refreshBrain(serverLevel);
            return false;
        }
        boolean inited = this.init();
        // 缓存的配方原料没了
        if (!recipesIngredients.isEmpty()) return true;
        // 是否为上一次的背包以及手上的物品
        boolean lastInv = this.isLastCookInv();
        if (!inited && lastInv && tryTime++ < 10) return true;
        tryTime = 0;
        this.createRecipesIngredients();
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean initTaskData() {
        if (lastTaskRule == null || recipeIds == null) {
            ICookTask<?, R> cookTask = (ICookTask<?, R>) maid.getTask();
            CookData cookData = cookTask.getTaskData(maid);
            this.lastTaskRule = cookData.mode();
            this.recipeIds = cookData.getRecs();
            this.rec.clear();

            List<R> allRecipesFor = this.getValidRecipesFor();
            this.rec.addAll(allRecipesFor);

            return true;
        }

        return false;
    }

    private List<R> getValidRecipesFor() {
        List<R> allRecipesFor;
        if (this.lastTaskRule.equals(CookData.Mode.WHITELIST.name)) {
            allRecipesFor = task.getRecipeHolders(level).stream().filter(r -> recipeIds.contains(r.id().toString())).map(RecipeHolder::value).toList();
        } else {
            allRecipesFor = task.getRecipeHolders(level).stream().filter(r -> !recipeIds.contains(r.id().toString())).map(RecipeHolder::value).toList();
        }
        return allRecipesFor;
    }

    private boolean isLastCookInv() {
        CombinedInvWrapper availableInv = this.maid.getAvailableInv(true);

        List<ItemStack> lastInvStack = this.cookInv.getLastInvStack();

        ItemStack culinaryHub = this.findCulinaryHub();
        if (!culinaryHub.isEmpty()) {
            IItemHandlerModifiable availableInv1 = this.getCookInv().getAvailableInv(maid, BagType.INGREDIENT);
            if (availableInv1.getSlots() != lastInvStack.size()) return false;

            for (int i = 0; i < availableInv1.getSlots(); i++) {
                ItemStack stackInSlot = availableInv1.getStackInSlot(i);
                ItemStack cacheStack = lastInvStack.get(i);
                if (!(stackInSlot.is(cacheStack.getItem()) && stackInSlot.getCount() == cacheStack.getCount())) {
                    return false;
                }
            }
        } else {
            if (availableInv.getSlots() != lastInvStack.size()) return false;

            for (int i = 0; i < availableInv.getSlots(); i++) {
                ItemStack stackInSlot = availableInv.getStackInSlot(i);
                ItemStack cacheStack = lastInvStack.get(i);
                if (!(stackInSlot.is(cacheStack.getItem()) && stackInSlot.getCount() == cacheStack.getCount())) {
                    return false;
                }
            }
        }

        return true;
    }

    public void mapChestIngredient() {
        if (!this.canHub()) return;

        List<BlockPos> ingredientPos = getBindingTypePoses(BagType.INGREDIENT);
        if (ingredientPos.isEmpty()) return;

        IItemHandlerModifiable inventory = this.getCookInv().getAvailableInv(maid, BagType.INGREDIENT);

        Map<Item, Integer> available = new HashMap<>();
        Map<Item, List<ItemStack>> ingredientAmount = new HashMap<>();
        Map<ItemStack, Pair<IItemHandler, Integer>> stackContentHandler = new HashMap<>();

        // 汇集所有箱子的原料
        for (BlockPos ingredientPo : ingredientPos) {
            if (isPosZone(ingredientPo)) continue;

            BlockEntity blockEntity = level.getBlockEntity(ingredientPo);
            if (blockEntity == null) continue;

            // 原版
            for (IChestType type : ChestManager.getAllChestTypes()) {
                if (!type.isChest(blockEntity) || type.getOpenCount(maid.level, ingredientPo, blockEntity) > 0)
                    continue;
                IItemHandler iItemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
                Optional.ofNullable(iItemHandler).ifPresent(beInv -> {
                    for (int i = 0; i < beInv.getSlots(); i++) {
                        ItemStack stackInSlot = beInv.getStackInSlot(i);
                        Item item = stackInSlot.getItem();

                        if (stackInSlot.isEmpty()) continue;

                        stackContentHandler.put(stackInSlot, Pair.of(beInv, i));

                        available.merge(item, stackInSlot.getCount(), Integer::sum);

                        List<ItemStack> itemStacks = ingredientAmount.get(item);
                        if (itemStacks == null) {
                            ingredientAmount.put(item, Lists.newArrayList(stackInSlot));
                        } else {
                            itemStacks.add(stackInSlot);
                        }
                    }
                });
                break;
            }
        }

        List<MaidRecipe<R>> _make = this.createIngres(available, false);
        if (_make.isEmpty()) return;

        // 转移箱子原料至CookBag
        for (MaidRecipe<R> maidRecipe : _make) {
            Pair<List<Integer>, List<Item>> legacy = maidRecipe.toLegacyFormat();
            List<Integer> counts = legacy.getFirst();
            List<Item> items = legacy.getSecond();

            for (int i = 0; i < counts.size(); i++) {
                Integer neededCount = counts.get(i);
                Item item = items.get(i);
                if (neededCount <= 0 || item == null) continue;

                List<ItemStack> itemStacks = ingredientAmount.get(item);
                if (itemStacks == null) continue;
                
                for (ItemStack itemStack : itemStacks) {
                    if (itemStack.isEmpty()) continue;
                    int count = itemStack.getCount();
                    // 减去当前物品的数量还是大于0，证明还没满足，就整体移动

                    Pair<IItemHandler, Integer> handlerInfo = stackContentHandler.get(itemStack);
                    if (handlerInfo == null) continue;

                    IItemHandler handler = handlerInfo.getFirst();
                    Integer slot = handlerInfo.getSecond();

                    if (neededCount >= count) {
                        // 需要的数量大于等于当前物品数量，全部移动
                        ItemStack copy = itemStack.copy();
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, copy, false);
                        int transferred = copy.getCount() - remainder.getCount();
                        handler.extractItem(slot, transferred, false);
                        neededCount -= transferred;
                    } else {
                        // 只需要部分数量
                        ItemStack copy = itemStack.copyWithCount(neededCount);
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, copy, false);
                        int transferred = copy.getCount() - remainder.getCount();
                        handler.extractItem(slot, transferred, false);
                        neededCount = 0;
                    }

                    if (neededCount <= 0) break;
                }
            }
        }
        
        // 更新所有箱子的状态
        for (BlockPos ingredientPo : ingredientPos) {
            if (isPosZone(ingredientPo)) continue;

            BlockEntity blockEntity = level.getBlockEntity(ingredientPo);
            if (blockEntity != null) {
                makeChanged(blockEntity);
            }
        }
        // 更新CookBag的inventory
        this.syncInv();
    }

    private boolean isPosZone(BlockPos ingredientPo) {
        float maxDistance = maid.getRestrictRadius();
        if (maid.distanceToSqr(ingredientPo.getX(), ingredientPo.getY(), ingredientPo.getZ()) > (maxDistance * maxDistance)) {
            return true;
        }
        return false;
    }

    private boolean init() {
        boolean initTaskData = this.initTaskData();
        boolean initInvData = this.initInvData();
        if (initTaskData || initInvData) {
            this.recipesIngredients = Collections.emptyList();
            return true;
        }

        return false;
    }

    private void createRecipesIngredients() {
        this.init();

        this.currentRecs.clear();
        this.currentRecs.addAll(this.getRecs());
        // 将CookBag里无用的配方原料放回原料箱子
        this.tranUnIngre2Chest();
        // 获取原料箱子配方原料并置入CookBag
        this.mapChestIngredient();
        this.cookInv.refreshInv(maid.registryAccess());
        this.createIngres(true);
        this.currentRecs.clear();

    }

    public void tranOutput2Chest() {
        this.tranCookBag2Chest(BagType.OUTPUT, false);
    }

    public void tranUnIngre2Chest() {
        this.tranCookBag2Chest(BagType.INGREDIENT, true);
    }

    private void createIngres(boolean setRecipeIngres) {
        Map<Item, Integer> maidAvailableInv = this.getMaidAvailableInv();
        this.createIngres(maidAvailableInv, setRecipeIngres);
    }

    protected List<MaidRecipe<R>> createIngres(Map<Item, Integer> available, boolean setRecipeIngres) {
        List<MaidRecipe<R>> _make = getRecIngreMake(available);

        if (setRecipeIngres) {
            setRecIngres(_make, available);
        }

        return _make;
    }

    @NotNull
    protected List<MaidRecipe<R>> getRecIngreMake(Map<Item, Integer> available) {
        List<MaidRecipe<R>> _make = new ArrayList<>();
        for (R r : this.currentRecs) {
            // 创建 RecipeHolder
            RecipeHolder<R> recipeHolder = task.getRecipeHolders(level).stream()
                    .filter(holder -> holder.value().equals(r))
                    .findFirst()
                    .orElse(null);
            if (recipeHolder == null) {
                MaidsoulKitchen.LOGGER.warn("Could not find maid recipe for {}", r);
                continue;
            }
            MaidRecipe<R> maidRecipe = this.getAmountIngredient(recipeHolder, available);
            if (!maidRecipe.isEmpty()) {
                _make.add(maidRecipe);
            }
        }
        repeat(_make, available, this.repeatTimes);
        return _make;
    }

    protected void setRecIngres(List<MaidRecipe<R>> _make, Map<Item, Integer> available) {
        if (_make.isEmpty()) return;
        this.recipesIngredients = new ArrayList<>(_make);
    }

    @NotNull
    protected Map<Item, Integer> getMaidAvailableInv() {
        return new HashMap<>(getCookInv().getInventoryItem());
    }

    protected ICookInventory getCookInv() {
        return this.cookInv;
    }

    protected void repeat(List<MaidRecipe<R>> oriList, Map<Item, Integer> available, int times) {
        ArrayList<MaidRecipe<R>> oriRecipes = new ArrayList<>(oriList);
        for (int l = 0; l < times; l++) {
            for (MaidRecipe<R> maidRecipe : oriRecipes) {
                Pair<List<Integer>, List<Item>> legacy = maidRecipe.toLegacyFormat();
                List<Integer> counts = legacy.getFirst();
                List<Item> items = legacy.getSecond();

                boolean canRepeat = true;
                for (int i = 0; i < items.size(); i++) {
                    Integer availableCount = available.get(items.get(i));
                    if (availableCount < counts.get(i)) {
                        canRepeat = false;
                        break;
                    }
                }

                if (canRepeat) {
                    for (int i = 0; i < items.size(); i++) {
                        Item item = items.get(i);
                        available.put(item, available.get(item) - counts.get(i));
                    }
                    oriList.add(maidRecipe);
                }
            }
        }
    }


    protected MaidRecipe<R> getAmountIngredient(RecipeHolder<R> recipe, Map<Item, Integer> available) {
        List<Ingredient> ingredients = task.getIngredients(recipe.value());
        List<Item> invIngredient = new ArrayList<>();
        Map<Item, Integer> itemTimes = new HashMap<>();
        boolean[] canMake = {true};
        boolean[] single = {false};

        extraStartRecipe(recipe.value(), available, canMake, single, itemTimes, invIngredient);

        for (Ingredient ingredient : ingredients) {
            // 不知道有什么负面影响，但是不跳过的话会导致kk空气压缩机无法正常识别材料
            if (ingredient.isEmpty()) {
                continue;
            }
            boolean hasIngredient = false;
            for (Item item : available.keySet()) {
                ItemStack stack = item.getDefaultInstance();
                if (ingredient.test(stack)) {
                    invIngredient.add(item);
                    hasIngredient = true;

                    if (stack.getMaxStackSize() == 1) {
                        single[0] = true;
                        itemTimes.put(item, 1);
                    } else {
                        itemTimes.merge(item, 1, Integer::sum);
                    }

                    break;
                }
            }

            if (!hasIngredient) {
                canMake[0] = false;
                itemTimes.clear();
                invIngredient.clear();
                break;
            }
        }

        extraEndRecipe(recipe.value(), available, canMake, single, itemTimes, invIngredient);

        if (!canMake[0] || itemTimes.entrySet().stream().anyMatch(entry -> available.get(entry.getKey()) < entry.getValue())) {
            return MaidRecipe.empty();
        }

        int maxCount = 64;
        if (single[0] || this.single) {
            maxCount = 1;
        } else {
            for (Item item : itemTimes.keySet()) {
                maxCount = Math.min(maxCount, item.getDefaultInstance().getMaxStackSize());
                maxCount = Math.min(maxCount, available.get(item) / itemTimes.get(item));
            }
        }

        List<Pair<Item, Integer>> ingredientMap = new ArrayList<>();
        for (Item item : invIngredient) {
            ingredientMap.add(Pair.of(item, maxCount));
            available.put(item, available.get(item) - maxCount);
        }

        return new MaidRecipe<>(recipe, ingredientMap);
    }

    protected boolean extraStartRecipe(R recipe, Map<Item, Integer> available, boolean[] canMake, boolean[] single, Map<Item, Integer> itemTimes, List<Item> invIngredient) {
        return true;
    }

    protected boolean extraEndRecipe(R recipe, Map<Item, Integer> available, boolean[] single, boolean[] canMake, Map<Item, Integer> itemTimes, List<Item> invIngredient) {
        return true;
    }

    protected void shuffle(List<R> recipes) {
        Collections.shuffle(recipes);
    }

    public void shrinkOutputAdditionItem(ItemStack findItem, int count) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                availableInv.extractItem(additionSlot, count, false);
                this.cookInv.syncInv();
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                this.shrinkAdditionStackFromHub(findItem, bindModePoses, this.level, count);
            }

        } else {
            CombinedInvWrapper maidInv = this.maid.getAvailableInv(true);
            int additionSlot = ItemsUtil.findStackSlot(maidInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                maidInv.extractItem(additionSlot, count, false);
            }

        }
    }

    public int getOutputAdditionItemCount(ItemStack findItem) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                return availableInv.getStackInSlot(additionSlot).getCount();
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                return getAdditionStackFromHubCount(findItem, bindModePoses, this.level);
            }

        } else {
            CombinedInvWrapper maidInv = this.maid.getAvailableInv(true);
            int additionSlot = ItemsUtil.findStackSlot(maidInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                return maidInv.getStackInSlot(additionSlot).getCount();
            }

        }

        return 0;
    }

    public boolean hasOutputAdditionItem(Predicate<ItemStack> findItem) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> findItem.test(stack));

            if (additionSlot > -1) {
                return true;
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                return hasAdditionStackFromHub(findItem, bindModePoses, level);
            }
        } else {
            return ItemsUtil.findStackSlot(maid.getAvailableInv(true), stack -> findItem.test(stack)) > -1;
        }
    }

    private boolean hasAdditionStackFromHub(Predicate<ItemStack> findItem, List<BlockPos> bindModePoses, Level level) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();
                    return ItemsUtil.findStackSlot(beInv, stack -> findItem.test(stack)) > -1;
                }

            }

        }
        return false;
    }

    public ItemStack findOutputAdditionItem(Predicate<ItemStack> findItem) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> findItem.test(stack));

            if (additionSlot > -1) {
                ItemStack itemStack = availableInv.extractItem(additionSlot, 64, false);
                this.cookInv.syncInv();
                return itemStack.copy();
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                return getAdditionStackFromHub(findItem, bindModePoses, this.level);
            }

        } else {
            CombinedInvWrapper maidInv = this.maid.getAvailableInv(true);
            int additionSlot = ItemsUtil.findStackSlot(maidInv, stack -> findItem.test(stack));

            if (additionSlot > -1) {
                ItemStack stackInSlot = maidInv.getStackInSlot(additionSlot);
                ItemStack copy = stackInSlot.copy();
                stackInSlot.setCount(0);
                return copy;

            }

        }

        return ItemStack.EMPTY;
    }

    private ItemStack getAdditionStackFromHub(Predicate<ItemStack> findItem, List<BlockPos> bindModePoses, Level level) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();

                    int stackSlot = ItemsUtil.findStackSlot(beInv, stack -> findItem.test(stack));

                    if (stackSlot > -1) {
                        ItemStack copy = beInv.extractItem(stackSlot, 64, false).copy();
                        blockEntity.setChanged();
                        return copy;
                    }

                }

            }

        }
        return ItemStack.EMPTY;
    }

    public boolean hasOutputAdditionItem(ItemStack findItem) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                return true;
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                return hasAdditionStackFromHub(findItem, bindModePoses, level);
            }
        } else {
            return ItemsUtil.findStackSlot(maid.getAvailableInv(true), stack -> stack.is(findItem.getItem())) > -1;
        }
    }

    public ItemStack findOutputAdditionItem(ItemStack findItem) {
        if (this.canHub()) {
            IItemHandlerModifiable availableInv = this.getOutputAdditionInv();
            int additionSlot = ItemsUtil.findStackSlot(availableInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                ItemStack itemStack = availableInv.extractItem(additionSlot, 64, false);
                this.cookInv.syncInv();
                return itemStack.copy();
            } else {
                List<BlockPos> bindModePoses = getBindingTypePoses(BagType.OUTPUT_ADDITION);
                return getAdditionStackFromHub(findItem, bindModePoses, this.level);
            }

        } else {
            CombinedInvWrapper maidInv = this.maid.getAvailableInv(true);
            int additionSlot = ItemsUtil.findStackSlot(maidInv, stack -> stack.is(findItem.getItem()));

            if (additionSlot > -1) {
                ItemStack stackInSlot = maidInv.getStackInSlot(additionSlot);
                ItemStack copy = stackInSlot.copy();
                stackInSlot.setCount(0);
                return copy;

            }

        }

        return ItemStack.EMPTY;
    }

    private boolean hasAdditionStackFromHub(ItemStack findItem, List<BlockPos> bindModePoses, Level level) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();
                    return ItemsUtil.findStackSlot(beInv, stack -> stack.is(findItem.getItem())) > -1;
                }

            }

        }
        return false;
    }

    private int getAdditionStackFromHubCount(ItemStack findItem, List<BlockPos> bindModePoses, Level level) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();

                    int stackSlot = ItemsUtil.findStackSlot(beInv, stack -> stack.is(findItem.getItem()));

                    if (stackSlot > -1) {
                        return beInv.getStackInSlot(stackSlot).getCount();
                    }

                }

            }

        }
        return 0;
    }

    private void shrinkAdditionStackFromHub(ItemStack findItem, List<BlockPos> bindModePoses, Level level, int count) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();

                    int stackSlot = ItemsUtil.findStackSlot(beInv, stack -> stack.is(findItem.getItem()));

                    if (stackSlot > -1) {
                        beInv.extractItem(stackSlot, count, false).copy();
                        return;
                    }

                }

            }

        }
    }


    private ItemStack getAdditionStackFromHub(ItemStack findItem, List<BlockPos> bindModePoses, Level level) {
        for (BlockPos bindModePose : bindModePoses) {
            if (isPosZone(bindModePose)) continue;

            BlockEntity blockEntity = level.getBlockEntity(bindModePose);

            if (blockEntity != null) {
                Optional<IItemHandler> capability = Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null));

                if (capability.isPresent()) {
                    IItemHandler beInv = capability.get();

                    int stackSlot = ItemsUtil.findStackSlot(beInv, stack -> stack.is(findItem.getItem()));

                    if (stackSlot > -1) {
                        ItemStack copy = beInv.extractItem(stackSlot, 64, false).copy();
                        blockEntity.setChanged();
                        return copy;
                    }

                }

            }

        }
        return ItemStack.EMPTY;
    }

    public IItemHandlerModifiable getOutputInv() {
        return this.getBagContainerInv(BagType.OUTPUT);
    }

    public IItemHandlerModifiable getOutputAdditionInv() {
        return this.getBagContainerInv(BagType.OUTPUT_ADDITION);
    }

    public IItemHandlerModifiable getInputInv() {
        return this.getBagContainerInv(BagType.INGREDIENT);
    }

    private IItemHandlerModifiable getBagContainerInv(BagType bagType) {
        return this.getCookInv().getAvailableInv(maid, bagType);
    }


    @Nullable
    public IItemHandlerModifiable getIngredientInv() {
        return this.getInputInv();
    }

    // fixme: 不应该这么做，临时解决，等待版本重构
    // bug: 不知为啥，有时并没有初始化，但还是会跳过初始化调用 cookInv.syncInv();
    public void syncInv() {
        ICookInventory cookInv = this.getCookInv();
        if (cookInv != null) {
            cookInv.syncInv();
        }
    }

    // fixme: 不应该这么做，临时解决，等待版本重构
    private boolean canHub() {
        return enableHub() && hasCulinaryHub;
    }

    //不与烹饪中枢交互
    protected boolean enableHub() {
        return true;
    }

}
