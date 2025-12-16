package com.github.wallev.maidsoulkitchen.compat.msm.drinkbeer.beerbarrel;


import com.github.wallev.maidsoulkitchen.compat.msm.common.inv.InvHandlerRegister;
import com.github.wallev.maidsoulkitchen.compat.msm.common.inv.IInvHandlerFactory;
import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.github.wallev.maidsoulkitchen.task.cook.common.cook.inv.IInvHandler;
import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import lekavar.lma.drinkbeer.registries.BlockEntityRegistry;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;

@InvHandlerRegister(TaskInfo.MSM_DB_DRINKBEER_BEERBARREL)
public class BeerBarrelBlockEntityContainerInvRegister extends IInvHandlerFactory<BeerBarrelBlockEntity> {

    public BeerBarrelBlockEntityContainerInvRegister() {
        super(BlockEntityRegistry.BEER_BARREL_TILEENTITY.get());
    }

    @Override
    protected IInvHandler create(BeerBarrelBlockEntity blockEntity, @Nullable Direction side) {
        if (side != null) {
            switch (side) {
                case DOWN:
                    return (IInvHandler) blockEntity.getCapability(Capabilities.ItemHandler.BLOCK, Direction.DOWN)
                            .orElseThrow(() -> new IllegalStateException("BeerBarrelBlockEntityContainerInvRegister: DOWN side is not available"));
            }
        }

        return (IInvHandler) blockEntity.getBrewingInventory();
    }
}
