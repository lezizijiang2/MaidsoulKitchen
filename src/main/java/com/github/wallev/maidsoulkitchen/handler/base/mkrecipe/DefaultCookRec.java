package com.github.wallev.maidsoulkitchen.handler.base.mkrecipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class DefaultCookRec<R extends Recipe<? extends RecipeInput>> extends AbstractCookRec<R> {
    public DefaultCookRec(R rec, List<List<Item>> ingres, List<Item> result, boolean single) {
        super(rec, ingres, result, single);
    }

    public DefaultCookRec(R rec, List<List<Item>> ingres, List<Item> result) {
        super(rec, ingres, result);
    }
}