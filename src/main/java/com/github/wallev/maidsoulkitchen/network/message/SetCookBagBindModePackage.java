package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetCookBagBindModePackage(String mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetCookBagBindModePackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_cook_bag_bind_poses"));
    public static final StreamCodec<ByteBuf, SetCookBagBindModePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetCookBagBindModePackage::mode,
            SetCookBagBindModePackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetCookBagBindModePackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) {
                    return;
                }
                ItemCulinaryHub.setBindModeTag(sender.getMainHandItem(), message.mode);
            });
        }
    }
}
