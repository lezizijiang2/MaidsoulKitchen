package com.github.wallev.maidsoulkitchen.task.cook.common.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.init.MkEntities;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.inv.MaidCookManager;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.cook.AbstractCookRule;
import com.github.wallev.maidsoulkitchen.util.MemoryUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask.checkOwnerPos;
import static com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask.getSearchPos;

public class MaidCookMoveTask<B extends BlockEntity, R extends Recipe<? extends RecipeInput>> extends MaidCheckRateTask implements BehaviorControl<EntityMaid> {
    private static final int MAX_DELAY_TIME = 120;
    private final float movementSpeed;
    private final int verticalSearchRange;
    private final ICookTask<B, R> task;
    private final MaidCookManager<R> rm;
    private final AbstractCookRule<B, R> rule;
    private final CookBeBase<B> cookBe;
    protected int verticalSearchStart;

    public MaidCookMoveTask(ICookTask<B, R> task, MaidCookManager<R> rm, AbstractCookRule<B, R> rule, CookBeBase<B> cookBe) {
        this(task, rm, rule, cookBe, ICookTask.MOVE_SPEED, ICookTask.VERTICAL_SEARCH_RANGE);
    }

    public MaidCookMoveTask(ICookTask<B, R> task, MaidCookManager<R> rm, AbstractCookRule<B, R> rule, CookBeBase<B> cookBe, float movementSpeed, int verticalSearchRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT,
                MkEntities.WORK_POS.get(), MemoryStatus.VALUE_ABSENT));
        this.task = task;
        this.rm = rm;
        this.rule = rule;
        this.cookBe = cookBe;

        this.movementSpeed = movementSpeed;
        this.verticalSearchRange = verticalSearchRange;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid maid, long pGameTime) {
        this.searchForDestination(worldIn, maid);
    }

    private boolean processRecipeManager() {
        return this.rm.checkAndCreateRecipesIngredients();
    }

    @SuppressWarnings("unchecked")
    protected boolean shouldMoveTo(ServerLevel worldIn, EntityMaid maid, BlockPos blockPos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return false;
        }
        if (cookBe.isCookBe(blockEntity)) {
            boolean processed = this.processRecipeManager();
            if (!processed) return false;
            cookBe.setBe((B) blockEntity);
            return this.rule.canMoveTo(cookBe, rm);
        }
        return false;
    }

    protected boolean checkPathReach(EntityMaid maid, BlockPos pos) {
        return maid.canPathReach(pos);
    }

    protected final void searchForDestination(ServerLevel worldIn, EntityMaid maid) {
        BlockPos centrePos = getSearchPos(maid);
        int searchRange = (int) maid.getRestrictRadius();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int y = this.verticalSearchStart; y <= this.verticalSearchRange; y = y > 0 ? -y : 1 - y) {
            for (int i = 0; i < searchRange; ++i) {
                for (int x = 0; x <= i; x = x > 0 ? -x : 1 - x) {
                    for (int z = x < i && x > -i ? i : 0; z <= i; z = z > 0 ? -z : 1 - z) {
                        mutableBlockPos.setWithOffset(centrePos, x, y + 1, z);

                        if (maid.isWithinRestriction(mutableBlockPos) && shouldMoveTo(worldIn, maid, mutableBlockPos)
//                                && checkPathReach(maid, mutableBlockPos)
                                && checkOwnerPos(maid, mutableBlockPos)) {
                            MemoryUtil.rememberWorkPos(maid, mutableBlockPos.immutable(), 0.3f, 0);
                            // 获取目标方块位置（烹饪设备位置）
                            BlockPos targetBlockPos = mutableBlockPos.immutable();

                            // 计算女仆应该站立的位置
                            BlockPos standingPos = calculateStandingPosition(worldIn, targetBlockPos);

                            MemoryUtil.rememberWorkPos(maid, standingPos, targetBlockPos, 0.3f, 0);
//                            debugInfo(maid, mutableBlockPos);
                            this.setNextCheckTickCount(5);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 计算女仆应该站立的位置
     * 对于不完整方块（如砧板、煎锅），女仆应该站在方块旁边，而不是上方
     */
    private BlockPos calculateStandingPosition(ServerLevel worldIn, BlockPos targetPos) {
        BlockState targetBlockState = worldIn.getBlockState(targetPos);

        // 检查目标方块是否是不完整方块（高度小于1）
        if (!targetBlockState.isCollisionShapeFullBlock(worldIn, targetPos)) {
            // 对于不完整方块，寻找一个相邻的可站立位置
            BlockPos[] adjacentPositions = {
                    targetPos.north(),
                    targetPos.south(),
                    targetPos.east(),
                    targetPos.west()
            };

            for (BlockPos adjacentPos : adjacentPositions) {
                // 检查相邻位置是否可以站立
                if (isValidStandingPosition(worldIn, adjacentPos) || isValidStandingPosition(worldIn, adjacentPos.below())) {
                    return adjacentPos;
                }
            }
        }

        // 对于完整方块或找不到合适相邻位置时，站在方块上方
        return targetPos;
    }

    /**
     * 检查指定位置是否是有效的站立位置
     */
    private boolean isValidStandingPosition(ServerLevel worldIn, BlockPos pos) {
        // 检查脚下的方块是否是固体
        BlockState groundState = worldIn.getBlockState(pos.below());
        if (!groundState.isSolid()) {
            return false;
        }

        // 检查站立位置和头部位置是否为空
        BlockState standingState = worldIn.getBlockState(pos);
        BlockState headState = worldIn.getBlockState(pos.above());

        return standingState.isAir() && headState.isAir();
    }

    private void debugInfo(EntityMaid maid, BlockPos pos) {
        BlockState blockState = maid.level.getBlockState(pos);
        MaidsoulKitchen.LOGGER.debug("{} MoveTo {} {}", maid, pos, blockState);
    }
}
