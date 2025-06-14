package com.github.wallev.maidsoulkitchen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record GiveRecipeIngredientC2SPackage(List<ItemStack> list) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GiveRecipeIngredientC2SPackage> TYPE =
            new CustomPacketPayload.Type<>(getResourceLocation("give_recipe_ingredient_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GiveRecipeIngredientC2SPackage> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.LIST_STREAM_CODEC,
                    GiveRecipeIngredientC2SPackage::list,
                    GiveRecipeIngredientC2SPackage::new
            );

    public static void handle(GiveRecipeIngredientC2SPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) return;
                for (ItemStack itemStack : message.list()) {
                    sender.addItem(itemStack);
                }
            });
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
