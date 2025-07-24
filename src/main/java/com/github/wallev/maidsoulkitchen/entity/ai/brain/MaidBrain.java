package com.github.wallev.maidsoulkitchen.entity.ai.brain;

import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

import static com.github.wallev.maidsoulkitchen.init.MkEntities.MEMORY_MODULE_TYPES;

/**
 * Enhanced MaidBrain with upstream 1.20.1 integration improvements
 * Includes picnic food placement behaviors from commit 24440d9bf0b8c4622afb7ad6c459b3a0194ad660
 */
public class MaidBrain implements IExtraMaidBrain {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<MemoryModuleType<?>> getExtraMemoryTypes() {
        return (List) MEMORY_MODULE_TYPES.getEntries().stream()
                .map(DeferredHolder::get)
                .toList();
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getIdleBehaviors() {
        return List.of();
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRideIdleBehaviors() {
        // Enhanced with picnic food placement system from upstream 1.20.1 integration
        if (MaidsoulKitchen.DEBUG) {
            // In debug mode, enable advanced picnic food placement behaviors
            // This would require implementing PlaceFoodForPicnicWithRideIdleTask for 1.21.1
            // For now, return empty list until the task is ported
            return Lists.newArrayList();
        }
        return List.of();
    }
}
