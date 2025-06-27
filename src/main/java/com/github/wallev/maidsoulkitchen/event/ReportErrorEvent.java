package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.util.classana.TaskLoadError;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber
public class ReportErrorEvent {

//    @SubscribeEvent
//    public static void reportError(ServerStartedEvent event) {
//        TaskLoadError.reportError((component -> {
//            event.getServer().sendSystemMessage(component);
//        }));
//    }

    @SubscribeEvent
    public static void reportError(PlayerEvent.PlayerLoggedInEvent event) {
        TaskLoadError.reportError((component -> {
            event.getEntity().sendSystemMessage(component);
        }));
    }

}
