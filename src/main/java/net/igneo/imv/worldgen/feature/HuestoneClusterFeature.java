package net.igneo.imv.worldgen.feature;

import com.mojang.serialization.Codec;
import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.block.custom.HuestoneThickness;
import net.igneo.imv.block.custom.PointedHuestoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class HuestoneClusterFeature extends Feature<HuestoneClusterConfiguration> {
    public HuestoneClusterFeature(Codec<HuestoneClusterConfiguration> pCodec) {
        super(pCodec);
    }

    public boolean place(FeaturePlaceContext<HuestoneClusterConfiguration> pContext) {
        WorldGenLevel $$1 = pContext.level();
        BlockPos $$2 = pContext.origin();
        HuestoneClusterConfiguration $$3 = (HuestoneClusterConfiguration)pContext.config();
        RandomSource $$4 = pContext.random();
        if (!isEmptyOrWater($$1, $$2)) {
            return false;
        } else {
            int $$5 = $$3.height.sample($$4);
            float $$6 = $$3.wetness.sample($$4);
            float $$7 = $$3.density.sample($$4);
            int $$8 = $$3.radius.sample($$4);
            int $$9 = $$3.radius.sample($$4);

            for(int $$10 = -$$8; $$10 <= $$8; ++$$10) {
                for(int $$11 = -$$9; $$11 <= $$9; ++$$11) {
                    double $$12 = this.getChanceOfStalagmiteOrStalactite($$8, $$9, $$10, $$11, $$3);
                    BlockPos $$13 = $$2.offset($$10, 0, $$11);
                    this.placeColumn($$1, $$4, $$13, $$10, $$11, $$6, $$12, $$5, $$7, $$3);
                }
            }

            return true;
        }
    }

    protected static boolean isEmptyOrWater(LevelAccessor pLevel, BlockPos pPos) {
        return pLevel.isStateAtPosition(pPos, HuestoneUtils::isEmptyOrWater);
    }

    private void placeColumn(WorldGenLevel pLevel, RandomSource pRandom, BlockPos pPos, int pX, int pZ, float pWetness, double pChance, int pHeight, float pDensity, HuestoneClusterConfiguration pConfig) {
        Optional<Column> $$10 = Column.scan(pLevel, pPos, pConfig.floorToCeilingSearchRange, HuestoneUtils::isEmptyOrWater, HuestoneUtils::isNeitherEmptyNorWater);
        if ($$10.isPresent()) {
            OptionalInt $$11 = ((Column)$$10.get()).getCeiling();
            OptionalInt $$12 = ((Column)$$10.get()).getFloor();
            if ($$11.isPresent() || $$12.isPresent()) {
                boolean $$13 = pRandom.nextFloat() < pWetness;
                Column $$16;
                if ($$13 && $$12.isPresent() && this.canPlacePool(pLevel, pPos.atY($$12.getAsInt()))) {
                    int $$14 = $$12.getAsInt();
                    $$16 = ((Column)$$10.get()).withFloor(OptionalInt.of($$14 - 1));
                    pLevel.setBlock(pPos.atY($$14), Blocks.WATER.defaultBlockState(), 2);
                } else {
                    $$16 = (Column)$$10.get();
                }

                OptionalInt $$17 = $$16.getFloor();
                boolean $$18 = pRandom.nextDouble() < pChance;
                int $$23;
                int $$28;
                if ($$11.isPresent() && $$18 && !this.isLava(pLevel, pPos.atY($$11.getAsInt()))) {
                    $$28 = pConfig.dripstoneBlockLayerThickness.sample(pRandom);
                    this.replaceBlocksWithHuestoneBlocks(pLevel, pPos.atY($$11.getAsInt()), $$28, Direction.UP);
                    int $$21;
                    if ($$17.isPresent()) {
                        $$21 = Math.min(pHeight, $$11.getAsInt() - $$17.getAsInt());
                    } else {
                        $$21 = pHeight;
                    }

                    $$23 = this.getHuestoneHeight(pRandom, pX, pZ, pDensity, $$21, pConfig);
                } else {
                    $$23 = 0;
                }

                boolean $$24 = pRandom.nextDouble() < pChance;
                int $$37;
                if ($$17.isPresent() && $$24 && !this.isLava(pLevel, pPos.atY($$17.getAsInt()))) {
                    $$37 = pConfig.dripstoneBlockLayerThickness.sample(pRandom);
                    this.replaceBlocksWithHuestoneBlocks(pLevel, pPos.atY($$17.getAsInt()), $$37, Direction.DOWN);
                    if ($$11.isPresent()) {
                        $$28 = Math.max(0, $$23 + Mth.randomBetweenInclusive(pRandom, -pConfig.maxStalagmiteStalactiteHeightDiff, pConfig.maxStalagmiteStalactiteHeightDiff));
                    } else {
                        $$28 = this.getHuestoneHeight(pRandom, pX, pZ, pDensity, pHeight, pConfig);
                    }
                } else {
                    $$28 = 0;
                }

                int $$38;
                if ($$11.isPresent() && $$17.isPresent() && $$11.getAsInt() - $$23 <= $$17.getAsInt() + $$28) {
                    int $$29 = $$17.getAsInt();
                    int $$30 = $$11.getAsInt();
                    int $$31 = Math.max($$30 - $$23, $$29 + 1);
                    int $$32 = Math.min($$29 + $$28, $$30 - 1);
                    int $$33 = Mth.randomBetweenInclusive(pRandom, $$31, $$32 + 1);
                    int $$34 = $$33 - 1;
                    $$37 = $$30 - $$33;
                    $$38 = $$34 - $$29;
                } else {
                    $$37 = $$23;
                    $$38 = $$28;
                }

                boolean $$39 = pRandom.nextBoolean() && $$37 > 0 && $$38 > 0 && $$16.getHeight().isPresent() && $$37 + $$38 == $$16.getHeight().getAsInt();
                if ($$11.isPresent()) {
                    growPointedHuestone(pLevel, pPos.atY($$11.getAsInt() - 1), Direction.DOWN, $$37, $$39);
                }

                if ($$17.isPresent()) {
                    growPointedHuestone(pLevel, pPos.atY($$17.getAsInt() + 1), Direction.UP, $$38, $$39);
                }

            }
        }
    }

    protected static void growPointedHuestone(LevelAccessor pLevel, BlockPos pPos, Direction pDirection, int pHeight, boolean pMergeTip) {
        if (isHuestoneBase(pLevel.getBlockState(pPos.relative(pDirection.getOpposite())))) {
            BlockPos.MutableBlockPos $$5 = pPos.mutable();
            buildBaseToTipColumn(pDirection, pHeight, pMergeTip, (p_277326_) -> {
                if (p_277326_.is(ModBlocks.POINTED_HUESTONE.get())) {
                    p_277326_ = (BlockState)p_277326_.setValue(PointedHuestoneBlock.WATERLOGGED, pLevel.isWaterAt($$5));
                }

                pLevel.setBlock($$5, p_277326_, 2);
                $$5.move(pDirection);
            });
        }
    }

    protected static void buildBaseToTipColumn(Direction pDirection, int pHeight, boolean pMergeTip, Consumer<BlockState> pBlockSetter) {
        if (pHeight >= 3) {
            pBlockSetter.accept(createPointedHuestone(pDirection, HuestoneThickness.BASE));

            for(int $$4 = 0; $$4 < pHeight - 3; ++$$4) {
                pBlockSetter.accept(createPointedHuestone(pDirection, HuestoneThickness.MIDDLE));
            }
        }

        if (pHeight >= 2) {
            pBlockSetter.accept(createPointedHuestone(pDirection, HuestoneThickness.FRUSTUM));
        }

        if (pHeight >= 1) {
            pBlockSetter.accept(createPointedHuestone(pDirection, pMergeTip ? HuestoneThickness.TIP_MERGE : HuestoneThickness.TIP));
        }

    }

    private static BlockState createPointedHuestone(Direction pDirection, HuestoneThickness pHuestoneThickness) {
        return (BlockState)((BlockState)ModBlocks.POINTED_HUESTONE.get().defaultBlockState().setValue(PointedHuestoneBlock.TIP_DIRECTION, pDirection)).setValue(PointedHuestoneBlock.THICKNESS, pHuestoneThickness);
    }

    public static boolean isHuestoneBase(BlockState pState) {
        return pState.is(ModBlocks.SATURINIUM.get()) || pState.is(BlockTags.DRIPSTONE_REPLACEABLE) || pState.is(ModBlocks.MOSSY_SATURINIUM.get());
    }

    private boolean isLava(LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos).is(Blocks.LAVA);
    }

    private int getHuestoneHeight(RandomSource pRandom, int pX, int pZ, float pChance, int pHeight, HuestoneClusterConfiguration pConfig) {
        if (pRandom.nextFloat() > pChance) {
            return 0;
        } else {
            int $$6 = Math.abs(pX) + Math.abs(pZ);
            float $$7 = (float)Mth.clampedMap((double)$$6, 0.0, (double)pConfig.maxDistanceFromCenterAffectingHeightBias, (double)pHeight / 2.0, 0.0);
            return (int)randomBetweenBiased(pRandom, 0.0F, (float)pHeight, $$7, (float)pConfig.heightDeviation);
        }
    }

    private boolean canPlacePool(WorldGenLevel pLevel, BlockPos pPos) {
        BlockState $$2 = pLevel.getBlockState(pPos);
        if (!$$2.is(Blocks.WATER) && !$$2.is(ModBlocks.SATURINIUM.get()) && !$$2.is(ModBlocks.MOSSY_SATURINIUM.get()) && !$$2.is(ModBlocks.POINTED_HUESTONE.get())) {
            if (pLevel.getBlockState(pPos.above()).getFluidState().is(FluidTags.WATER)) {
                return false;
            } else {
                Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

                Direction $$3;
                do {
                    if (!var4.hasNext()) {
                        return this.canBeAdjacentToWater(pLevel, pPos.below());
                    }

                    $$3 = (Direction)var4.next();
                } while(this.canBeAdjacentToWater(pLevel, pPos.relative($$3)));

                return false;
            }
        } else {
            return false;
        }
    }

    private boolean canBeAdjacentToWater(LevelAccessor pLevel, BlockPos pPos) {
        BlockState $$2 = pLevel.getBlockState(pPos);
        return $$2.is(BlockTags.BASE_STONE_OVERWORLD) || $$2.getFluidState().is(FluidTags.WATER);
    }

    private void replaceBlocksWithHuestoneBlocks(WorldGenLevel pLevel, BlockPos pPos, int pThickness, Direction pDirection) {
        BlockPos.MutableBlockPos $$4 = pPos.mutable();

        for(int $$5 = 0; $$5 < pThickness; ++$$5) {
            if (!placeHuestoneBlockIfPossible(pLevel, $$4)) {
                return;
            }

            $$4.move(pDirection);
        }

    }

    protected static boolean placeHuestoneBlockIfPossible(LevelAccessor pLevel, BlockPos pPos) {
        BlockState $$2 = pLevel.getBlockState(pPos);
        if ($$2.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
            pLevel.setBlock(pPos, ModBlocks.SATURINIUM.get().defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }

    private double getChanceOfStalagmiteOrStalactite(int pXRadius, int pZRadius, int pX, int pZ, HuestoneClusterConfiguration pConfig) {
        int $$5 = pXRadius - Math.abs(pX);
        int $$6 = pZRadius - Math.abs(pZ);
        int $$7 = Math.min($$5, $$6);
        return (double)Mth.clampedMap((float)$$7, 0.0F, (float)pConfig.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, pConfig.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0F);
    }

    private static float randomBetweenBiased(RandomSource pRandom, float pMin, float pMax, float pMean, float pDeviation) {
        return ClampedNormalFloat.sample(pRandom, pMean, pDeviation, pMin, pMax);
    }
}
