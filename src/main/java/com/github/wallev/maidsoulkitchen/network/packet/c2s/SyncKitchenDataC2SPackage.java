package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.cook.v1.CookDataV1;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.cook.v1.KitchenData;
import com.github.wallev.maidsoulkitchen.init.touhoulittlemaid.DataRegister;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncKitchenDataC2SPackage(int maidId, KitchenData kitchenData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncKitchenDataC2SPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_kitchen_data_c2s"));

    public static final StreamCodec<ByteBuf, SyncKitchenDataC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncKitchenDataC2SPackage::maidId,
            createKitchenDataCodec(),
            SyncKitchenDataC2SPackage::kitchenData,
            SyncKitchenDataC2SPackage::new
    );

    private static StreamCodec<ByteBuf, KitchenData> createKitchenDataCodec() {
        return new StreamCodec<>() {
            @Override
            public KitchenData decode(ByteBuf buf) {
                ResourceLocation cookName = ResourceLocation.STREAM_CODEC.decode(buf);

                int cookDataSize = ByteBufCodecs.VAR_INT.decode(buf);
                Map<ResourceLocation, CookDataV1> cookDataMap = Maps.newHashMap();

                for (int i = 0; i < cookDataSize; i++) {
                    ResourceLocation typeName = ResourceLocation.STREAM_CODEC.decode(buf);
                    CookDataV1 cookData = createCookDataCodec().decode(buf);
                    cookDataMap.put(typeName, cookData);
                }

                return new KitchenData(cookDataMap, cookName);
            }

            @Override
            public void encode(ByteBuf buf, KitchenData kitchenData) {
                ResourceLocation.STREAM_CODEC.encode(buf, kitchenData.getCookName());

                ByteBufCodecs.VAR_INT.encode(buf, kitchenData.getCookDatas().size());
                kitchenData.getCookDatas().forEach((typeName, cookData) -> {
                    ResourceLocation.STREAM_CODEC.encode(buf, typeName);
                    createCookDataCodec().encode(buf, cookData);
                });
            }
        };
    }

    private static StreamCodec<ByteBuf, CookDataV1> createCookDataCodec() {
        return new StreamCodec<>() {
            @Override
            public CookDataV1 decode(ByteBuf buf) {
                String mode = ByteBufCodecs.STRING_UTF8.decode(buf);

                int whiteListSize = ByteBufCodecs.VAR_INT.decode(buf);
                List<String> whiteList = Lists.newArrayList();
                for (int i = 0; i < whiteListSize; i++) {
                    String whiteRec = ByteBufCodecs.STRING_UTF8.decode(buf);
                    whiteList.add(whiteRec);
                }

                int blackListSize = ByteBufCodecs.VAR_INT.decode(buf);
                List<String> blackList = Lists.newArrayList();
                for (int i = 0; i < blackListSize; i++) {
                    String blackRec = ByteBufCodecs.STRING_UTF8.decode(buf);
                    blackList.add(blackRec);
                }

                return new CookDataV1(mode, whiteList, blackList);
            }

            @Override
            public void encode(ByteBuf buf, CookDataV1 cookData) {
                ByteBufCodecs.STRING_UTF8.encode(buf, cookData.mode());

                ByteBufCodecs.VAR_INT.encode(buf, cookData.whitelistRecs().size());
                cookData.whitelistRecs().forEach(rec -> ByteBufCodecs.STRING_UTF8.encode(buf, rec));

                ByteBufCodecs.VAR_INT.encode(buf, cookData.blacklistRecs().size());
                cookData.blacklistRecs().forEach(rec -> ByteBufCodecs.STRING_UTF8.encode(buf, rec));
            }
        };
    }

    public static void handle(SyncKitchenDataC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) {
                    return;
                }
                Entity entity = sender.level().getEntity(message.maidId());
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<KitchenData> kitchenDataTaskDataKey2 = TaskDataRegister.getValue(DataRegister.COOK.getKey());
                    maid.setAndSyncData(kitchenDataTaskDataKey2, message.kitchenData());
                }
            });
        }
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
