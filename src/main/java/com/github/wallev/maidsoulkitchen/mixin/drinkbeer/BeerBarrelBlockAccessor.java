package com.github.wallev.maidsoulkitchen.mixin.drinkbeer;

import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BeerBarrelBlockEntity.class, remap = false)
public interface BeerBarrelBlockAccessor {

    @Accessor("statusCode")
    int tlmk$statusCode();

}
