package com.github.wallev.maidsoulkitchen.compat.msm.common.inv;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskClassAnalyzer;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.IInvHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import javax.annotation.Nullable;

import static com.github.wallev.maidsoulkitchen.compat.msm.common.inv.InvHandlersHelper.INV_HANDLERS;

@TaskClassAnalyzer(TaskInfo.MSM_CORE)
public abstract class IInvHandlerFactory<B extends BlockEntity> {

    public IInvHandlerFactory(BlockEntityType<?> type) {
        INV_HANDLERS.put(type, this);
    }

    @SuppressWarnings("unchecked")
    public final IInvHandler createInv(BlockEntity blockEntity, @Nullable Direction side) {
        return create((B) blockEntity, side);
    }

    protected abstract IInvHandler create(B blockEntity, @Nullable Direction side);

    protected final IInvHandler createForFdPot(B blockEntity, ItemStackHandler itemStackHandler, @Nullable Direction side) {
        if (side == null) {
            return (IInvHandler) blockEntity.getCapability(Capabilities.ItemHandler.BLOCK, null);
        }

        switch (side) {
            case EAST -> {
                return (IInvHandler) new RangedWrapper(itemStackHandler, 7, 8);
            }
            default -> {
                return (IInvHandler) blockEntity.getCapability(Capabilities.ItemHandler.BLOCK, null);
            }
        }
    }
}
