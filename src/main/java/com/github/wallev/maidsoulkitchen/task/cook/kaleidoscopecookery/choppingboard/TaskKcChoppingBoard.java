package com.github.wallev.maidsoulkitchen.task.cook.kaleidoscopecookery.choppingboard;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.init.touhoulittlemaid.DataRegister;
import com.github.wallev.maidsoulkitchen.task.MaidsoulKitchenTask;
import com.github.wallev.maidsoulkitchen.task.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.cook.AbstractCookRule;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.RecSerializerManager;
import com.github.wallev.maidsoulkitchen.util.classana.clazz.TaskClassAnalyzer;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@TaskClassAnalyzer(TaskInfo.KC_CHOPPING_BOARD)
public class TaskKcChoppingBoard extends ICookTask<ChoppingBoardBlockEntity, ChoppingBoardRecipe> {
    @Override
    protected AbstractCookRule<ChoppingBoardBlockEntity, ChoppingBoardRecipe> createCookRule() {
        return ChoppingBoardRule.getInstance();
    }

    @Override
    protected RecSerializerManager<ChoppingBoardRecipe> createRecSerializerManager() {
        return ChoppingBoardRecSerializerManager.getInstance();
    }

    @Override
    protected CookBeBase<ChoppingBoardBlockEntity> createCookBe(EntityMaid maid) {
        return new ChoppingBoardBe(maid);
    }

    @Override
    public ResourceLocation getUid() {
        return MaidsoulKitchenTask.KC_CHOPPING_BOARD.uid;
    }

    @Override
    public ItemStack getIcon() {
        return ModItems.CHOPPING_BOARD.get().getDefaultInstance();
    }

    @Override
    public TaskDataKey<CookData> getCookDataKey() {
        return DataRegister.KC_CHOPPING_BOARD;
    }
}
