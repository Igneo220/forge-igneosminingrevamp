package net.igneo.imv.block.custom;

import net.igneo.imv.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.BlockTagsProvider;

import java.util.List;
import java.util.Optional;

public class MossySaturiniumBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
    public MossySaturiniumBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        System.out.println("just ticking!!!");
        if (!pPos.above().equals(BlockTags.REPLACEABLE)) {
            pLevel.setBlock(pPos, ModBlocks.SATURINIUM.get().defaultBlockState(),2);
        }
        super.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        System.out.println("randomly ticking!!!");
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean b) {
        return levelReader.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        BlockPos $$4 = blockPos.above();
        BlockState $$5 = Blocks.MOSS_CARPET.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> $$6 = serverLevel.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);

        label49:
        for(int $$7 = 0; $$7 < 128; ++$$7) {
            BlockPos $$8 = $$4;

            for(int $$9 = 0; $$9 < $$7 / 16; ++$$9) {
                $$8 = $$8.offset(randomSource.nextInt(3) - 1, (randomSource.nextInt(3) - 1) * randomSource.nextInt(3) / 2, randomSource.nextInt(3) - 1);
                if (!serverLevel.getBlockState($$8.below()).is(this) || serverLevel.getBlockState($$8).isCollisionShapeFullBlock(serverLevel, $$8)) {
                    continue label49;
                }
            }

            BlockState $$10 = serverLevel.getBlockState($$8);
            if ($$10.is($$5.getBlock()) && randomSource.nextInt(10) == 0) {
                ((BonemealableBlock)$$5.getBlock()).performBonemeal(serverLevel, randomSource, $$8, $$10);
            }

            if ($$10.isAir()) {
                Holder $$13;
                if (randomSource.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> $$11 = ((Biome)serverLevel.getBiome($$8).value()).getGenerationSettings().getFlowerFeatures();
                    if ($$11.isEmpty()) {
                        continue;
                    }

                    $$13 = ((RandomPatchConfiguration)((ConfiguredFeature)$$11.get(0)).config()).feature();
                } else {
                    if (!$$6.isPresent()) {
                        continue;
                    }

                    $$13 = $$6.get();
                }

                ((PlacedFeature)$$13.value()).place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, $$8);
            }
        }

    }
}
