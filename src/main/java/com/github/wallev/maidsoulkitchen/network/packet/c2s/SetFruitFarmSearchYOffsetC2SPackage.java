package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.FruitData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetFruitFarmSearchYOffsetC2SPackage(int entityId, ResourceLocation dataKey,
                                                  int searchYOffset) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetFruitFarmSearchYOffsetC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_fruit_farm_search_y_offset_c2s"));
    public static final StreamCodec<ByteBuf, SetFruitFarmSearchYOffsetC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetFruitFarmSearchYOffsetC2SPackage::entityId,
            ResourceLocation.STREAM_CODEC,
            SetFruitFarmSearchYOffsetC2SPackage::dataKey,
            ByteBufCodecs.VAR_INT,
            SetFruitFarmSearchYOffsetC2SPackage::searchYOffset,
            SetFruitFarmSearchYOffsetC2SPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetFruitFarmSearchYOffsetC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level.getEntity(message.entityId);
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
