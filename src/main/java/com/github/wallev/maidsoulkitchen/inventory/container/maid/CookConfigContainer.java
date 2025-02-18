package com.github.wallev.maidsoulkitchen.inventory.container.maid;

import com.github.tartaricacid.touhoulittlemaid.inventory.container.task.TaskConfigContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class CookConfigContainer extends TaskConfigContainer {
    public static final MenuType<CookConfigContainer> TYPE = IMenuTypeExtension.create((windowId, inv, data) -> new CookConfigContainer(windowId, inv, data.readInt()));

    public CookConfigContainer(int id, Inventory inventory, int entityId) {
        super(TYPE, id, inventory, entityId);
    }
}
