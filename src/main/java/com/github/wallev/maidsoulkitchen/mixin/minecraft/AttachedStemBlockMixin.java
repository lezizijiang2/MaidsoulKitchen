package com.github.wallev.maidsoulkitchen.mixin.minecraft;

import com.github.wallev.maidsoulkitchen.task.cook.v1.common.cbaccessor.IAttachedStemBlockFruitAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.world.level.block.AttachedStemBlock.class)
public abstract class AttachedStemBlockMixin implements IAttachedStemBlockFruitAccessor {
    @Shadow @Final private ResourceKey<Block> fruit;

    @Override
    public ResourceKey<Block> maidsoulKitchen$friut() {
        return this.fruit;
    }
}
