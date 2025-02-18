package com.github.wallev.maidsoulkitchen.compat.kubejs.recipes;

import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipeSerializer;
import com.github.wallev.maidsoulkitchen.util.EntityCraftingHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.MapJS;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TlmOutputComponent implements RecipeComponent<EntityCraftingHelper.Output> {
    public static final RecipeComponent<EntityCraftingHelper.Output> INSTANCE = new TlmOutputComponent();
    @Override
    public String componentType() {
        return "touhou_little_maid_output";
    }

    @Override
    public Class<?> componentClass() {
        return EntityCraftingHelper.Output.class;
    }

    @Override
    public JsonElement write(RecipeJS recipe, EntityCraftingHelper.Output value) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("type", value.getType().getDescriptionId());
        if (!value.getData().isEmpty()){
            obj.addProperty("nbt", value.getData().toString());
        }
        return obj;
    }

    @Override
    public EntityCraftingHelper.Output read(RecipeJS recipe, Object from) {
        return EntityCraftingHelper.getEntityData(MapJS.json(from));
    }
}
