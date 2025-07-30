package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.cook.v0.CookData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetCookDataModeC2SPackage(int entityId, ResourceLocation dataKey,
                                        String mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetCookDataModeC2SPackage> TYPE =
            new CustomPacketPayload.Type<>(getResourceLocation("set_cook_data_mode_c2s"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, SetCookDataModeC2SPackage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SetCookDataModeC2SPackage::entityId,
                    ResourceLocation.STREAM_CODEC,
                    SetCookDataModeC2SPackage::dataKey,
                    ByteBufCodecs.STRING_UTF8,
                    SetCookDataModeC2SPackage::mode,
                    SetCookDataModeC2SPackage::new
            );

    public static void handle(SetCookDataModeC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) return;
                var entity = sender.level.getEntity(message.entityId());
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<CookData> value = TaskDataRegister.getValue(message.dataKey);
                    CookData cookData = maid.getOrCreateData(value, new CookData());
                    cookData.setMode(message.mode);
                    maid.setAndSyncData(value, cookData);
                }
            });
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
