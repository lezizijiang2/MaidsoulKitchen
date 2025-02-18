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

public record ActionFruitFarmRulePackage(int entityId, ResourceLocation dataKey, String rec) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActionFruitFarmRulePackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("action_fruit_farm_rule"));
    public static final StreamCodec<ByteBuf, ActionFruitFarmRulePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ActionFruitFarmRulePackage::entityId,
            ResourceLocation.STREAM_CODEC,
            ActionFruitFarmRulePackage::dataKey,
            ByteBufCodecs.STRING_UTF8,
            ActionFruitFarmRulePackage::rec,
            ActionFruitFarmRulePackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActionFruitFarmRulePackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<FruitData> value = TaskDataRegister.getValue(message.dataKey);
                    FruitData fruitData = maid.getOrCreateData(value, new FruitData());
                    fruitData.addOrRemoveRule(message.rec);
                    maid.setAndSyncData(value, fruitData);
                }
            });
        }
    }
}
