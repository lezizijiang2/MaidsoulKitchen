package com.github.wallev.maidsoulkitchen.api;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.api.event.MaidMkTaskEnableEvent;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("all")
public interface IMaidsoulKitchenTask extends IMaidTask {

    default TaskBookEntryType getBookEntryType() {
        return TaskBookEntryType.OTHER;
    }

    default String getBookEntry() {
        return this.getBookEntryType().name;
    }

    @Override
    default boolean isEnable(EntityMaid maid) {
        MaidMkTaskEnableEvent maidMkTaskEnableEvent = new MaidMkTaskEnableEvent(maid, this);
        var eventPosted = NeoForge.EVENT_BUS.post(maidMkTaskEnableEvent);
        if (!eventPosted.isCanceled()) {
            return maidMkTaskEnableEvent.isEnable();
        }

        return IMaidTask.super.isEnable(maid);
    }

    @Override
    default List<Pair<String, Predicate<EntityMaid>>> getEnableConditionDesc(EntityMaid maid) {
        MaidMkTaskEnableEvent maidMkTaskEnableEvent = new MaidMkTaskEnableEvent(maid, this);
        var eventPosted = NeoForge.EVENT_BUS.post(maidMkTaskEnableEvent);
        if (!eventPosted.isCanceled()) {
            return maidMkTaskEnableEvent.getEnableConditionDesc();
        }

        return IMaidTask.super.getEnableConditionDesc(maid);
    }

    @Override
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        return Collections.emptyList();
    }


    @Override
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
        List<Pair<Integer, BehaviorControl>> rideBrainTasks = vCreateRideBrainTasks(maid);
        if (!rideBrainTasks.isEmpty()) {
            return (List) rideBrainTasks;
        }
        return IMaidTask.super.createRideBrainTasks(maid);
    }

    default List<Pair<Integer, BehaviorControl>> vCreateRideBrainTasks(EntityMaid entityMaid) {
        return Collections.emptyList();
    }

    default IItemHandlerModifiable getInventory(EntityMaid maid) {
        return maid.getAvailableInv(false);
    }

}
