package com.github.wallev.maidsoulkitchen.init;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.task.cook.common.inv.item.ItemInventory;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.MaidRec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public final class MkEntities {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, MaidsoulKitchen.MOD_ID);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<PositionTracker>> WORK_POS = MEMORY_MODULE_TYPES.register("work_pos", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<Vec3>>> CURRENT_WORK_POSES = MEMORY_MODULE_TYPES.register("current_poses", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> CET_CHEST_ITEMHANDLER = MEMORY_MODULE_TYPES.register("cet_chest_itemhandler", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> GENERATE_RECS = MEMORY_MODULE_TYPES.register("generate_recs", () -> new MemoryModuleType<>(Optional.empty()));

    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ItemInventory>> HUB_INPUT_INVENTORY = MEMORY_MODULE_TYPES.register("hub_input_inventory", () -> new MemoryModuleType<>(Optional.empty()));
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ItemInventory>> HUB_OUTPUT_INVENTORY = MEMORY_MODULE_TYPES.register("hub_output_inventory", () -> new MemoryModuleType<>(Optional.empty()));
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ItemInventory>> INPUT_CHEST_INVENTORY = MEMORY_MODULE_TYPES.register("input_chest_inventory", () -> new MemoryModuleType<>(Optional.empty()));
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ItemInventory>> OUTPUT_CHEST_INVENTORY = MEMORY_MODULE_TYPES.register("output_chest_inventory", () -> new MemoryModuleType<>(Optional.empty()));
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<MaidRec>>> MAID_RECS = MEMORY_MODULE_TYPES.register("maid_recs", () -> new MemoryModuleType<>(Optional.empty()));
}
