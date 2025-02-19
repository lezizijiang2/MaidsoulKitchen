package com.github.wallev.maidsoulkitchen.handler.base.mkrecipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class ContainerCookRec<R extends Recipe<? extends RecipeInput>> extends AbstractCookRec<R>{
    protected final Item container;

    public ContainerCookRec(R rec, List<List<Item>> ingres, List<Item> result, boolean single, Item container, String id) {
        super(rec, ingres, result, single, id);
        this.container = container;
    }

    public ContainerCookRec(R rec, List<List<Item>> ingres, List<Item> result, Item container, String id) {
        super(rec, ingres, result, id);
        this.container = container;
    }

    public Item getOutputContainer() {
        return container;
    }
}
