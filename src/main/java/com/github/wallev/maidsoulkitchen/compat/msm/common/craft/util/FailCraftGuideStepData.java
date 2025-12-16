package com.github.wallev.maidsoulkitchen.compat.msm.common.craft.util;

import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskClassAnalyzer;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import studio.fantasyit.maid_storage_manager.craft.action.ActionOption;
import studio.fantasyit.maid_storage_manager.craft.action.ActionOptionSet;
import studio.fantasyit.maid_storage_manager.craft.data.CraftGuideStepData;

import java.util.Arrays;
import java.util.List;

/**
 * Record class for handling failure step data in craft guides.
 * Encodes/decodes failure steps using Codec and manages optional step flags.
 * 
 * <p>NeoForge 1.21.1 Migration Notes:
 * - No Forge-specific APIs used
 * - Uses Mojang Codec API (unchanged between Forge and NeoForge)
 * - Depends on maid_storage_manager NeoForge 1.21.1
 * 
 * <p>Failure steps are automatically marked as OPTIONAL to ensure craft guides
 * can continue even if failure handling steps don't execute perfectly.
 * 
 * @param steps List of craft guide step data representing failure handling
 * 
 * @author Wall-ev (original 1.20.1-Forge)
 * @author Copilot (1.21.1-NeoForge port)
 */
@TaskClassAnalyzer(TaskInfo.MSM_CORE)
public record FailCraftGuideStepData(List<CraftGuideStepData> steps) {
    
    /**
     * Constructs from varargs of steps.
     * 
     * @param steps Variable arguments of craft guide step data
     */
    public FailCraftGuideStepData(CraftGuideStepData... steps) {
        this(Arrays.stream(steps).toList());
    }

    /**
     * Canonical constructor that ensures all steps are properly configured.
     * Automatically marks steps as optional if they support the OPTIONAL action option.
     * 
     * @param steps List of craft guide step data
     */
    public FailCraftGuideStepData(List<CraftGuideStepData> steps) {
        this.steps = steps;
        this.checkAndMakeOptionalSteps();
    }

    /**
     * Ensures all failure steps that support OPTIONAL are marked as optional.
     * This prevents failure steps from blocking craft guide progression.
     */
    private void checkAndMakeOptionalSteps() {
        for (CraftGuideStepData step : this.steps) {
            ActionOption<Boolean> optionalAction = ActionOption.OPTIONAL;
            // Skip if this action type doesn't support OPTIONAL
            if (step.actionType.options().stream().noneMatch(o -> o.equals(optionalAction))) {
                continue;
            }

            // Set OPTIONAL to true if not already set or if set to false
            step.getOptionSelection(optionalAction)
                    .ifPresentOrElse(existOptional -> {
                        if (!existOptional) {
                            ActionOptionSet.with(optionalAction, true).applyTo(step);
                        }
                    }, () -> {
                        ActionOptionSet.with(optionalAction, true).applyTo(step);
                    });
        }
    }

    /**
     * Codec for serializing/deserializing failure craft guide step data.
     */
    public static final Codec<FailCraftGuideStepData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CraftGuideStepData.CODEC.listOf().fieldOf("fail_steps").forGetter(FailCraftGuideStepData::steps)
    ).apply(ins, FailCraftGuideStepData::new));

    /**
     * Encodes this failure step data to an NBT compound tag.
     * 
     * @return CompoundTag containing encoded failure steps
     */
    public CompoundTag toCompoundTag() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .result()
                .map(tag -> (CompoundTag) tag)
                .orElseGet(CompoundTag::new);
    }

    /**
     * Static helper to convert failure step data to compound tag.
     * 
     * @param failCraftGuideStepData The failure step data to encode
     * @return CompoundTag containing encoded data
     */
    public static CompoundTag toCompoundTag(FailCraftGuideStepData failCraftGuideStepData) {
        return failCraftGuideStepData.toCompoundTag();
    }

    /**
     * Static helper to convert step list to compound tag.
     * 
     * @param steps List of craft guide steps
     * @return CompoundTag containing encoded steps
     */
    public static CompoundTag toCompoundTag(List<CraftGuideStepData> steps) {
        return toCompoundTag(new FailCraftGuideStepData(steps));
    }

    /**
     * Static helper to convert varargs steps to compound tag.
     * 
     * @param steps Variable arguments of craft guide steps
     * @return CompoundTag containing encoded steps
     */
    public static CompoundTag toCompoundTag(CraftGuideStepData... steps) {
        return toCompoundTag(new FailCraftGuideStepData(steps));
    }

    /**
     * Decodes failure step data from an NBT compound tag.
     * Returns empty failure step data if parsing fails.
     * 
     * @param compoundTag NBT tag containing encoded failure steps
     * @return Decoded FailCraftGuideStepData or empty instance on failure
     */
    public static FailCraftGuideStepData toFailSteps(CompoundTag compoundTag) {
        return CODEC.parse(NbtOps.INSTANCE, compoundTag)
                .result()
                .orElse(new FailCraftGuideStepData());
    }

    /**
     * Factory method to create failure step data from list.
     * 
     * @param steps List of craft guide steps
     * @return New FailCraftGuideStepData instance
     */
    public static FailCraftGuideStepData create(List<CraftGuideStepData> steps) {
        return new FailCraftGuideStepData(steps);
    }

    /**
     * Factory method to create failure step data from varargs.
     * 
     * @param steps Variable arguments of craft guide steps
     * @return New FailCraftGuideStepData instance
     */
    public static FailCraftGuideStepData create(CraftGuideStepData... steps) {
        return new FailCraftGuideStepData(steps);
    }
}
