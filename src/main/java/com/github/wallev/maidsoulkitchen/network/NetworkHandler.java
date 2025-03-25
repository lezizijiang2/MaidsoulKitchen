package com.github.wallev.maidsoulkitchen.network;

import com.github.wallev.maidsoulkitchen.network.message.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String VERSION = "1.0.0";

    public static void registerPacket(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION).optional();

        registrar.playToServer(ActionBerryFarmRuleC2SPackage.TYPE, ActionBerryFarmRuleC2SPackage.STREAM_CODEC, ActionBerryFarmRuleC2SPackage::handle);
        registrar.playToServer(ActionCookDataRecC2SPackage.TYPE, ActionCookDataRecC2SPackage.STREAM_CODEC, ActionCookDataRecC2SPackage::handle);
        registrar.playToServer(ClearCookBagBindPosesC2SPackage.TYPE, ClearCookBagBindPosesC2SPackage.STREAM_CODEC, ClearCookBagBindPosesC2SPackage::handle);
        registrar.playToServer(ActionFruitFarmRuleC2SPackage.TYPE, ActionFruitFarmRuleC2SPackage.STREAM_CODEC, ActionFruitFarmRuleC2SPackage::handle);
        registrar.playToServer(SetCookBagBindModeC2SPackage.TYPE, SetCookBagBindModeC2SPackage.STREAM_CODEC, SetCookBagBindModeC2SPackage::handle);
        registrar.playToServer(SetCookDataC2SPackage.TYPE, SetCookDataC2SPackage.STREAM_CODEC, SetCookDataC2SPackage::handle);
        registrar.playToServer(SetFruitFarmSearchYOffsetC2SPackage.TYPE, SetFruitFarmSearchYOffsetC2SPackage.STREAM_CODEC, SetFruitFarmSearchYOffsetC2SPackage::handle);
        registrar.playToServer(ToggleCookBagGuiSideTabC2SPackage.TYPE, ToggleCookBagGuiSideTabC2SPackage.STREAM_CODEC, ToggleCookBagGuiSideTabC2SPackage::handle);

        registrar.playToClient(SetCookBagBindModeS2CPackage.TYPE, SetCookBagBindModeS2CPackage.STREAM_CODEC, SetCookBagBindModeS2CPackage::handle);
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend) {
        if (entity.level instanceof ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, toSend);
        }
    }

    public static void sendToServer(CustomPacketPayload toSend) {
        PacketDistributor.sendToServer(toSend);
    }

    public static void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload toSend) {
        PacketDistributor.sendToPlayer(serverPlayer, toSend);
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend, int distance) {
        if (entity.level instanceof ServerLevel serverLevel) {
            BlockPos pos = entity.blockPosition();
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), distance, toSend);
        }
    }
}
