package com.github.wallev.maidsoulkitchen.compat.msm.common.util.action;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskClassAnalyzer;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import studio.fantasyit.maid_storage_manager.storage.Target;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Utility class for creating Target objects for craft guide steps.
 * Provides convenience methods for creating targets with/without sides and virtual targets.
 * 
 * <p>NeoForge 1.21.1 Migration Notes:
 * - No Forge-specific APIs used
 * - Depends on maid_storage_manager NeoForge 1.21.1 (studio.fantasyit.maid_storage_manager.storage.Target)
 * 
 * @author Wall-ev (original 1.20.1-Forge)
 * @author Copilot (1.21.1-NeoForge port)
 */
@TaskClassAnalyzer(TaskInfo.MSM_CORE)
public class TargetUtil {
    
    /**
     * Creates a target without a specific side.
     * 
     * @param id Resource location identifier for the target
     * @param pos Block position of the target
     * @return Target with no side specified
     */
    public static Target makeTargetNoSide(ResourceLocation id, BlockPos pos) {
        return new Target(id, pos, Optional.empty());
    }

    /**
     * Creates a target with an optional side.
     * 
     * @param id Resource location identifier for the target
     * @param pos Block position of the target
     * @param side Direction side (can be null)
     * @return Target with optional side
     */
    public static Target makeTarget(ResourceLocation id, BlockPos pos, @Nullable Direction side) {
        if (side == null) {
            return makeTargetNoSide(id, pos);
        } else {
            return new Target(id, pos, Optional.of(side));
        }
    }

    /**
     * Creates a virtual target without a side.
     * Virtual targets are used for actions that don't require specific block entities.
     * 
     * @param clickedPos Position where the action occurs
     * @return Virtual target with no side
     */
    public static Target makeTargetVirtualNoSide(BlockPos clickedPos) {
        return Target.virtual(clickedPos, null);
    }

    /**
     * Creates a virtual target with a specific side.
     * 
     * @param id Resource location identifier (unused for virtual targets in current impl)
     * @param clickedPos Position where the action occurs
     * @param side Direction side for the action
     * @return Virtual target with specified side
     */
    public static Target makeTargetVirtual(ResourceLocation id, BlockPos clickedPos, Direction side) {
        return Target.virtual(clickedPos, side);
    }

}
