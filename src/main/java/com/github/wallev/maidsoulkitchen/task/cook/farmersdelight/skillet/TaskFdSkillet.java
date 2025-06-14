package com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.skillet;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.api.task.cook.ICookTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.init.touhoulittlemaid.DataRegister;
import com.github.wallev.maidsoulkitchen.task.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.be.CookBeBase;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.cook.AbstractCookRule;
import com.github.wallev.maidsoulkitchen.task.cook.common.rule.rec.RecSerializerManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.registry.ModItems;

public class TaskFdSkillet extends ICookTask<SkilletBlockEntity, CampfireCookingRecipe> {

    @Override
    protected CookBeBase<SkilletBlockEntity> createCookBe(EntityMaid maid) {
        return new SkilletBe(maid);
    }

    @Override
    protected AbstractCookRule<SkilletBlockEntity, CampfireCookingRecipe> createCookRule() {
        return SkilletCookRule.getInstance();
    }

    @Override
    protected RecSerializerManager<CampfireCookingRecipe> createRecSerializerManager() {
        return SkilletRecSerializerManager.getInstance();
    }

    @Override
    public TaskDataKey<CookData> getCookDataKey() {
        return DataRegister.FD_SKILLET;
    }

    @Override
    public ResourceLocation getUid() {
        return TaskInfo.FD_SKILLET.uid;
    }

    @Override
    public ItemStack getIcon() {
        return ModItems.SKILLET.get().getDefaultInstance();
    }
}
