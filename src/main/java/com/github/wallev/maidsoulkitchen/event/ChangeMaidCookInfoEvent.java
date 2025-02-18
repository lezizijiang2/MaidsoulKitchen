package com.github.wallev.maidsoulkitchen.event;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = MaidsoulKitchen.MOD_ID)
public class ChangeMaidCookInfoEvent {
    @SubscribeEvent
    public static void copyEntityId(PlayerInteractEvent.EntityInteract event) {
//        Player player = event.getEntity();
//        InteractionHand hand = event.getHand();
//        Entity target = event.getTarget();
//        if (target instanceof EntityMaid maid && maid.getTask() instanceof ICookTask<?,?> cookTask && player.getItemInHand(hand).is(InitItems.BURN_PROTECT_BAUBLE.get())) {
////            NetworkHandler.sendToNearby(maid, new SetCookDataPackage(maid.getId(), cookTask.getCookDataKey().getKey(), CookData.Mode.SELECT.name));
//            CookData data = maid.getData(cookTask.getCookDataKey());
//            if (data != null) {
//                data.setMode(CookData.Mode.SELECT.name);
//            }
//            event.setCanceled(true);
//        }
    }
}
