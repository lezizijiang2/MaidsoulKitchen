package com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.skillet;

import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.inv.item.ItemInventory;
import com.github.wallev.maidsoulkitchen.task.cook.common.manager.MaidCookManager;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.cook.AbstractCookRule;
import com.github.wallev.maidsoulkitchen.util.MaidUtil;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

import java.util.Objects;

public class SkilletCookRule extends AbstractCookRule<SkilletBlockEntity, CampfireCookingRecipe> {
    private static final SkilletCookRule INSTANCE = new SkilletCookRule();

    public static SkilletCookRule getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean canMoveTo(CookBeBase<SkilletBlockEntity> cookBeBase, MaidCookManager<CampfireCookingRecipe> rc) {
        SkilletBlockEntity cuttingBoard = cookBeBase.getBe();

        return !cuttingBoard.hasStoredStack() && cuttingBoard.isHeated() && rc.hasMaidRecs(cookBeBase);
    }

    @Override
    public void cookMake(CookBeBase<SkilletBlockEntity> cookBeBase, MaidCookManager<CampfireCookingRecipe> rc) {
        boolean pickAction = false;

        // 放入烹饪的原材料
        if (cookBeBase.cookStateMatch() && !cookBeBase.recMatch() && rc.hasMaidRecs(cookBeBase)) {
            ItemInventory itemInventory = rc.getItemInventory();
            cookBeBase.insertInputs(rc.pollMaidRec(cookBeBase), itemInventory);
            cookBeBase.markChanged();
            rc.getItemInventory().markDirty();

            pickAction = true;
        }

        if (pickAction) {
            MaidUtil.pickupAction(cookBeBase);
        }
    }

    @Override
    public AbstractCookRule<SkilletBlockEntity, CampfireCookingRecipe> getOrCreate() {
        return Objects.requireNonNullElseGet(INSTANCE, SkilletCookRule::new);
    }
}
