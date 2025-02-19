package com.github.wallev.maidsoulkitchen.handler.base.mkrecipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class DefaultCookRec<R extends Recipe<? extends RecipeInput>> extends AbstractCookRec<R> {
    public DefaultCookRec(R rec, List<List<Item>> ingres, List<Item> result, boolean single, String id) {
        super(rec, ingres, result, single, id);
    }

    public DefaultCookRec(R rec, List<List<Item>> ingres, List<Item> result, String id) {
        super(rec, ingres, result, id);
    }
}