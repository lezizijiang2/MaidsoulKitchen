package com.github.wallev.maidsoulkitchen.handler.base.mkcontainer;

import net.minecraft.world.Container;
import net.neoforged.neoforge.items.ItemStackHandler;

public interface IInvMcb {
    interface IContainerMcb {
        Container getCookBeInv();

    }

    interface IItemHandlerMcb {
        ItemStackHandler getCookBeInv();
    }
}
