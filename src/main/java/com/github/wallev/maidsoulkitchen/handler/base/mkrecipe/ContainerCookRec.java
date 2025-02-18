package com.github.wallev.maidsoulkitchen.handler.base.mkrecipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class ContainerCookRec<R extends Recipe<? extends RecipeInput>> extends AbstractCookRec<R>{
    protected final Item container;

    public ContainerCookRec(R rec, List<List<Item>> ingres, List<Item> result, boolean single, Item container) {
        super(rec, ingres, result, single);
        this.container = container;
    }

    public ContainerCookRec(R rec, List<List<Item>> ingres, List<Item> result, Item container) {
        super(rec, ingres, result);
        this.container = container;
    }

    public Item getOutputContainer() {
        return container;
    }
}
