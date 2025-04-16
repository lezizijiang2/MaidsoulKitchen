package com.github.wallev.maidsoulkitchen.chest;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.wallev.maidsoulkitchen.foundation.utility.Mods;
import com.tom.storagemod.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TomSimpleStorageContainer implements IChestType {
    @Override
    public boolean isChest(BlockEntity blockEntity) {
        return Mods.TSS.isLoaded() && isTSSStorageBlock(blockEntity);
    }

    @Override
    public boolean canOpenByPlayer(BlockEntity blockEntity, Player player) {
        // TSS一般不限制玩家访问，所以如果是TSS方块实体且模组加载，则允许访问
        return Mods.TSS.isLoaded() && isTSSStorageBlock(blockEntity);
    }

    @Override
    public int getOpenCount(BlockGetter blockGetter, BlockPos blockPos, BlockEntity blockEntity) {
        return ALLOW_COUNT;
    }

    /**
     * 判断是否为TSS的存储方块实体
     *
     * @param blockEntity 方块实体
     * @return 是否为TSS存储方块
     */
    private boolean isTSSStorageBlock(BlockEntity blockEntity) {
        // 检查类名，因为不同版本的TSS可能会有不同的类路径
        return blockEntity instanceof InventoryCableConnectorBlockEntity ||
                blockEntity instanceof OpenCrateBlockEntity ||
                blockEntity instanceof InventoryConnectorBlockEntity ||
                blockEntity instanceof InventoryInterfaceBlockEntity ||
                blockEntity instanceof FilingCabinetBlockEntity ||
                blockEntity instanceof InventoryProxyBlockEntity;
    }
}