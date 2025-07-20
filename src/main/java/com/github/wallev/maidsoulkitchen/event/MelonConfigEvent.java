package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.config.subconfig.TaskConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.wallev.maidsoulkitchen.util.BlockUtil.getId;

@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MelonConfigEvent {
    public static final Map<Block, Block> MELON_STEM_MAP = new HashMap<>();
    private static final String CONFIG_NAME = MaidsoulKitchen.MOD_ID + "-common.toml";

    @SubscribeEvent
    public static void onEvent(ModConfigEvent.Loading event) {
        String fileName = event.getConfig().getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            handleConfig();
        }
    }

    public static void handleConfig() {
        MELON_STEM_MAP.clear();
        handleMelonStemList(MELON_STEM_MAP);
        handleMelonAndStemList(TaskConfig.MELON_AND_STEM_LIST.get(), MELON_STEM_MAP);
    }

    private static void handleMelonStemList(Map<Block, Block> output) {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof AttachedStemBlock attachedStemBlock) {
                output.put(BuiltInRegistries.BLOCK.get(attachedStemBlock.fruit), attachedStemBlock);
                MaidsoulKitchen.LOGGER.debug("add fruit {}, stem {}", attachedStemBlock.fruit.location().toString(), getId(attachedStemBlock));
            }
        }
    }

    private static void handleMelonAndStemList(List<List<String>> config, Map<Block, Block> output) {
        for (List<String> strings : config) {
            if (strings.size() < 2) continue;

            String melonId = strings.get(0);
            String stemId = strings.get(1);


            ResourceLocation melonLoc = ResourceLocation.tryParse(melonId);
            ResourceLocation stemLoc = ResourceLocation.tryParse(stemId);
            if (melonLoc == null || stemLoc == null) continue;

            Block melonBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(melonId));
            Block stemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(stemId));
            if (melonBlock == null || stemBlock == null) continue;

            output.put(melonBlock, stemBlock);
        }
    }
}
