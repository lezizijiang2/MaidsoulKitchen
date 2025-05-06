package com.github.wallev.maidsoulkitchen.mixin.farmersdelight;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

@Mixin(SkilletBlockEntity.class)
public interface SkilletBlockEntityMixin {
    
    @Accessor("cookingTime")
    int getCookingTime();
    
    @Accessor("cookingTimeTotal")
    int getCookingTimeTotal();

}
