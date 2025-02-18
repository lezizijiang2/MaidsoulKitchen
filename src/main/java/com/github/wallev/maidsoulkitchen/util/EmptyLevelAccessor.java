package com.github.wallev.maidsoulkitchen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class EmptyLevelAccessor implements LevelAccessor {
    public static final EmptyLevelAccessor INSTANCE = new EmptyLevelAccessor();

    @Override
    public long nextSubTickCount() {
        return 0;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public LevelData getLevelData() {
        return null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return null;
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return null;
    }

    @Override
    public RandomSource getRandom() {
        return null;
    }

    @Override
    public void playSound(@Nullable Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {

    }

    @Override
    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {

    }

    @Override
    public void levelEvent(@Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> p_316267_, Vec3 p_220405_, GameEvent.Context p_220406_) {

    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        return null;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos p_45570_) {
        return null;
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        return null;
    }

    @Override
    public boolean setBlock(BlockPos p_46947_, BlockState p_46948_, int p_46949_, int p_46950_) {
        return false;
    }

    @Override
    public boolean setBlock(@NotNull BlockPos pos, @NotNull BlockState state, int flags) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, @Nullable Entity p_46959_, int p_46960_) {
        return false;
    }

    @Override
    public boolean isStateAtPosition(@NotNull BlockPos pos, java.util.function.Predicate<BlockState> predicate) {
        return false;
    }

    @Override
    public boolean isFluidAtPosition(@NotNull BlockPos pos, java.util.function.Predicate<FluidState> predicate) {
        return false;
    }

    @Override
    public @NotNull RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return null;
    }


    @Override
    public @Nullable ChunkAccess getChunk(int p_46823_, int p_46824_, ChunkStatus p_330944_, boolean p_46826_) {
        return null;
    }

    @Override
    public int getHeight(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return null;
    }

    @Override
    public @NotNull Holder<Biome> getBiome(@NotNull BlockPos pos) {
        return null;
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public DimensionType dimensionType() {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
        return List.of();
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return List.of();
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }
}