package com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.skillet;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.IInvHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

public class SkilletBe extends CookBeBase<SkilletBlockEntity> {
    public SkilletBe(EntityMaid maid) {
        super(maid);
    }

    @Override
    public boolean isCookBe(BlockEntity be) {
        return be instanceof SkilletBlockEntity;
    }

    @Override
    public IInvHandler getInv() {
        return (IInvHandler) be.getInventory();
    }

    @Override
    public int getIngredientSize() {
        return 1;
    }

    @Override
    public int getResultSlot() {
        return 0;
    }

    @Override
    public boolean recMatch() {
        return false;
    }

    @Override
    public boolean cookStateMatch() {
        return be.isHeated();
    }

    @Override
    public void markChanged() {
        this.defaultChanged();
    }
}
