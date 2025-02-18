package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.item.ItemCulinaryHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record ClearCookBagBindPosesPackage(int id) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClearCookBagBindPosesPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("clear_book_bag_bind_poses"));
    public static final StreamCodec<ByteBuf, ClearCookBagBindPosesPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClearCookBagBindPosesPackage::id,
            ClearCookBagBindPosesPackage::new);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClearCookBagBindPosesPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                ItemStack mainHandItem = sender.getMainHandItem();
                ItemCulinaryHub.removeModePoses(mainHandItem);
            });
        }
    }
}
