package com.github.wallev.maidsoulkitchen.mixin.compat.kaleidoscope;

import com.github.wallev.maidsoulkitchen.task.TaskInfo;
import com.github.wallev.maidsoulkitchen.util.classana.IMaidsoulKitchenInterface;
import com.github.wallev.maidsoulkitchen.util.classana.TaskMixin;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@TaskMixin(task = TaskInfo.KC_CHOPPING_BOARD)
@Mixin(value = ChoppingBoardBlockEntity.class, remap = false)
public interface ChoppingBoardBlockEntityAccessor extends IMaidsoulKitchenInterface {
    @Accessor("currentCutStack")
    public ItemStack tlmk$getCurrentCutStack();

}
