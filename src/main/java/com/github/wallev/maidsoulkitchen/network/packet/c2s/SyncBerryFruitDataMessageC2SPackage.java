package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.wallev.maidsoulkitchen.api.task.farm.ICompatFarmTask;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.berryfruit.v1.BerryFruitData;
import com.github.wallev.maidsoulkitchen.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncBerryFruitDataMessageC2SPackage(int maidId, ResourceLocation taskId,
                                                  BerryFruitData berryFruitData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncBerryFruitDataMessageC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_berry_fruit_data_message_c2s"));

    public static final StreamCodec<ByteBuf, SyncBerryFruitDataMessageC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncBerryFruitDataMessageC2SPackage::maidId,
            ResourceLocation.STREAM_CODEC,
            SyncBerryFruitDataMessageC2SPackage::taskId,
            createBerryFruitDataCodec(),
            SyncBerryFruitDataMessageC2SPackage::berryFruitData,
            SyncBerryFruitDataMessageC2SPackage::new
    );

    private static StreamCodec<ByteBuf, BerryFruitData> createBerryFruitDataCodec() {
        return new StreamCodec<>() {
            @Override
            public BerryFruitData decode(ByteBuf buf) {
                Map<String, Boolean> rules = ByteBufUtil.readMapSB(new FriendlyByteBuf(buf));
                int searchYOffset = buf.readInt();
                return new BerryFruitData(rules, searchYOffset);
            }

            @Override
            public void encode(ByteBuf buf, BerryFruitData berryFruitData) {
                ByteBufUtil.writeMapSB(berryFruitData.rules(), new FriendlyByteBuf(buf));
                buf.writeInt(berryFruitData.searchYOffset());
            }
        };
    }

    public static void handle(SyncBerryFruitDataMessageC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) {
                    return;
                }
                if (sender.level().getEntity(message.maidId) instanceof EntityMaid maid) {
                    TaskManager.findTask(message.taskId).ifPresent(task -> {
                        TaskDataKey<BerryFruitData> cookDataKey = ((ICompatFarmTask<?>) task).getCookDataKey();
                        maid.setAndSyncData(cookDataKey, message.berryFruitData);
                    });
                }
            });
        }
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
