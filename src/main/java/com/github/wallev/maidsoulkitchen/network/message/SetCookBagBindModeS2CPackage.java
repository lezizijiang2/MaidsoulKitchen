package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetCookBagBindModeS2CPackage(String mode) implements CustomPacketPayload {

    public static final Type<SetCookBagBindModeS2CPackage> TYPE = new Type<>(getResourceLocation("set_cook_bag_bind_poses_s2c"));
    public static final StreamCodec<ByteBuf, SetCookBagBindModeS2CPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetCookBagBindModeS2CPackage::mode,
            SetCookBagBindModeS2CPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetCookBagBindModeS2CPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player sender = context.player();
                ItemCulinaryHub.setBindModeTag(sender.getMainHandItem(), message.mode);
            });
        }
    }
}
