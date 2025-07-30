package com.github.wallev.maidsoulkitchen.mixin.compat.kaleidoscope;


import com.github.wallev.maidsoulkitchen.modclazzchecker.core.classana.IMccMixinInterface;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskMixin;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@TaskMixin(TaskInfo.KC_CHOPPING_BOARD)
@Mixin(value = ChoppingBoardBlockEntity.class, remap = false)
public interface ChoppingBoardBlockEntityAccessor extends IMccMixinInterface {
    @Accessor("currentCutStack")
    ItemStack tlmk$getCurrentCutStack();

}
