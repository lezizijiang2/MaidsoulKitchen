package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetCookDataPackage(int entityId, ResourceLocation dataKey, String mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetCookDataPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_cook_daata"));
    public static final StreamCodec<ByteBuf, SetCookDataPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetCookDataPackage::entityId,
            ResourceLocation.STREAM_CODEC,
            SetCookDataPackage::dataKey,
            ByteBufCodecs.STRING_UTF8,
            SetCookDataPackage::mode,
            SetCookDataPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetCookDataPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) {
                    return;
                }
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<CookData> value = TaskDataRegister.getValue(message.dataKey);
                    CookData cookData = maid.getOrCreateData(value, new CookData());
                    cookData.setMode(message.mode);
                    maid.setAndSyncData(value, cookData);
                }
            });
        }
    }
}
