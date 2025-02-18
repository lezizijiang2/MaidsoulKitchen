package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.config.subconfig.TaskConfig;
import com.github.wallev.maidsoulkitchen.util.EmptyLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.wallev.maidsoulkitchen.util.BlockUtil.getId;

@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MelonConfigEvent {
    public static final Map<String, String> MELON_STEM_MAP = new HashMap<>();
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

    private static void handleMelonStemList(Map<String, String> output) {
        // todo: 找到更好的方式获取瓜和瓜藤的对应关系
//        for (Block block : BuiltInRegistries.BLOCK) {
//            if (block instanceof AttachedStemBlock attachedStemBlock) {
//                BlockState defaultState = attachedStemBlock.defaultBlockState();
//                Direction facing = defaultState.getValue(AttachedStemBlock.FACING);
//                BlockPos pos = BlockPos.ZERO;
//                BlockPos fruitPos = pos.relative(facing);
//
//                // Get the fruit block from the block update behavior
//                BlockState testState = defaultState.updateShape(facing, Blocks.AIR.defaultBlockState(),
//                        EmptyLevelAccessor.INSTANCE, pos, fruitPos);

                    output.put(getId(Blocks.MELON), getId(Blocks.ATTACHED_MELON_STEM));
                    output.put(getId(Blocks.PUMPKIN), getId(Blocks.ATTACHED_PUMPKIN_STEM));
//            }
//        }
    }

    private static void handleMelonAndStemList(List<List<String>> config, Map<String, String> output) {
        for (List<String> strings : config) {
            if (strings.size() < 2) continue;

            String melonId = strings.get(0);
            String stemId = strings.get(1);

            Block melonBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(melonId));
            Block stemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(stemId));
            if (melonBlock == null || stemBlock == null) continue;

            output.put(melonId, stemId);
        }
    }
}
