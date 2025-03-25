package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import com.github.wallev.maidsoulkitchen.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetCookBagBindModeC2SPackage(String mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetCookBagBindModeC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_cook_bag_bind_poses_c2s"));
    public static final StreamCodec<ByteBuf, SetCookBagBindModeC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetCookBagBindModeC2SPackage::mode,
            SetCookBagBindModeC2SPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetCookBagBindModeC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                ItemCulinaryHub.setBindModeTag(sender.getMainHandItem(), message.mode);
                NetworkHandler.sendToClient(sender, new SetCookBagBindModeS2CPackage(message.mode));
            });
        }
    }
}
