package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record ClearCookBagBindPosesC2SPackage() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClearCookBagBindPosesC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("clear_book_bag_bind_poses_c2s"));
    public static final StreamCodec<ByteBuf, ClearCookBagBindPosesC2SPackage> STREAM_CODEC = StreamCodec.unit(new ClearCookBagBindPosesC2SPackage());

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClearCookBagBindPosesC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                ItemStack mainHandItem = sender.getMainHandItem();
                ItemCulinaryHub.removeModePoses(mainHandItem);
            });
        }
    }
}
