package com.github.wallev.maidsoulkitchen.task.cook.common.inventory;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.wallev.maidsoulkitchen.api.task.v1.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import com.github.wallev.maidsoulkitchen.inventory.container.item.BagType;
import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
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

/**
 * 女仆配方管理器 - 负责处理与烹饪任务相关的配方管理和物品转移
 *
 * @param <R> 配方类型
 */
public class MaidRecipesManager<R extends Recipe<? extends RecipeInput>> {
    protected final List<R> rec = new ArrayList<>();              // 所有可用配方列表
    protected final List<R> currentRecs = new ArrayList<>();      // 当前正在处理的配方列表
    protected final EntityMaid maid;                              // 关联的女仆实体
    protected final Level level;                                  // 所在世界
    protected final ICookTask<?, R> task;                         // 烹饪任务接口
    protected final boolean single;                               // 是否单个处理
    protected ICookInventory cookInv;                             // 烹饪物品栏
    protected boolean hasCulinaryHub;                             // 是否拥有烹饪中枢
    protected Map<BagType, List<BlockPos>> bindingPoses;          // 绑定的方块位置（按背包类型分类）
    protected String lastTaskRule;                                // 上一次任务规则
    protected List<String> recipeIds;                             // 配方ID列表
    protected int repeatTimes = 0;                                // 重复次数
    protected List<Pair<List<Integer>, List<List<ItemStack>>>> recipesIngredients = new ArrayList<>(); // 配方材料缓存
    protected int tryTime = 0;                                    // 尝试次数
    protected CookData.RecipeSortMode sortMode = CookData.RecipeSortMode.DEFAULT;   // 配方排序模式

    /**
     * 构造函数
     * @param maid 关联的女仆实体
     * @param task 烹饪任务
     * @param single 是否单个处理
     */
    public MaidRecipesManager(EntityMaid maid, ICookTask<?, R> task, boolean single) {
        this(maid, task, single, true);
    }

    /**
     * 构造函数
     * @param maid 关联的女仆实体
     * @param task 烹饪任务
     * @param single 是否单个处理
     * @param createRecIng 是否创建配方材料
     */
    public MaidRecipesManager(EntityMaid maid, ICookTask<?, R> task, boolean single, boolean createRecIng) {
        this.maid = maid;
        this.level = maid.level;
        this.single = single;
        this.task = task;

//        if (createRecIng) {
//            this.createRecipesIngredients();
//        }
    }

    /**
     * 使方块实体标记为已更改并更新客户端
     * @param tile 方块实体
     */
    public static void makeChanged(BlockEntity tile) {
        tile.setChanged();
        Level world = tile.getLevel();
        if (world != null) {
            world.sendBlockUpdated(tile.getBlockPos(), tile.getBlockState(), tile.getBlockState(), 3);
        }
    }

    /**
     * 初始化物品栏数据
     * @return 是否进行了初始化
     */
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

    /**
     * 初始化烹饪物品栏
     * @return 烹饪物品栏接口
     */
    private ICookInventory initCookInv() {
        ItemStack culinaryHub = this.findCulinaryHub();
        return culinaryHub.isEmpty() ? new MaidInventory(maid) : new CookBagInventory(maid.registryAccess(), culinaryHub);
    }

    /**
     * 查找女仆身上的烹饪中枢物品
     * @return 烹饪中枢物品堆，如果没有则返回空堆
     */
    public ItemStack findCulinaryHub() {
        ItemStack culinaryHubItem = this.maid.getMaidInv().getStackInSlot(4);
        if (culinaryHubItem.is(MkItems.CULINARY_HUB.get())) return culinaryHubItem;
        return ItemStack.EMPTY;
    }

    /**
     * 将烹饪袋中的物品转移到箱子中
     * @param bagType 背包类型
     * @param requireHasItem 是否要求有物品
     */
    private void tranCookBag2Chest(BagType bagType, boolean requireHasItem) {
        if (!this.hasCulinaryHub) return;

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
        this.getCookInv().syncInv();
    }

    /**
     * 获取指定类型背包绑定的方块位置列表
     * @param bagType 背包类型
     * @return 方块位置列表
     */
    private List<BlockPos> getBindingTypePoses(BagType bagType) {
        return this.bindingPoses.getOrDefault(bagType, Collections.emptyList());
    }

    /**
     * 获取关联的女仆实体
     * @return 女仆实体
     */
    public EntityMaid getMaid() {
        return maid;
    }

    /**
     * 是否为单个处理模式
     * @return 是否单个处理
     */
    public boolean isSingle() {
        return single;
    }

    /**
     * 获取配方的输出物品饱食度
     *
     * @param recipe 配方
     * @return 饱食度值，若无法获取则返回0
     */
    protected int getRecipeNutrition(R recipe) {
        ItemStack resultItem = task.getResultItem(recipe, maid.registryAccess());
        FoodProperties foodProperties = resultItem.getFoodProperties(maid);
        return foodProperties != null ? foodProperties.nutrition() : 0;
    }

    /**
     * 获取配方的输出物品饱和度
     *
     * @param recipe 配方
     * @return 饱和度值，若无法获取则返回0
     */
    protected float getRecipeSaturation(R recipe) {
        ItemStack resultItem = task.getResultItem(recipe, maid.registryAccess());
        FoodProperties foodProperties = resultItem.getFoodProperties(maid);
        return foodProperties != null ? foodProperties.saturation() : 0;
    }

    /**
     * 获取配方的材料数量
     *
     * @param recipe 配方
     * @return 材料数量
     */
    protected int getRecipeIngredientsCount(R recipe) {
        List<Ingredient> ingredients = task.getIngredients(recipe);
        return ingredients.size();
    }

    /**
     * 获取可用的配方列表并根据排序模式排序
     * @return 配方列表
     */
    private List<R> getRecs() {
        List<R> list = this.getFilterRecipes(this.rec);

        if (sortMode == CookData.RecipeSortMode.DEFAULT) {
            /**
             * 仅在默认模式下随机打乱配方顺序
             */
            shuffle(list);
        }

        return list;
    }

    /**
     * 过滤配方列表并根据排序模式排序
     * @param rec 原始配方列表
     * @return 排序后的配方列表
     */
    protected List<R> getFilterRecipes(List<R> rec) {
        List<R> filteredRecipes = new ArrayList<>(rec);

        if (sortMode != CookData.RecipeSortMode.DEFAULT) {
            /**
             * 根据排序模式进行排序
             */
            this.sort(filteredRecipes);
        }

        return filteredRecipes;
    }

    /**
     * 对配方列表进行排序
     *
     * @param recipes 配方列表
     */
    private void sort(List<R> recipes) {
        switch (sortMode) {
            case NUTRITION:
                recipes.sort((r1, r2) -> {
                    int nutrition1 = getRecipeNutrition(r1);
                    int nutrition2 = getRecipeNutrition(r2);
                    // 降序排序，饱食度高的优先
                    return Integer.compare(nutrition2, nutrition1);
                });
                break;
            case SATURATION:
                recipes.sort((r1, r2) -> {
                    float saturation1 = getRecipeSaturation(r1);
                    float saturation2 = getRecipeSaturation(r2);
                    // 降序排序，饱和度高的优先
                    return Float.compare(saturation2, saturation1);
                });
                break;
            case INGREDIENTS:
                recipes.sort((r1, r2) -> {
                    int count1 = getRecipeIngredientsCount(r1);
                    int count2 = getRecipeIngredientsCount(r2);
                    // 降序排序，材料数量多的优先
                    return Integer.compare(count2, count1);
                });
                break;
            default:
                // 默认模式不进行排序
                break;
        }
    }

    /**
     * 随机打乱配方列表
     *
     * @param recipes 配方列表
     */
    private void shuffle(List<R> recipes) {
        Collections.shuffle(recipes);
    }

    /**
     * 获取当前配方排序模式
     *
     * @return 排序模式
     */
    public CookData.RecipeSortMode getSortMode() {
        return this.sortMode;
    }

    /**
     * 设置配方排序模式
     *
     * @param mode 排序模式
     */
    public void setSortMode(CookData.RecipeSortMode mode) {
        this.sortMode = mode;
    }

    /**
     * 切换到下一个排序模式
     *
     * @return 切换后的排序模式
     */
    public CookData.RecipeSortMode cycleSortMode() {
        this.sortMode = this.sortMode.next();
        return this.sortMode;
    }

    /**
     * 获取所有配方材料
     * @return 配方材料列表
     */
    public List<Pair<List<Integer>, List<List<ItemStack>>>> getRecipesIngredients() {
        return recipesIngredients;
    }

    /**
     * 获取单个配方材料
     * @return 配方材料
     */
    public Pair<List<Integer>, List<List<ItemStack>>> getRecipeIngredient() {
        if (recipesIngredients.isEmpty()) return Pair.of(Collections.emptyList(), Collections.emptyList());
        int size = recipesIngredients.size();
        Pair<List<Integer>, List<List<ItemStack>>> integerListPair = recipesIngredients.get(0);
        List<Pair<List<Integer>, List<List<ItemStack>>>> pairs = recipesIngredients.subList(1, size);
        recipesIngredients = pairs;
        return integerListPair;
    }

    /**
     * 检查并创建配方材料
     * @return 是否成功创建
     */
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

    /**
     * 初始化任务数据
     * @return 是否进行了初始化
     */
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

    /**
     * 获取有效的配方列表
     * @return 配方列表
     */
    private List<R> getValidRecipesFor() {
        List<R> allRecipesFor;
        if (this.lastTaskRule.equals(CookData.Mode.WHITELIST.name)) {
            allRecipesFor = task.getRecipeHolders(level).stream().filter(r -> recipeIds.contains(r.id().toString())).map(RecipeHolder::value).toList();
        } else {
            allRecipesFor = task.getRecipeHolders(level).stream().filter(r -> !recipeIds.contains(r.id().toString())).map(RecipeHolder::value).toList();
        }
        return allRecipesFor;
    }

    /**
     * 检查当前物品栏是否与上次相同
     * @return 是否相同
     */
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

    /**
     * 将箱子中的原料映射到物品栏中
     */
    public void mapChestIngredient() {
        if (!hasCulinaryHub) return;

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
                IItemHandler iItemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, ingredientPo, null);
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

        List<Pair<List<Integer>, List<Item>>> _make = this.createIngres(available, false);
        if (_make.isEmpty()) return;

        // 转移箱子原料至CookBag
        for (Pair<List<Integer>, List<Item>> listListPair : _make) {
            List<Integer> first = listListPair.getFirst();
            List<Item> second = listListPair.getSecond();

            for (int i = 0; i < first.size(); i++) {
                Integer i1 = first.get(i);
                Item item = second.get(i);
                if (i1 <= 0 || item == null) continue;

                List<ItemStack> itemStacks = ingredientAmount.get(item);
                for (ItemStack itemStack : itemStacks) {
                    if (itemStack.isEmpty()) continue;
                    int count = itemStack.getCount();
                    // 减去当前物品的数量还是大于0，证明还没满足，就整体移动

                    Pair<IItemHandler, Integer> iTrackedContentsItemHandlerIntegerPair = stackContentHandler.get(itemStack);

                    IItemHandler first1 = iTrackedContentsItemHandlerIntegerPair.getFirst();
                    Integer second1 = iTrackedContentsItemHandlerIntegerPair.getSecond();

                    if (i1 - count > 0) {
                        ItemStack copy = itemStack.copy();

                        ItemStack itemStack1 = ItemHandlerHelper.insertItemStacked(inventory, copy, false);
                        first1.extractItem(second1, itemStack.getCount() - itemStack1.getCount(), false);
                    } else {
                        ItemStack copy = itemStack.copyWithCount(i1);

                        ItemStack itemStack1 = ItemHandlerHelper.insertItemStacked(inventory, copy, false);
                        first1.extractItem(second1, i1 - itemStack1.getCount(), false);
                        break;
                    }
                    i1 -= count;
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
        this.getCookInv().syncInv();
    }

    /**
     * 检查方块位置是否超出女仆活动范围
     * @param ingredientPo 方块位置
     * @return 是否超出范围
     */
    private boolean isPosZone(BlockPos ingredientPo) {
        float maxDistance = maid.getRestrictRadius();
        if (maid.distanceToSqr(ingredientPo.getX(), ingredientPo.getY(), ingredientPo.getZ()) > (maxDistance * maxDistance)) {
            return true;
        }
        return false;
    }

    /**
     * 初始化管理器
     * @return 是否进行了初始化
     */
    private boolean init() {
        boolean initTaskData = this.initTaskData();
        boolean initInvData = this.initInvData();
        if (initTaskData || initInvData) {
            this.recipesIngredients = Collections.emptyList();
            return true;
        }

        return false;
    }

    /**
     * 创建配方材料缓存
     */
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

    /**
     * 将输出物品转移到箱子
     */
    public void tranOutput2Chest() {
        this.tranCookBag2Chest(BagType.OUTPUT, false);
    }

    /**
     * 将未使用的原料转移到箱子
     */
    public void tranUnIngre2Chest() {
        this.tranCookBag2Chest(BagType.INGREDIENT, true);
    }

    /**
     * 根据可用物品创建配方材料
     * @param setRecipeIngres 是否设置配方材料缓存
     */
    private void createIngres(boolean setRecipeIngres) {
        Map<Item, Integer> maidAvailableInv = this.getMaidAvailableInv();
        this.createIngres(maidAvailableInv, setRecipeIngres);
    }

    /**
     * 根据可用物品创建配方材料
     * @param available 可用物品映射
     * @param setRecipeIngres 是否设置配方材料缓存
     * @return 配方材料列表
     */
    protected List<Pair<List<Integer>, List<Item>>> createIngres(Map<Item, Integer> available, boolean setRecipeIngres) {
        List<Pair<List<Integer>, List<Item>>> _make = getRecIngreMake(available);

        if (setRecipeIngres) {
            setRecIngres(_make, available);
        }

        return _make;
    }

    /**
     * 获取配方可制作的材料列表
     * @param available 可用物品映射
     * @return 配方材料列表
     */
    @NotNull
    protected List<Pair<List<Integer>, List<Item>>> getRecIngreMake(Map<Item, Integer> available) {
        List<Pair<List<Integer>, List<Item>>> _make = new ArrayList<>();
        for (R r : this.currentRecs) {
            Pair<List<Integer>, List<Item>> maxCount = this.getAmountIngredient(r, available);
            if (!maxCount.getFirst().isEmpty()) {
                _make.add(Pair.of(maxCount.getFirst(), maxCount.getSecond()));
            }
        }
        repeat(_make, available, this.repeatTimes);
        return _make;
    }

    /**
     * 设置配方材料缓存
     * @param _make 配方材料列表
     * @param available 可用物品映射
     */
    protected void setRecIngres(List<Pair<List<Integer>, List<Item>>> _make, Map<Item, Integer> available) {
        if (_make.isEmpty()) return;
        this.recipesIngredients = new ArrayList<>(transform(_make, available));
    }

    /**
     * 获取女仆可用的物品映射
     * @return 物品映射
     */
    @NotNull
    protected Map<Item, Integer> getMaidAvailableInv() {
        return new HashMap<>(getCookInv().getInventoryItem());
    }

    /**
     * 获取烹饪物品栏
     * @return 烹饪物品栏接口
     */
    public ICookInventory getCookInv() {
        return this.cookInv;
    }

    /**
     * 重复配方材料
     * @param oriList 原始配方材料列表
     * @param available 可用物品映射
     * @param times 重复次数
     */
    protected void repeat(List<Pair<List<Integer>, List<Item>>> oriList, Map<Item, Integer> available, int times) {
        ArrayList<Pair<List<Integer>, List<Item>>> oriPairs = new ArrayList<>(oriList);
        for (int l = 0; l < times; l++) {
            for (Pair<List<Integer>, List<Item>> listListPair : oriPairs) {
                List<Integer> first = listListPair.getFirst();
                List<Item> second = listListPair.getSecond();

                boolean canRepeat = true;
                for (int i = 0; i < second.size(); i++) {
                    Integer availableCount = available.get(second.get(i));
                    if (availableCount < first.get(i)) {
                        canRepeat = false;
                        break;
                    }
                }

                if (canRepeat) {
                    for (int i = 0; i < second.size(); i++) {
                        Item item = second.get(i);
                        available.put(item, available.get(item) - first.get(i));
                    }
                    oriList.add(listListPair);
                }
            }
        }
    }

    /**
     * 转换配方材料格式
     * @param oriList 原始配方材料列表
     * @param available 可用物品映射
     * @return 转换后的配方材料列表
     */
    protected List<Pair<List<Integer>, List<List<ItemStack>>>> transform(List<Pair<List<Integer>, List<Item>>> oriList, Map<Item, Integer> available) {
        Map<Item, List<ItemStack>> inventoryStack = this.getCookInv().getInventoryStack();
//        return oriList.stream().map(p -> Pair.of(p.getFirst(), p.getSecond().stream().map(inventoryStack::get).toList())).toList();

        List<Pair<List<Integer>, List<List<ItemStack>>>> list1 = oriList.stream().map(p -> {
            List<List<ItemStack>> list = p.getSecond().stream().map(item -> {
//                return inventoryStack.get(item);
                return inventoryStack.getOrDefault(item, new ArrayList<>());
            }).toList();
            return Pair.of(p.getFirst(), list);
        }).toList();
        return list1;
    }

    /**
     * 获取配方需要的材料数量和物品
     * @param recipe 配方
     * @param available 可用物品映射
     * @return 材料数量和物品对
     */
    protected Pair<List<Integer>, List<Item>> getAmountIngredient(R recipe, Map<Item, Integer> available) {
        List<Ingredient> ingredients = task.getIngredients(recipe);
        List<Item> invIngredient = new ArrayList<>();
        Map<Item, Integer> itemTimes = new HashMap<>();
        boolean[] canMake = {true};
        boolean[] single = {false};

        extraStartRecipe(recipe, available, canMake, single, itemTimes, invIngredient);

        for (Ingredient ingredient : ingredients) {
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

        extraEndRecipe(recipe, available, canMake, single, itemTimes, invIngredient);

//        if (!canMake[0] || invIngredient.stream().anyMatch(item -> available.get(item) <= 0)) {
        if (!canMake[0] || itemTimes.entrySet().stream().anyMatch(entry -> available.get(entry.getKey()) < entry.getValue())) {
            return Pair.of(Collections.emptyList(), Collections.emptyList());
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

        List<Integer> countList = new ArrayList<>();
        for (Item item : invIngredient) {
            countList.add(maxCount);
            available.put(item, available.get(item) - maxCount);
        }

        return Pair.of(countList, invIngredient);
    }

    /**
     * 配方开始前的额外处理
     */
    protected boolean extraStartRecipe(R recipe, Map<Item, Integer> available, boolean[] single, boolean[] canMake, Map<Item, Integer> itemTimes, List<Item> invIngredient) {
        return true;
    }

    /**
     * 配方结束后的额外处理
     */
    protected boolean extraEndRecipe(R recipe, Map<Item, Integer> available, boolean[] single, boolean[] canMake, Map<Item, Integer> itemTimes, List<Item> invIngredient) {
        return true;
    }

    /**
     * 减少输出附加物品的数量
     * @param findItem 要查找的物品
     * @param count 减少数量
     */
    public void shrinkOutputAdditionItem(ItemStack findItem, int count) {
        if (hasCulinaryHub) {
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

    /**
     * 获取输出附加物品的数量
     * @param findItem 要查找的物品
     * @return 物品数量
     */
    public int getOutputAdditionItemCount(ItemStack findItem) {
        if (hasCulinaryHub) {
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

    /**
     * 检查是否有输出附加物品（使用谓词）
     * @param findItem 物品谓词
     * @return 是否有物品
     */
    public boolean hasOutputAdditionItem(Predicate<ItemStack> findItem) {
        if (this.hasCulinaryHub) {
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

    /**
     * 从绑定的箱子中检查是否有附加物品（使用谓词）
     * @param findItem 物品谓词
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @return 是否有物品
     */
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

    /**
     * 查找输出附加物品（使用谓词）
     * @param findItem 物品谓词
     * @return 找到的物品堆
     */
    public ItemStack findOutputAdditionItem(Predicate<ItemStack> findItem) {
        if (hasCulinaryHub) {
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

    /**
     * 从绑定的箱子中获取附加物品（使用谓词）
     * @param findItem 物品谓词
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @return 找到的物品堆
     */
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

    /**
     * 检查是否有输出附加物品
     * @param findItem 要查找的物品
     * @return 是否有物品
     */
    public boolean hasOutputAdditionItem(ItemStack findItem) {
        if (this.hasCulinaryHub) {
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

    /**
     * 查找输出附加物品
     * @param findItem 要查找的物品
     * @return 找到的物品堆
     */
    public ItemStack findOutputAdditionItem(ItemStack findItem) {
        if (hasCulinaryHub) {
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

    /**
     * 从绑定的箱子中检查是否有附加物品
     * @param findItem 要查找的物品
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @return 是否有物品
     */
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

    /**
     * 从绑定的箱子中获取附加物品的数量
     * @param findItem 要查找的物品
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @return 物品数量
     */
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

    /**
     * 减少绑定箱子中附加物品的数量
     * @param findItem 要查找的物品
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @param count 减少数量
     */
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

    /**
     * 从绑定的箱子中获取附加物品
     * @param findItem 要查找的物品
     * @param bindModePoses 绑定的方块位置列表
     * @param level 世界
     * @return 找到的物品堆
     */
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

    /**
     * 获取输出物品栏
     * @return 物品栏处理器
     */
    public IItemHandlerModifiable getOutputInv() {
        return this.getBagContainerInv(BagType.OUTPUT);
    }

    /**
     * 获取输出附加物品栏
     * @return 物品栏处理器
     */
    public IItemHandlerModifiable getOutputAdditionInv() {
        return this.getBagContainerInv(BagType.OUTPUT_ADDITION);
    }

    /**
     * 获取输入物品栏
     * @return 物品栏处理器
     */
    public IItemHandlerModifiable getInputInv() {
        return this.getBagContainerInv(BagType.INGREDIENT);
    }

    /**
     * 获取指定类型的背包容器物品栏
     * @param bagType 背包类型
     * @return 物品栏处理器
     */
    private IItemHandlerModifiable getBagContainerInv(BagType bagType) {
        return this.getCookInv().getAvailableInv(maid, bagType);
    }

    /**
     * 获取原料物品栏
     * @return 物品栏处理器
     */
    @Nullable
    public IItemHandlerModifiable getIngredientInv() {
        return this.getInputInv();
    }

    /**
     * 是否启用烹饪中枢交互
     * @return 是否启用
     */
    protected boolean enableHub() {
        return true;
    }
}
