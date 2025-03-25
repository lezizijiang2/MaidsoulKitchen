package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record ToggleCookBagGuiSideTabC2SPackage(int tabId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleCookBagGuiSideTabC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("toggle_cook_bag_gui_side_tab_c2s"));
    public static final StreamCodec<ByteBuf, ToggleCookBagGuiSideTabC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ToggleCookBagGuiSideTabC2SPackage::tabId,
            ToggleCookBagGuiSideTabC2SPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleCookBagGuiSideTabC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                ItemCulinaryHub.openCookBagGuiFromSideTab(sender, message.tabId);
            });
        }
    }
}