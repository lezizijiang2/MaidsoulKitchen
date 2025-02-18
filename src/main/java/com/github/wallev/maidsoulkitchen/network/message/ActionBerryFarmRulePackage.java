package com.github.wallev.maidsoulkitchen.network.message;


import com.github.tartaricacid.touhoulittlemaid.network.message.MaidModelPackage;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.BerryData;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
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

public record ActionBerryFarmRulePackage(int entityId, ResourceLocation dataKey, String rec) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActionBerryFarmRulePackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("berry_farm_rule"));
    public static final StreamCodec<ByteBuf, ActionBerryFarmRulePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ActionBerryFarmRulePackage::entityId,
            ResourceLocation.STREAM_CODEC,
            ActionBerryFarmRulePackage::dataKey,
            ByteBufCodecs.STRING_UTF8,
            ActionBerryFarmRulePackage::rec,
            ActionBerryFarmRulePackage::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(ActionBerryFarmRulePackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<BerryData> value = TaskDataRegister.getValue(message.dataKey);
                    BerryData fruitData = maid.getOrCreateData(value, new BerryData());
                    fruitData.addOrRemoveRule(message.rec);
                    maid.setAndSyncData(value, fruitData);
                }
            });
        }
    }



}
