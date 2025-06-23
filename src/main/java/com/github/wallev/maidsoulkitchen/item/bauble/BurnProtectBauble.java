package com.github.wallev.maidsoulkitchen.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAttackEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDamageEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityExtinguishingAgent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ItemBreakPackage;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.wallev.maidsoulkitchen.api.bauble.IMaidsoulKitchenBauble;
import com.github.wallev.maidsoulkitchen.datagen.ModDamageTypeTags;
import com.github.wallev.maidsoulkitchen.init.MkEffects;
import com.github.wallev.maidsoulkitchen.network.NetworkHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;


public class BurnProtectBauble implements IMaidsoulKitchenBauble {

    public BurnProtectBauble() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(MaidDamageEvent event) {
        EntityMaid maid = event.getMaid();
        DamageSource source = event.getSource();
        if (source.is(ModDamageTypeTags.DAMAGES_BURN)) {
            int slot = ItemsUtil.getBaubleSlotInMaid(maid, this);
            if (slot >= 0) {
                event.setCanceled(true);
                ItemStack stack = maid.getMaidBauble().getStackInSlot(slot);
                if (maid.level instanceof ServerLevel serverLevel) {
                    stack.hurtAndBreak(1, serverLevel, maid, m -> NetworkHandler.sendToNearby(maid, new ItemBreakPackage(maid.getId(), stack)));
                }
                maid.getMaidBauble().setStackInSlot(slot, stack);
                maid.addEffect(new MobEffectInstance(MkEffects.BURN_PROTECT, 300));
                if (!maid.level.isClientSide) {
                    maid.level.addFreshEntity(new EntityExtinguishingAgent(maid.level, maid.position()));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBurnDamage(MaidAttackEvent event) {
        EntityMaid maid = event.getMaid();
        DamageSource source = event.getSource();
        if (maid.hasEffect(MkEffects.BURN_PROTECT) && source.is(ModDamageTypeTags.DAMAGES_BURN)) {
            event.setCanceled(true);
        }
    }
}
