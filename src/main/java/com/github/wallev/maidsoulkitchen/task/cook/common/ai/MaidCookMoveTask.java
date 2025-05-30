package com.github.wallev.maidsoulkitchen.task.cook.common.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.task.cook.common.inventory.MaidRecipesManager;
import com.github.wallev.maidsoulkitchen.util.MemoryUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaidCookMoveTask<B extends BlockEntity, R extends Recipe<? extends RecipeInput>> extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 120;
    private final float movementSpeed;
    private final int verticalSearchRange;
    private final ICookTask<B, R> task;
    private final MaidRecipesManager<R> maidRecipesManager;
    protected int verticalSearchStart;

    public MaidCookMoveTask(ICookTask<B, R> task, MaidRecipesManager<R> maidRecipesManager) {
        this(task, 0.5f, 2, maidRecipesManager);
    }

    public MaidCookMoveTask(ICookTask<B, R> task, float movementSpeed, int verticalSearchRange, MaidRecipesManager<R> maidRecipesManager) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT));
        this.task = task;
        this.movementSpeed = movementSpeed;
        this.verticalSearchRange = verticalSearchRange;
        this.setMaxCheckRate(MAX_DELAY_TIME);
        this.maidRecipesManager = maidRecipesManager;
    }

    private static void setWalkAndLookTargetMemories(LivingEntity pLivingEntity, BlockPos walkPos, BlockPos lookPos, float pSpeed, int pDistance) {
        pLivingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(walkPos, pSpeed, pDistance));
        pLivingEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(lookPos));
        
        pLivingEntity.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(lookPos));
    }

    private static BlockPos getSearchPos(EntityMaid maid) {
        return maid.hasRestriction() ? maid.getRestrictCenter() : maid.blockPosition().below();
    }

    public MaidRecipesManager<R> getMaidRecipesManager() {
        return maidRecipesManager;
    }

    private boolean checkOwnerPos(EntityMaid maid, BlockPos mutableBlockPos) {
        if (maid.isHomeModeEnable()) {
            return true;
        }
        return maid.getOwner() != null && mutableBlockPos.closerToCenterThan(maid.getOwner().position(), 8);
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid maid, long pGameTime) {
        if (maid != this.maidRecipesManager.getMaid()) {
            return;
        }
        this.searchForDestination(worldIn, maid);
    }

    private boolean processRecipeManager() {
        return this.maidRecipesManager.checkAndCreateRecipesIngredients();
    }

    @SuppressWarnings("unchecked")
    protected boolean shouldMoveTo(ServerLevel worldIn, EntityMaid maid, BlockPos blockPos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return false;
        }
        if (this.task.isCookBE(blockEntity)) {
            boolean processed = this.processRecipeManager();
            if (!processed) return false;
            return this.task.shouldMoveTo(worldIn, this.maidRecipesManager.getMaid(), (B) blockEntity, maidRecipesManager);
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
                            // 获取目标方块位置（烹饪设备位置）
                            BlockPos targetBlockPos = mutableBlockPos.immutable();

                            // 计算女仆应该站立的位置
                            BlockPos standingPos = calculateStandingPosition(worldIn, targetBlockPos);

                            MemoryUtil.rememberWorkPos(maid, standingPos, targetBlockPos, this.movementSpeed, 0);
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
