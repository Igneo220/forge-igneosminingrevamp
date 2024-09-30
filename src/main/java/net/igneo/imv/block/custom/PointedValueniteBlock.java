package net.igneo.imv.block.custom;

import com.google.common.annotations.VisibleForTesting;
import net.igneo.imv.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PointedValueniteBlock extends Block implements Fallable, SimpleWaterloggedBlock {
    public static final DirectionProperty TIP_DIRECTION;
    public static final EnumProperty<ValueniteThickness> THICKNESS;
    public static final BooleanProperty WATERLOGGED;
    private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_HUE_TYPE = 11;
    private static final int DELAY_BEFORE_FALLING = 2;
    private static final float HUE_PROBABILITY_PER_ANIMATE_TICK = 0.02F;
    private static final float HUE_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12F;
    private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
    private static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
    private static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
    private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_VALUENITE = 0.6;
    private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
    private static final int STALACTITE_MAX_DAMAGE = 40;
    private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
    private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.0F;
    private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
    private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
    private static final int MAX_GROWTH_LENGTH = 7;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
    private static final float STALACTITE_HUE_START_PIXEL = 0.6875F;
    private static final VoxelShape TIP_MERGE_SHAPE;
    private static final VoxelShape TIP_SHAPE_UP;
    private static final VoxelShape TIP_SHAPE_DOWN;
    private static final VoxelShape FRUSTUM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BASE_SHAPE;
    private static final float MAX_HORIZONTAL_OFFSET = 0.125F;
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK;

    public PointedValueniteBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, ValueniteThickness.TIP)).setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{TIP_DIRECTION, THICKNESS, WATERLOGGED});
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return isValidPointedValuenitePlacement(pLevel, pPos, (Direction)pState.getValue(TIP_DIRECTION));
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if ((Boolean)pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        if (pDirection != Direction.UP && pDirection != Direction.DOWN) {
            return pState;
        } else {
            Direction $$6 = (Direction)pState.getValue(TIP_DIRECTION);
            if ($$6 == Direction.DOWN && pLevel.getBlockTicks().hasScheduledTick(pPos, this)) {
                return pState;
            } else if (pDirection == $$6.getOpposite() && !this.canSurvive(pState, pLevel, pPos)) {
                if ($$6 == Direction.DOWN) {
                    pLevel.scheduleTick(pPos, this, 2);
                } else {
                    pLevel.scheduleTick(pPos, this, 1);
                }

                return pState;
            } else {
                boolean $$7 = pState.getValue(THICKNESS) == ValueniteThickness.TIP_MERGE;
                ValueniteThickness $$8 = calculateValueniteThickness(pLevel, pPos, $$6, $$7);
                return (BlockState)pState.setValue(THICKNESS, $$8);
            }
        }
    }

    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        BlockPos $$4 = pHit.getBlockPos();
        if (!pLevel.isClientSide && pProjectile.mayInteract(pLevel, $$4) && pProjectile instanceof ThrownTrident && pProjectile.getDeltaMovement().length() > 0.6) {
            pLevel.destroyBlock($$4, true);
        }

    }

    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        if (pState.getValue(TIP_DIRECTION) == Direction.UP && pState.getValue(THICKNESS) == ValueniteThickness.TIP) {
            pEntity.causeFallDamage(pFallDistance + 2.0F, 2.0F, pLevel.damageSources().stalagmite());
        } else {
            super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
        }

    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (canDrip(pState)) {
            float $$4 = pRandom.nextFloat();
            if (!($$4 > 0.12F)) {
                getFluidAboveStalactite(pLevel, pPos, pState).filter((p_221848_) -> {
                    return $$4 < 0.02F || canFillCauldron(p_221848_.fluid);
                }).ifPresent((p_221881_) -> {
                    spawnDripParticle(pLevel, pPos, pState, p_221881_.fluid);
                });
            }
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (isStalagmite(pState) && !this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, true);
        } else {
            spawnFallingStalactite(pState, pLevel, pPos);
        }

    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        maybeTransferFluid(pState, pLevel, pPos, pRandom.nextFloat());
        if (pRandom.nextFloat() < 0.011377778F && isStalactiteStartPos(pState, pLevel, pPos)) {
            growStalactiteOrStalagmiteIfPossible(pState, pLevel, pPos, pRandom);
        }

    }

    @VisibleForTesting
    public static void maybeTransferFluid(BlockState pState, ServerLevel pLevel, BlockPos pPos, float pRandChance) {
        if (!(pRandChance > 0.17578125F) || !(pRandChance > 0.05859375F)) {
            if (isStalactiteStartPos(pState, pLevel, pPos)) {
                Optional<PointedValueniteBlock.FluidInfo> $$4 = getFluidAboveStalactite(pLevel, pPos, pState);
                if (!$$4.isEmpty()) {
                    Fluid $$5 = ((PointedValueniteBlock.FluidInfo)$$4.get()).fluid;
                    float $$8;
                    if ($$5 == Fluids.WATER) {
                        $$8 = 0.17578125F;
                    } else {
                        if ($$5 != Fluids.LAVA) {
                            return;
                        }

                        $$8 = 0.05859375F;
                    }

                    if (!(pRandChance >= $$8)) {
                        BlockPos $$9 = findTip(pState, pLevel, pPos, 11, false);
                        if ($$9 != null) {
                            if (((PointedValueniteBlock.FluidInfo)$$4.get()).sourceState.is(Blocks.MUD) && $$5 == Fluids.WATER) {
                                BlockState $$10 = Blocks.CLAY.defaultBlockState();
                                pLevel.setBlockAndUpdate(((PointedValueniteBlock.FluidInfo)$$4.get()).pos, $$10);
                                Block.pushEntitiesUp(((PointedValueniteBlock.FluidInfo)$$4.get()).sourceState, $$10, pLevel, ((PointedValueniteBlock.FluidInfo)$$4.get()).pos);
                                pLevel.gameEvent(GameEvent.BLOCK_CHANGE, ((PointedValueniteBlock.FluidInfo)$$4.get()).pos, GameEvent.Context.of($$10));
                                pLevel.levelEvent(1504, $$9, 0);
                            } else {
                                BlockPos $$11 = findFillableCauldronBelowStalactiteTip(pLevel, $$9, $$5);
                                if ($$11 != null) {
                                    pLevel.levelEvent(1504, $$9, 0);
                                    int $$12 = $$9.getY() - $$11.getY();
                                    int $$13 = 50 + $$12;
                                    BlockState $$14 = pLevel.getBlockState($$11);
                                    pLevel.scheduleTick($$11, $$14.getBlock(), $$13);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor $$1 = pContext.getLevel();
        BlockPos $$2 = pContext.getClickedPos();
        Direction $$3 = pContext.getNearestLookingVerticalDirection().getOpposite();
        Direction $$4 = calculateTipDirection($$1, $$2, $$3);
        if ($$4 == null) {
            return null;
        } else {
            boolean $$5 = !pContext.isSecondaryUseActive();
            ValueniteThickness $$6 = calculateValueniteThickness($$1, $$2, $$4, $$5);
            return $$6 == null ? null : (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, $$4)).setValue(THICKNESS, $$6)).setValue(WATERLOGGED, $$1.getFluidState($$2).getType() == Fluids.WATER);
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return (Boolean)pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        ValueniteThickness $$4 = (ValueniteThickness)pState.getValue(THICKNESS);
        VoxelShape $$10;
        if ($$4 == ValueniteThickness.TIP_MERGE) {
            $$10 = TIP_MERGE_SHAPE;
        } else if ($$4 == ValueniteThickness.TIP) {
            if (pState.getValue(TIP_DIRECTION) == Direction.DOWN) {
                $$10 = TIP_SHAPE_DOWN;
            } else {
                $$10 = TIP_SHAPE_UP;
            }
        } else if ($$4 == ValueniteThickness.FRUSTUM) {
            $$10 = FRUSTUM_SHAPE;
        } else if ($$4 == ValueniteThickness.MIDDLE) {
            $$10 = MIDDLE_SHAPE;
        } else {
            $$10 = BASE_SHAPE;
        }

        Vec3 $$11 = pState.getOffset(pLevel, pPos);
        return $$10.move($$11.x, 0.0, $$11.z);
    }

    public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }

    public float getMaxHorizontalOffset() {
        return 0.125F;
    }

    public void onBrokenAfterFall(Level pLevel, BlockPos pPos, FallingBlockEntity pFallingBlock) {
        if (!pFallingBlock.isSilent()) {
            pLevel.levelEvent(1045, pPos, 0);
        }

    }

    public DamageSource getFallDamageSource(Entity pEntity) {
        return pEntity.damageSources().fallingStalactite(pEntity);
    }

    private static void spawnFallingStalactite(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        BlockPos.MutableBlockPos $$3 = pPos.mutable();

        for(BlockState $$4 = pState; isStalactite($$4); $$4 = pLevel.getBlockState($$3)) {
            FallingBlockEntity $$5 = FallingBlockEntity.fall(pLevel, $$3, $$4);
            if (isTip($$4, true)) {
                int $$6 = Math.max(1 + pPos.getY() - $$3.getY(), 6);
                float $$7 = 1.0F * (float)$$6;
                $$5.setHurtsEntities($$7, 40);
                break;
            }

            $$3.move(Direction.DOWN);
        }

    }

    @VisibleForTesting
    public static void growStalactiteOrStalagmiteIfPossible(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockState $$4 = pLevel.getBlockState(pPos.above(1));
        BlockState $$5 = pLevel.getBlockState(pPos.above(2));
        if (canGrow($$4, $$5)) {
            BlockPos $$6 = findTip(pState, pLevel, pPos, 7, false);
            if ($$6 != null) {
                BlockState $$7 = pLevel.getBlockState($$6);
                if (canDrip($$7) && canTipGrow($$7, pLevel, $$6)) {
                    if (pRandom.nextBoolean()) {
                        grow(pLevel, $$6, Direction.DOWN);
                    } else {
                        growStalagmiteBelow(pLevel, $$6);
                    }

                }
            }
        }
    }

    private static void growStalagmiteBelow(ServerLevel pLevel, BlockPos pPos) {
        BlockPos.MutableBlockPos $$2 = pPos.mutable();

        for(int $$3 = 0; $$3 < 10; ++$$3) {
            $$2.move(Direction.DOWN);
            BlockState $$4 = pLevel.getBlockState($$2);
            if (!$$4.getFluidState().isEmpty()) {
                return;
            }

            if (isUnmergedTipWithDirection($$4, Direction.UP) && canTipGrow($$4, pLevel, $$2)) {
                grow(pLevel, $$2, Direction.UP);
                return;
            }

            if (isValidPointedValuenitePlacement(pLevel, $$2, Direction.UP) && !pLevel.isWaterAt($$2.below())) {
                grow(pLevel, $$2.below(), Direction.UP);
                return;
            }

            if (!canDripThrough(pLevel, $$2, $$4)) {
                return;
            }
        }

    }

    private static void grow(ServerLevel pServer, BlockPos pPos, Direction pDirection) {
        BlockPos $$3 = pPos.relative(pDirection);
        BlockState $$4 = pServer.getBlockState($$3);
        if (isUnmergedTipWithDirection($$4, pDirection.getOpposite())) {
            createMergedTips($$4, pServer, $$3);
        } else if ($$4.isAir() || $$4.is(Blocks.WATER)) {
            createValuenite(pServer, $$3, pDirection, ValueniteThickness.TIP);
        }

    }

    private static void createValuenite(LevelAccessor pLevel, BlockPos pPos, Direction pDirection, ValueniteThickness pThickness) {
        BlockState $$4 = (BlockState)((BlockState)((BlockState) ModBlocks.POINTED_VALUENITE.get().defaultBlockState().setValue(TIP_DIRECTION, pDirection)).setValue(THICKNESS, pThickness)).setValue(WATERLOGGED, pLevel.getFluidState(pPos).getType() == Fluids.WATER);
        pLevel.setBlock(pPos, $$4, 3);
    }

    private static void createMergedTips(BlockState pState, LevelAccessor pLevel, BlockPos pPos) {
        BlockPos $$5;
        BlockPos $$6;
        if (pState.getValue(TIP_DIRECTION) == Direction.UP) {
            $$6 = pPos;
            $$5 = pPos.above();
        } else {
            $$5 = pPos;
            $$6 = pPos.below();
        }

        createValuenite(pLevel, $$5, Direction.DOWN, ValueniteThickness.TIP_MERGE);
        createValuenite(pLevel, $$6, Direction.UP, ValueniteThickness.TIP_MERGE);
    }

    public static void spawnDripParticle(Level pLevel, BlockPos pPos, BlockState pState) {
        getFluidAboveStalactite(pLevel, pPos, pState).ifPresent((p_221856_) -> {
            spawnDripParticle(pLevel, pPos, pState, p_221856_.fluid);
        });
    }

    private static void spawnDripParticle(Level pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        Vec3 $$4 = pState.getOffset(pLevel, pPos);
        double $$5 = 0.0625;
        double $$6 = (double)pPos.getX() + 0.5 + $$4.x;
        double $$7 = (double)((float)(pPos.getY() + 1) - 0.6875F) - 0.0625;
        double $$8 = (double)pPos.getZ() + 0.5 + $$4.z;
        Fluid $$9 = getDripFluid(pLevel, pFluid);
        ParticleOptions $$10 = $$9.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
        pLevel.addParticle($$10, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Nullable
    private static BlockPos findTip(BlockState pState, LevelAccessor pLevel, BlockPos pPos, int pMaxIterations, boolean pIsTipMerge) {
        if (isTip(pState, pIsTipMerge)) {
            return pPos;
        } else {
            Direction $$5 = (Direction)pState.getValue(TIP_DIRECTION);
            BiPredicate<BlockPos, BlockState> $$6 = (p_202023_, p_202024_) -> {
                return p_202024_.is(ModBlocks.POINTED_VALUENITE.get()) && p_202024_.getValue(TIP_DIRECTION) == $$5;
            };
            return (BlockPos)findBlockVertical(pLevel, pPos, $$5.getAxisDirection(), $$6, (p_154168_) -> {
                return isTip(p_154168_, pIsTipMerge);
            }, pMaxIterations).orElse((BlockPos) null);
        }
    }

    @Nullable
    private static Direction calculateTipDirection(LevelReader pLevel, BlockPos pPos, Direction pDir) {
        Direction $$5;
        if (isValidPointedValuenitePlacement(pLevel, pPos, pDir)) {
            $$5 = pDir;
        } else {
            if (!isValidPointedValuenitePlacement(pLevel, pPos, pDir.getOpposite())) {
                return null;
            }

            $$5 = pDir.getOpposite();
        }

        return $$5;
    }

    private static ValueniteThickness calculateValueniteThickness(LevelReader pLevel, BlockPos pPos, Direction pDir, boolean pIsTipMerge) {
        Direction $$4 = pDir.getOpposite();
        BlockState $$5 = pLevel.getBlockState(pPos.relative(pDir));
        if (isPointedValueniteWithDirection($$5, $$4)) {
            return !pIsTipMerge && $$5.getValue(THICKNESS) != ValueniteThickness.TIP_MERGE ? ValueniteThickness.TIP : ValueniteThickness.TIP_MERGE;
        } else if (!isPointedValueniteWithDirection($$5, pDir)) {
            return ValueniteThickness.TIP;
        } else {
            ValueniteThickness $$6 = (ValueniteThickness)$$5.getValue(THICKNESS);
            if ($$6 != ValueniteThickness.TIP && $$6 != ValueniteThickness.TIP_MERGE) {
                BlockState $$7 = pLevel.getBlockState(pPos.relative($$4));
                return !isPointedValueniteWithDirection($$7, pDir) ? ValueniteThickness.BASE : ValueniteThickness.MIDDLE;
            } else {
                return ValueniteThickness.FRUSTUM;
            }
        }
    }

    public static boolean canDrip(BlockState p_154239_) {
        return isStalactite(p_154239_) && p_154239_.getValue(THICKNESS) == ValueniteThickness.TIP && !(Boolean)p_154239_.getValue(WATERLOGGED);
    }

    private static boolean canTipGrow(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        Direction $$3 = (Direction)pState.getValue(TIP_DIRECTION);
        BlockPos $$4 = pPos.relative($$3);
        BlockState $$5 = pLevel.getBlockState($$4);
        if (!$$5.getFluidState().isEmpty()) {
            return false;
        } else {
            return $$5.isAir() ? true : isUnmergedTipWithDirection($$5, $$3.getOpposite());
        }
    }

    private static Optional<BlockPos> findRootBlock(Level pLevel, BlockPos pPos, BlockState pState, int pMaxIterations) {
        Direction $$4 = (Direction)pState.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> $$5 = (p_202015_, p_202016_) -> {
            return p_202016_.is(ModBlocks.POINTED_VALUENITE.get()) && p_202016_.getValue(TIP_DIRECTION) == $$4;
        };
        return findBlockVertical(pLevel, pPos, $$4.getOpposite().getAxisDirection(), $$5, (p_154245_) -> {
            return !p_154245_.is(ModBlocks.POINTED_VALUENITE.get());
        }, pMaxIterations);
    }

    private static boolean isValidPointedValuenitePlacement(LevelReader pLevel, BlockPos pPos, Direction pDir) {
        BlockPos $$3 = pPos.relative(pDir.getOpposite());
        BlockState $$4 = pLevel.getBlockState($$3);
        return $$4.isFaceSturdy(pLevel, $$3, pDir) || isPointedValueniteWithDirection($$4, pDir);
    }

    private static boolean isTip(BlockState pState, boolean pIsTipMerge) {
        if (!pState.is(ModBlocks.POINTED_VALUENITE.get())) {
            return false;
        } else {
            ValueniteThickness $$2 = (ValueniteThickness)pState.getValue(THICKNESS);
            return $$2 == ValueniteThickness.TIP || pIsTipMerge && $$2 == ValueniteThickness.TIP_MERGE;
        }
    }

    private static boolean isUnmergedTipWithDirection(BlockState pState, Direction pDir) {
        return isTip(pState, false) && pState.getValue(TIP_DIRECTION) == pDir;
    }

    private static boolean isStalactite(BlockState pState) {
        return isPointedValueniteWithDirection(pState, Direction.DOWN);
    }

    private static boolean isStalagmite(BlockState pState) {
        return isPointedValueniteWithDirection(pState, Direction.UP);
    }

    private static boolean isStalactiteStartPos(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return isStalactite(pState) && !pLevel.getBlockState(pPos.above()).is(ModBlocks.POINTED_VALUENITE.get());
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    private static boolean isPointedValueniteWithDirection(BlockState pState, Direction pDir) {
        return pState.is(ModBlocks.POINTED_VALUENITE.get()) && pState.getValue(TIP_DIRECTION) == pDir;
    }

    @Nullable
    private static BlockPos findFillableCauldronBelowStalactiteTip(Level pLevel, BlockPos pPos, Fluid pFluid) {
        return null;
    }

    @Nullable
    public static BlockPos findStalactiteTipAboveCauldron(Level pLevel, BlockPos pPos) {
        BiPredicate<BlockPos, BlockState> $$2 = (p_202030_, p_202031_) -> {
            return canDripThrough(pLevel, p_202030_, p_202031_);
        };
        return (BlockPos)findBlockVertical(pLevel, pPos, Direction.UP.getAxisDirection(), $$2, PointedValueniteBlock::canDrip, 11).orElse((BlockPos) null);
    }

    public static Fluid getCauldronFillFluidType(ServerLevel pLevel, BlockPos pPos) {
        return (Fluid)getFluidAboveStalactite(pLevel, pPos, pLevel.getBlockState(pPos)).map((p_221858_) -> {
            return p_221858_.fluid;
        }).filter(PointedValueniteBlock::canFillCauldron).orElse(Fluids.EMPTY);
    }

    private static Optional<PointedValueniteBlock.FluidInfo> getFluidAboveStalactite(Level pLevel, BlockPos pPos, BlockState pState) {
        return !isStalactite(pState) ? Optional.empty() : findRootBlock(pLevel, pPos, pState, 11).map((p_221876_) -> {
            BlockPos $$2 = p_221876_.above();
            BlockState $$3 = pLevel.getBlockState($$2);
            Object $$5;
            if ($$3.is(Blocks.MUD) && !pLevel.dimensionType().ultraWarm()) {
                $$5 = Fluids.WATER;
            } else {
                $$5 = pLevel.getFluidState($$2).getType();
            }

            return new PointedValueniteBlock.FluidInfo($$2, (Fluid)$$5, $$3);
        });
    }

    private static boolean canFillCauldron(Fluid p_154159_) {
        return p_154159_ == Fluids.LAVA || p_154159_ == Fluids.WATER;
    }

    private static boolean canGrow(BlockState pValueniteState, BlockState pState) {
        return pValueniteState.is(ModBlocks.POINTED_VALUENITE.get()) && pState.is(Blocks.WATER) && pState.getFluidState().isSource();
    }

    private static Fluid getDripFluid(Level pLevel, Fluid pFluid) {
        if (pFluid.isSame(Fluids.EMPTY)) {
            return pLevel.dimensionType().ultraWarm() ? Fluids.LAVA : Fluids.WATER;
        } else {
            return pFluid;
        }
    }

    private static Optional<BlockPos> findBlockVertical(LevelAccessor pLevel, BlockPos pPos, Direction.AxisDirection pAxis, BiPredicate<BlockPos, BlockState> pPositionalStatePredicate, Predicate<BlockState> pStatePredicate, int pMaxIterations) {
        Direction $$6 = Direction.get(pAxis, Direction.Axis.Y);
        BlockPos.MutableBlockPos $$7 = pPos.mutable();

        for(int $$8 = 1; $$8 < pMaxIterations; ++$$8) {
            $$7.move($$6);
            BlockState $$9 = pLevel.getBlockState($$7);
            if (pStatePredicate.test($$9)) {
                return Optional.of($$7.immutable());
            }

            if (pLevel.isOutsideBuildHeight($$7.getY()) || !pPositionalStatePredicate.test($$7, $$9)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static boolean canDripThrough(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        if (pState.isAir()) {
            return true;
        } else if (pState.isSolidRender(pLevel, pPos)) {
            return false;
        } else if (!pState.getFluidState().isEmpty()) {
            return false;
        } else {
            VoxelShape $$3 = pState.getCollisionShape(pLevel, pPos);
            return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, $$3, BooleanOp.AND);
        }
    }

    static {
        TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
        THICKNESS = ModBlockStateProperties.VALUENITE_THICKNESS;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        TIP_MERGE_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
        TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
        TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
        FRUSTUM_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
        MIDDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
        REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    static record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
        FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
            this.pos = pos;
            this.fluid = fluid;
            this.sourceState = sourceState;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public Fluid fluid() {
            return this.fluid;
        }

        public BlockState sourceState() {
            return this.sourceState;
        }
    }
}
