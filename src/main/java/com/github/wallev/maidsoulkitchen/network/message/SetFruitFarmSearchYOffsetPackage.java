package com.github.wallev.maidsoulkitchen.network.message;

import com.github.wallev.maidsoulkitchen.entity.data.inner.task.FruitData;
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

public record SetFruitFarmSearchYOffsetPackage(int entityId, ResourceLocation dataKey, int searchYOffset) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetFruitFarmSearchYOffsetPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_fruit_farm_search_y_offset"));
    public static final StreamCodec<ByteBuf, SetFruitFarmSearchYOffsetPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetFruitFarmSearchYOffsetPackage::entityId,
            ResourceLocation.STREAM_CODEC,
            SetFruitFarmSearchYOffsetPackage::dataKey,
            ByteBufCodecs.VAR_INT,
            SetFruitFarmSearchYOffsetPackage::searchYOffset,
            SetFruitFarmSearchYOffsetPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetFruitFarmSearchYOffsetPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<FruitData> value = TaskDataRegister.getValue(message.dataKey);
                    FruitData fruitData = maid.getOrCreateData(value, new FruitData());
                    fruitData.setSearchYOffset(message.searchYOffset);
                    maid.setAndSyncData(value, fruitData);
                }
            });
        }
    }
}
