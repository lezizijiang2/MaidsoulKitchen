package com.github.wallev.maidsoulkitchen.chest;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.wallev.maidsoulkitchen.foundation.utility.Mods;
import earth.terrarium.handcrafted.common.blockentities.ContainerBlockEntity;
import earth.terrarium.handcrafted.common.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class HandCraftedContainer implements IChestType {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.CONTAINER.get(), (container, side) -> new InvWrapper(container));
    }

    @Override
    public boolean isChest(BlockEntity blockEntity) {
        return Mods.HANDCRAFTED.isLoaded() && blockEntity instanceof ContainerBlockEntity;
    }

    @Override
    public boolean canOpenByPlayer(BlockEntity blockEntity, Player player) {
        return Mods.HANDCRAFTED.isLoaded() && blockEntity instanceof ContainerBlockEntity containerBlockEntity &&
                containerBlockEntity.canOpen(player);
    }

    @Override
    public int getOpenCount(BlockGetter blockGetter, BlockPos blockPos, BlockEntity blockEntity) {
        return ALLOW_COUNT;
    }

}