package com.github.wallev.maidsoulkitchen.client.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.client.MaidContainerGuiEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class RenderSlotHighEventLegacy {

    @SubscribeEvent
    public void renderSlotHigh(MaidContainerGuiEvent.Render event) {
        SlotRenderAndTipsHandler.renderSlotHighlight(event.getGui(), event.getGraphics(), event.getLeftPos(), event.getTopPos());
        SlotRenderAndTipsHandler.renderTips(event.getGui(), event.getGraphics(), event.getLeftPos(), event.getTopPos());
    }

}
