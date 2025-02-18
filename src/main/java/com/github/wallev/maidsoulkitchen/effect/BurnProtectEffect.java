package com.github.wallev.maidsoulkitchen.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BurnProtectEffect extends MobEffect {
    public BurnProtectEffect() {
        super(MobEffectCategory.BENEFICIAL, 14165807);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        return true;
    }
}
