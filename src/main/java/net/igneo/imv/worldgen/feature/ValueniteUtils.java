package net.igneo.imv.worldgen.feature;

import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.block.custom.HuestoneThickness;
import net.igneo.imv.block.custom.PointedHuestoneBlock;
import net.igneo.imv.block.custom.PointedValueniteBlock;
import net.igneo.imv.block.custom.ValueniteThickness;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class ValueniteUtils {
    public ValueniteUtils() {
    }

    protected static double getValueniteHeight(double pRadius, double pMaxRadius, double pScale, double pMinRadius) {
        if (pRadius < pMinRadius) {
            pRadius = pMinRadius;
        }

        double $$4 = 0.384;
        double $$5 = pRadius / pMaxRadius * 0.384;
        double $$6 = 0.75 * Math.pow($$5, 1.3333333333333333);
        double $$7 = Math.pow($$5, 0.6666666666666666);
        double $$8 = 0.3333333333333333 * Math.log($$5);
        double $$9 = pScale * ($$6 - $$7 - $$8);
        $$9 = Math.max($$9, 0.0);
        return $$9 / 0.384 * pMaxRadius;
    }

    protected static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel pLevel, BlockPos pPos, int pRadius) {
        if (isEmptyOrWaterOrLava(pLevel, pPos)) {
            return false;
        } else {
            float $$3 = 6.0F;
            float $$4 = 6.0F / (float)pRadius;

            for(float $$5 = 0.0F; $$5 < 6.2831855F; $$5 += $$4) {
                int $$6 = (int)(Mth.cos($$5) * (float)pRadius);
                int $$7 = (int)(Mth.sin($$5) * (float)pRadius);
                if (isEmptyOrWaterOrLava(pLevel, pPos.offset($$6, 0, $$7))) {
                    return false;
                }
            }

            return true;
        }
    }

    protected static boolean isEmptyOrWater(LevelAccessor pLevel, BlockPos pPos) {
        return pLevel.isStateAtPosition(pPos, ValueniteUtils::isEmptyOrWater);
    }

    protected static boolean isEmptyOrWaterOrLava(LevelAccessor pLevel, BlockPos pPos) {
        return pLevel.isStateAtPosition(pPos, ValueniteUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(Direction pDirection, int pHeight, boolean pMergeTip, Consumer<BlockState> pBlockSetter) {
        if (pHeight >= 3) {
            pBlockSetter.accept(createPointedValuenite(pDirection, ValueniteThickness.BASE));

            for(int $$4 = 0; $$4 < pHeight - 3; ++$$4) {
                pBlockSetter.accept(createPointedValuenite(pDirection, ValueniteThickness.MIDDLE));
            }
        }

        if (pHeight >= 2) {
            pBlockSetter.accept(createPointedValuenite(pDirection, ValueniteThickness.FRUSTUM));
        }

        if (pHeight >= 1) {
            pBlockSetter.accept(createPointedValuenite(pDirection, pMergeTip ? ValueniteThickness.TIP_MERGE : ValueniteThickness.TIP));
        }

    }

    protected static void growPointedValuenite(LevelAccessor pLevel, BlockPos pPos, Direction pDirection, int pHeight, boolean pMergeTip) {
        if (isValueniteBase(pLevel.getBlockState(pPos.relative(pDirection.getOpposite())))) {
            BlockPos.MutableBlockPos $$5 = pPos.mutable();
            buildBaseToTipColumn(pDirection, pHeight, pMergeTip, (p_277326_) -> {
                if (p_277326_.is(ModBlocks.POINTED_VALUENITE.get())) {
                    p_277326_ = (BlockState)p_277326_.setValue(PointedValueniteBlock.WATERLOGGED, pLevel.isWaterAt($$5));
                }

                pLevel.setBlock($$5, p_277326_, 2);
                $$5.move(pDirection);
            });
        }
    }

    protected static boolean placeValueniteBlockIfPossible(LevelAccessor pLevel, BlockPos pPos) {
        BlockState $$2 = pLevel.getBlockState(pPos);
        if ($$2.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
            pLevel.setBlock(pPos, ModBlocks.POINTED_VALUENITE.get().defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState createPointedValuenite(Direction pDirection, ValueniteThickness pValueniteThickness) {
        return (BlockState)((BlockState)ModBlocks.POINTED_VALUENITE.get().defaultBlockState().setValue(PointedValueniteBlock.TIP_DIRECTION, pDirection)).setValue(PointedValueniteBlock.THICKNESS, pValueniteThickness);
    }

    public static boolean isValueniteBaseOrLava(BlockState pState) {
        return isValueniteBase(pState) || pState.is(Blocks.LAVA);
    }

    public static boolean isValueniteBase(BlockState pState) {
        return pState.is(ModBlocks.POINTED_VALUENITE.get()) || pState.is(BlockTags.DRIPSTONE_REPLACEABLE);
    }

    public static boolean isEmptyOrWater(BlockState p_159665_) {
        return p_159665_.isAir() || p_159665_.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState pState) {
        return !pState.isAir() && !pState.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState p_159667_) {
        return p_159667_.isAir() || p_159667_.is(Blocks.WATER) || p_159667_.is(Blocks.LAVA);
    }
}
