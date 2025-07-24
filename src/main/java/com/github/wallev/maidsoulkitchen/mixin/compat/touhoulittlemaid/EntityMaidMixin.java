package com.github.wallev.maidsoulkitchen.mixin.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.util.OldDataHelper;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Enhanced EntityMaid mixin for old data transformation
 * Ported from upstream 1.20.1 commit 24440d9bf0b8c4622afb7ad6c459b3a0194ad660
 */
@Mixin(EntityMaid.class)
public class EntityMaidMixin {

    @Inject(method = "readAdditionalSaveData", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void maidsoulkitchen$parseOldData(CompoundTag compound, CallbackInfo ci) {
        OldDataHelper.transOldKitchenData(compound);
    }
}