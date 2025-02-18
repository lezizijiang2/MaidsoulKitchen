package com.github.wallev.maidsoulkitchen.network;

import com.github.tartaricacid.touhoulittlemaid.network.message.*;
import com.github.wallev.maidsoulkitchen.network.message.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String VERSION = "1.0.0";

    public static void registerPacket(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION).optional();

        registrar.playToServer(ActionBerryFarmRulePackage.TYPE, ActionBerryFarmRulePackage.STREAM_CODEC, ActionBerryFarmRulePackage::handle);
        registrar.playToServer(ActionCookDataRecPackage.TYPE, ActionCookDataRecPackage.STREAM_CODEC, ActionCookDataRecPackage::handle);
        registrar.playToServer(ClearCookBagBindPosesPackage.TYPE, ClearCookBagBindPosesPackage.STREAM_CODEC, ClearCookBagBindPosesPackage::handle);
        registrar.playToServer(ActionFruitFarmRulePackage.TYPE, ActionFruitFarmRulePackage.STREAM_CODEC, ActionFruitFarmRulePackage::handle);
        registrar.playToServer(SetCookBagBindModePackage.TYPE, SetCookBagBindModePackage.STREAM_CODEC, SetCookBagBindModePackage::handle);
        registrar.playToServer(SetCookDataPackage.TYPE, SetCookDataPackage.STREAM_CODEC, SetCookDataPackage::handle);
        registrar.playToServer(SetFruitFarmSearchYOffsetPackage.TYPE, SetFruitFarmSearchYOffsetPackage.STREAM_CODEC, SetFruitFarmSearchYOffsetPackage::handle);
        registrar.playToServer(ToggleCookBagGuiSideTabPackage.TYPE, ToggleCookBagGuiSideTabPackage.STREAM_CODEC, ToggleCookBagGuiSideTabPackage::handle);
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend) {
        if (entity.level() instanceof ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, toSend);
        }
    }

    public static void sendToServer(CustomPacketPayload toSend) {

            PacketDistributor.sendToServer(toSend);
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend, int distance) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = entity.blockPosition();
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), distance, toSend);
        }
    }
}
