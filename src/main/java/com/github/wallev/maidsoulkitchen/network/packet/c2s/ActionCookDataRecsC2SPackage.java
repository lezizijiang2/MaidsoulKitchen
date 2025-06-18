package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record ActionCookDataRecsC2SPackage(int entityId, ResourceLocation dataKey, List<String> recs,
                                           boolean add) implements CustomPacketPayload {

    public static final Type<ActionCookDataRecsC2SPackage> TYPE = new Type<>(getResourceLocation("action_cook_data_recs_c2s"));

    public static final StreamCodec<ByteBuf, ActionCookDataRecsC2SPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ActionCookDataRecsC2SPackage::entityId,
            ResourceLocation.STREAM_CODEC,
            ActionCookDataRecsC2SPackage::dataKey,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            ActionCookDataRecsC2SPackage::recs,
            ByteBufCodecs.BOOL,
            ActionCookDataRecsC2SPackage::add,
            ActionCookDataRecsC2SPackage::new
    );

    public static void handle(ActionCookDataRecsC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player sender = context.player();
                if (sender == null) {
                    return;
                }
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    TaskDataKey<CookData> value = TaskDataRegister.getValue(message.dataKey);
                    CookData cookData = maid.getOrCreateData(value, new CookData());

                    boolean add = message.add();
                    List<String> recs = message.recs();
                    if (add) {
                        cookData.addRecs(recs);
                    } else {
                        cookData.removeRecs(recs);
                    }

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
