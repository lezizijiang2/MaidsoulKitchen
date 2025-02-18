package com.github.wallev.maidsoulkitchen.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class KubejsTlmPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
//        event.register(ResourceLocation.fromNamespaceAndPath("touhou_little_maid:altar_crafting"), AltarJs.SCHEMA);
    }
}
