package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.init.MkItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitCreativeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;


@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ItemTabEvent {
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == InitCreativeTabs.MAIN_TAB.getKey()){
            MkItems.ITEMS.getEntries().stream()
                    .filter(entry -> entry.asOptional().isPresent())
                    .forEach(entry -> event.accept(entry.get()));
        }
    }
}
