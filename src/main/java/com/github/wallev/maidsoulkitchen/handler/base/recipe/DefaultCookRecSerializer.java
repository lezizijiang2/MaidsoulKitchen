package com.github.wallev.maidsoulkitchen.handler.base.recipe;

import com.github.wallev.maidsoulkitchen.handler.base.mkrecipe.DefaultCookRec;
import com.google.common.collect.Lists;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultCookRecSerializer<R extends Recipe<? extends RecipeInput>> extends AbstractCookRecInitializer<R> {
    public DefaultCookRecSerializer(RecipeType<R> recipeType) {
        super(recipeType);
    }

    @Override
    protected void initialize(Level level) {
        this.initRecipes(level);
        for (RecipeHolder<R> rec : this.holders) {
            List<Ingredient> ingredients = getIngredients(rec.value());
            List<Item> resultItem = Lists.newArrayList(getResultItem(rec.value(), level).getItem());
            List<List<Item>> ingreItems = ingredients.stream()
                .map(ingredient -> {
                    List<Item> itemSet = Arrays.stream(ingredient.getItems())
                            .map(ItemStack::getItem)
                            .collect(Collectors.toList());
                    this.validIngres.addAll(itemSet);
                    return itemSet;
                })
                .collect(Collectors.toList());
            DefaultCookRec<R> cookRec = new DefaultCookRec<>(rec.value(), ingreItems, resultItem, rec.id().toString());
            this.cookRecs.add(cookRec);
            this.cookRecData.put(rec.value(), cookRec);
        }
    }
}
