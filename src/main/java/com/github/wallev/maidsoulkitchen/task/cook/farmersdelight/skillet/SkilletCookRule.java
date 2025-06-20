package com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.skillet;

import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.inv.ItemInventory;
import com.github.wallev.maidsoulkitchen.task.cook.common.inv.MaidRecipesManager2;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.cook.AbstractCookRule;
import com.github.wallev.maidsoulkitchen.util.MaidUtil;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

public class SkilletCookRule extends AbstractCookRule<SkilletBlockEntity, CampfireCookingRecipe> {
    private static final SkilletCookRule INSTANCE = new SkilletCookRule();

    public static SkilletCookRule getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean canMoveTo(CookBeBase<SkilletBlockEntity> cookBeBase, MaidRecipesManager2<CampfireCookingRecipe> rm) {
        SkilletBlockEntity cuttingBoard = cookBeBase.getBe();

        return !cuttingBoard.hasStoredStack() && cuttingBoard.isHeated() && rm.hasMaidRecs(cookBeBase);
    }

    @Override
    public void cookMake(CookBeBase<SkilletBlockEntity> cookBeBase, MaidRecipesManager2<CampfireCookingRecipe> rm) {
        boolean pickAction = false;

        // 放入烹饪的原材料
        if (cookBeBase.cookStateMatch() && !cookBeBase.recMatch() && rm.hasMaidRecs(cookBeBase)) {
            ItemInventory itemInventory = rm.getItemInventory();
            cookBeBase.insertInputs(rm.pollMaidRec(cookBeBase), itemInventory);
            cookBeBase.markChanged();
            rm.getItemInventory().markDirty();

            pickAction = true;
        }

        if (pickAction) {
            MaidUtil.pickupAction(cookBeBase);
        }
    }

    @Override
    public AbstractCookRule<SkilletBlockEntity, CampfireCookingRecipe> getOrCreate() {
        return new SkilletCookRule();
    }
}
