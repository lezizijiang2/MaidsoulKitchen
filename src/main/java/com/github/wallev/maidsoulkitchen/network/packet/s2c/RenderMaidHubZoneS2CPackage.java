package com.github.wallev.maidsoulkitchen.network.packet.s2c;

import com.github.wallev.maidsoulkitchen.client.event.DisplayHubWithMaidRangeEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record RenderMaidHubZoneS2CPackage(int maidId) implements CustomPacketPayload {

    public static final Type<RenderMaidHubZoneS2CPackage> TYPE = new Type<>(getResourceLocation("render_maid_hub_zone_s2c"));
    public static final StreamCodec<ByteBuf, RenderMaidHubZoneS2CPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            RenderMaidHubZoneS2CPackage::maidId,
            RenderMaidHubZoneS2CPackage::new
    );

    public static void handle(RenderMaidHubZoneS2CPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> writeMaidId(message));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void writeMaidId(RenderMaidHubZoneS2CPackage message) {
        DisplayHubWithMaidRangeEvent.add(message.maidId());
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

