package com.github.wallev.maidsoulkitchen.network;

import com.github.wallev.maidsoulkitchen.network.packet.c2s.*;
import com.github.wallev.maidsoulkitchen.network.packet.s2c.RenderMaidHubZoneS2CPackage;
import com.github.wallev.maidsoulkitchen.network.packet.s2c.SetCookBagBindModeS2CPackage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public final class NetworkHandler {
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
        registrar.playToClient(RenderMaidHubZoneS2CPackage.TYPE, RenderMaidHubZoneS2CPackage.STREAM_CODEC, RenderMaidHubZoneS2CPackage::handle);
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

    public static class C2S {
        public static void toggleCookBagGuiSideTab(int tabId) {
            sendToServer(new ToggleCookBagGuiSideTabC2SPackage(tabId));
        }

        public static void setCookBagBindMode(String mode) {
            sendToServer(new SetCookBagBindModeC2SPackage(mode));
        }

        public static void setCookDataMode(int entityId, ResourceLocation dataKey, String mode) {
            sendToServer(new SetCookDataModeC2SPackage(entityId, dataKey, mode));
        }

        public static void actionCookDataRec(int entityId, ResourceLocation dataKey, String rec, String mode) {
            sendToServer(new ActionCookDataRecC2SPackage(entityId, dataKey, rec, mode));
        }

        public static void actionCookDataRecs(int entityId, ResourceLocation dataKey, List<String> rec, boolean add) {
            sendToServer(new ActionCookDataRecsC2SPackage(entityId, dataKey, rec, add));
        }

        public static void setFruitFarmSearchYOffset(int entityId, ResourceLocation dataKey, int searchYOffset) {
            sendToServer(new SetFruitFarmSearchYOffsetC2SPackage(entityId, dataKey, searchYOffset));
        }

        public static void actionBerryFarmRule(int entityId, ResourceLocation dataKey, String rec) {
            sendToServer(new ActionBerryFarmRuleC2SPackage(entityId, dataKey, rec));
        }

        public static void actionFruitFarmRule(int entityId, ResourceLocation dataKey, String rec) {
            sendToServer(new ActionFruitFarmRuleC2SPackage(entityId, dataKey, rec));
        }

        public static void clearCookBagBindPoses() {
            sendToServer(new ClearCookBagBindPosesC2SPackage());
        }

        public static void giveRecipeIngredient(List<ItemStack> itemStacks) {
            sendToServer(new GiveRecipeIngredientC2SPackage(itemStacks));
        }

        public static void syncBerryFruitData(int maidId, ResourceLocation taskId, BerryFruitData data) {
            sendToServer(new SyncBerryFruitDataC2SPackage(maidId, taskId, data));
        }

        public static void syncKitchenData2(int maidId, KitchenData data) {
            sendToServer(new SyncKitchenDataC2SPackage(maidId, data));
        }
    }

    public static class S2C {
        public static void renderMaidHubZone(int maidId, ServerPlayer player) {
            sendToClient(player, new RenderMaidHubZoneS2CPackage(maidId));
        }
    }

    public static class SAC {

    }
}
