package net.igneo.imv.worldgen.structure.placement;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

import java.util.*;

public class SetRotJigsawPlacement {
    static final Logger LOGGER = LogUtils.getLogger();

    public SetRotJigsawPlacement() {
    }

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext pContext, Holder<StructureTemplatePool> pStartPool, Optional<ResourceLocation> pStartJigsawName, int pMaxDepth, BlockPos pPos, boolean pUseExpansionHack, Optional<Heightmap.Types> pProjectStartToHeightmap, int pMaxDistanceFromCenter, Rotation rot) {
        RegistryAccess $$8 = pContext.registryAccess();
        ChunkGenerator $$9 = pContext.chunkGenerator();
        StructureTemplateManager $$10 = pContext.structureTemplateManager();
        LevelHeightAccessor $$11 = pContext.heightAccessor();
        WorldgenRandom $$12 = pContext.random();
        Registry<StructureTemplatePool> $$13 = $$8.registryOrThrow(Registries.TEMPLATE_POOL);
        System.out.println(rot);
        StructureTemplatePool $$15 = (StructureTemplatePool)pStartPool.value();
        StructurePoolElement $$16 = $$15.getRandomTemplate($$12);
        if ($$16 == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos $$20;
            if (pStartJigsawName.isPresent()) {
                ResourceLocation $$17 = (ResourceLocation)pStartJigsawName.get();
                Optional<BlockPos> $$18 = getRandomNamedJigsaw($$16, $$17, pPos, rot, $$10, $$12);
                if ($$18.isEmpty()) {
                    LOGGER.error("No starting jigsaw {} found in start pool {}", $$17, pStartPool.unwrapKey().map((p_248484_) -> {
                        return p_248484_.location().toString();
                    }).orElse("<unregistered>"));
                    return Optional.empty();
                }

                $$20 = (BlockPos)$$18.get();
            } else {
                $$20 = pPos;
            }

            Vec3i $$21 = $$20.subtract(pPos);
            BlockPos $$22 = pPos.subtract($$21);
            PoolElementStructurePiece $$23 = new PoolElementStructurePiece($$10, $$16, $$22, $$16.getGroundLevelDelta(), rot, $$16.getBoundingBox($$10, $$22, rot));
            BoundingBox $$24 = $$23.getBoundingBox();
            int $$25 = ($$24.maxX() + $$24.minX()) / 2;
            int $$26 = ($$24.maxZ() + $$24.minZ()) / 2;
            int $$28;
            if (pProjectStartToHeightmap.isPresent()) {
                $$28 = pPos.getY() + $$9.getFirstFreeHeight($$25, $$26, (Heightmap.Types)pProjectStartToHeightmap.get(), $$11, pContext.randomState());
            } else {
                $$28 = $$22.getY();
            }

            int $$29 = $$24.minY() + $$23.getGroundLevelDelta();
            $$23.move(0, $$28 - $$29, 0);
            int $$30 = $$28 + $$21.getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos($$25, $$30, $$26), (p_227237_) -> {
                List<PoolElementStructurePiece> structurePool = Lists.newArrayList();
                structurePool.add($$23);
                if (pMaxDepth > 0) {
                    AABB bbox = new AABB((double)($$25 - pMaxDistanceFromCenter), (double)($$30 - pMaxDistanceFromCenter), (double)($$26 - pMaxDistanceFromCenter), (double)($$25 + pMaxDistanceFromCenter + 1), (double)($$30 + pMaxDistanceFromCenter + 1), (double)($$26 + pMaxDistanceFromCenter + 1));
                    VoxelShape $$17 = Shapes.join(Shapes.create(bbox), Shapes.create(AABB.of($$24)), BooleanOp.ONLY_FIRST);
                    addPieces(pContext.randomState(), pMaxDepth, pUseExpansionHack, $$9, $$10, $$11, $$12, $$13, $$23, structurePool, $$17);
                    Objects.requireNonNull(p_227237_);
                    structurePool.forEach(p_227237_::addPiece);
                }
            }));
        }
    }

    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement pElement, ResourceLocation pStartJigsawName, BlockPos pPos, Rotation pRotation, StructureTemplateManager pStructureTemplateManager, WorldgenRandom pRandom) {
        List<StructureTemplate.StructureBlockInfo> $$6 = pElement.getShuffledJigsawBlocks(pStructureTemplateManager, pPos, pRotation, pRandom);
        Optional<BlockPos> $$7 = Optional.empty();
        Iterator var8 = $$6.iterator();

        while(var8.hasNext()) {
            StructureTemplate.StructureBlockInfo $$8 = (StructureTemplate.StructureBlockInfo)var8.next();
            ResourceLocation $$9 = ResourceLocation.tryParse($$8.nbt().getString("name"));
            if (pStartJigsawName.equals($$9)) {
                $$7 = Optional.of($$8.pos());
                break;
            }
        }

        return $$7;
    }

    private static void addPieces(RandomState pRandomState, int pMaxDepth, boolean pUseExpansionHack, ChunkGenerator pChunkGenerator, StructureTemplateManager pStructureTemplateManager, LevelHeightAccessor pLevel, RandomSource pRandom, Registry<StructureTemplatePool> pPools, PoolElementStructurePiece p_227219_, List<PoolElementStructurePiece> pPieces, VoxelShape p_227221_) {
        SetRotJigsawPlacement.Placer $$11 = new SetRotJigsawPlacement.Placer(pPools, pMaxDepth, pChunkGenerator, pStructureTemplateManager, pPieces, pRandom);
        $$11.placing.addLast(new SetRotJigsawPlacement.PieceState(p_227219_, new MutableObject(p_227221_), 0));

        while(!$$11.placing.isEmpty()) {
            SetRotJigsawPlacement.PieceState $$12 = (SetRotJigsawPlacement.PieceState)$$11.placing.removeFirst();
            $$11.tryPlacingChildren($$12.piece, $$12.free, $$12.depth, pUseExpansionHack, pLevel, pRandomState);
        }

    }
/*
    public static boolean generateJigsaw(ServerLevel pLevel, Holder<StructureTemplatePool> pStartPool, ResourceLocation pStartJigsawName, int pMaxDepth, BlockPos pPos, boolean pKeepJigsaws) {
        ChunkGenerator $$6 = pLevel.getChunkSource().getGenerator();
        StructureTemplateManager $$7 = pLevel.getStructureManager();
        StructureManager $$8 = pLevel.structureManager();
        RandomSource $$9 = pLevel.getRandom();
        Structure.GenerationContext $$10 = new Structure.GenerationContext(pLevel.registryAccess(), $$6, $$6.getBiomeSource(), pLevel.getChunkSource().randomState(), $$7, pLevel.getSeed(), new ChunkPos(pPos), pLevel, (p_227255_) -> {
            return true;
        });
        Optional<Structure.GenerationStub> $$11 = addPieces($$10, pStartPool, Optional.of(pStartJigsawName), pMaxDepth, pPos, false, Optional.empty(), 128);
        if ($$11.isPresent()) {
            StructurePiecesBuilder $$12 = ((Structure.GenerationStub)$$11.get()).getPiecesBuilder();
            Iterator var13 = $$12.build().pieces().iterator();

            while(var13.hasNext()) {
                StructurePiece $$13 = (StructurePiece)var13.next();
                if ($$13 instanceof PoolElementStructurePiece) {
                    PoolElementStructurePiece $$14 = (PoolElementStructurePiece)$$13;
                    $$14.place(pLevel, $$8, $$6, $$9, BoundingBox.infinite(), pPos, pKeepJigsaws);
                }
            }

            return true;
        } else {
            return false;
        }
    }*/

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource random;
        final Deque<SetRotJigsawPlacement.PieceState> placing = Queues.newArrayDeque();

        Placer(Registry<StructureTemplatePool> pPools, int pMaxDepth, ChunkGenerator pChunkGenerator, StructureTemplateManager pStructureTemplateManager, List<? super PoolElementStructurePiece> pPieces, RandomSource pRandom) {
            this.pools = pPools;
            this.maxDepth = pMaxDepth;
            this.chunkGenerator = pChunkGenerator;
            this.structureTemplateManager = pStructureTemplateManager;
            this.pieces = pPieces;
            this.random = pRandom;
        }

        void tryPlacingChildren(PoolElementStructurePiece pPiece, MutableObject<VoxelShape> pFree, int pDepth, boolean pUseExpansionHack, LevelHeightAccessor pLevel, RandomState pRandomState) {
            StructurePoolElement $$6 = pPiece.getElement();
            BlockPos $$7 = pPiece.getPosition();
            Rotation $$8 = pPiece.getRotation();
            StructureTemplatePool.Projection $$9 = $$6.getProjection();
            boolean $$10 = $$9 == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> $$11 = new MutableObject();
            BoundingBox $$12 = pPiece.getBoundingBox();
            int $$13 = $$12.minY();
            Iterator var15 = $$6.getShuffledJigsawBlocks(this.structureTemplateManager, $$7, $$8, this.random).iterator();

            while(true) {
                label129:
                while(var15.hasNext()) {
                    StructureTemplate.StructureBlockInfo $$14 = (StructureTemplate.StructureBlockInfo)var15.next();
                    Direction $$15 = JigsawBlock.getFrontFacing($$14.state());
                    BlockPos $$16 = $$14.pos();
                    BlockPos $$17 = $$16.relative($$15);
                    int $$18 = $$16.getY() - $$13;
                    int $$19 = -1;
                    ResourceKey<StructureTemplatePool> $$20 = readPoolName($$14);
                    Optional<? extends Holder<StructureTemplatePool>> $$21 = this.pools.getHolder($$20);
                    if ($$21.isEmpty()) {
                        SetRotJigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", $$20.location());
                    } else {
                        Holder<StructureTemplatePool> $$22 = (Holder)$$21.get();
                        if (((StructureTemplatePool)$$22.value()).size() == 0 && !$$22.is(Pools.EMPTY)) {
                            SetRotJigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", $$20.location());
                        } else {
                            Holder<StructureTemplatePool> $$23 = ((StructureTemplatePool)$$22.value()).getFallback();
                            if (((StructureTemplatePool)$$23.value()).size() == 0 && !$$23.is(Pools.EMPTY)) {
                                SetRotJigsawPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", $$23.unwrapKey().map((p_255599_) -> {
                                    return p_255599_.location().toString();
                                }).orElse("<unregistered>"));
                            } else {
                                boolean $$24 = $$12.isInside($$17);
                                MutableObject $$26;
                                if ($$24) {
                                    $$26 = $$11;
                                    if ($$11.getValue() == null) {
                                        $$11.setValue(Shapes.create(AABB.of($$12)));
                                    }
                                } else {
                                    $$26 = pFree;
                                }

                                List<StructurePoolElement> $$27 = Lists.newArrayList();
                                if (pDepth != this.maxDepth) {
                                    $$27.addAll(((StructureTemplatePool)$$22.value()).getShuffledTemplates(this.random));
                                }

                                $$27.addAll(((StructureTemplatePool)$$23.value()).getShuffledTemplates(this.random));
                                Iterator var29 = $$27.iterator();

                                while(var29.hasNext()) {
                                    StructurePoolElement $$28 = (StructurePoolElement)var29.next();
                                    if ($$28 == EmptyPoolElement.INSTANCE) {
                                        break;
                                    }

                                    Iterator var31 = Rotation.getShuffled(this.random).iterator();

                                    label125:
                                    while(var31.hasNext()) {
                                        Rotation $$29 = (Rotation)var31.next();
                                        List<StructureTemplate.StructureBlockInfo> $$30 = $$28.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, $$29, this.random);
                                        BoundingBox $$31 = $$28.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, $$29);
                                        int $$33;
                                        if (pUseExpansionHack && $$31.getYSpan() <= 16) {
                                            $$33 = $$30.stream().mapToInt((p_255598_) -> {
                                                if (!$$31.isInside(p_255598_.pos().relative(JigsawBlock.getFrontFacing(p_255598_.state())))) {
                                                    return 0;
                                                } else {
                                                    ResourceKey<StructureTemplatePool> $$2 = readPoolName(p_255598_);
                                                    Optional<? extends Holder<StructureTemplatePool>> $$3 = this.pools.getHolder($$2);
                                                    Optional<Holder<StructureTemplatePool>> $$4 = $$3.map((p_255600_) -> {
                                                        return ((StructureTemplatePool)p_255600_.value()).getFallback();
                                                    });
                                                    int $$5 = (Integer)$$3.map((p_255596_) -> {
                                                        return ((StructureTemplatePool)p_255596_.value()).getMaxSize(this.structureTemplateManager);
                                                    }).orElse(0);
                                                    int fmap = (Integer)$$4.map((p_255601_) -> {
                                                        return ((StructureTemplatePool)p_255601_.value()).getMaxSize(this.structureTemplateManager);
                                                    }).orElse(0);
                                                    return Math.max($$5, fmap);
                                                }
                                            }).max().orElse(0);
                                        } else {
                                            $$33 = 0;
                                        }

                                        Iterator var36 = $$30.iterator();

                                        StructureTemplatePool.Projection $$39;
                                        boolean $$40;
                                        int $$41;
                                        int $$42;
                                        int $$44;
                                        BoundingBox $$46;
                                        BlockPos $$47;
                                        int $$49;
                                        do {
                                            StructureTemplate.StructureBlockInfo $$34;
                                            do {
                                                if (!var36.hasNext()) {
                                                    continue label125;
                                                }

                                                $$34 = (StructureTemplate.StructureBlockInfo)var36.next();
                                            } while(!JigsawBlock.canAttach($$14, $$34));

                                            BlockPos $$35 = $$34.pos();
                                            BlockPos $$36 = $$17.subtract($$35);
                                            BoundingBox $$37 = $$28.getBoundingBox(this.structureTemplateManager, $$36, $$29);
                                            int $$38 = $$37.minY();
                                            $$39 = $$28.getProjection();
                                            $$40 = $$39 == StructureTemplatePool.Projection.RIGID;
                                            $$41 = $$35.getY();
                                            $$42 = $$18 - $$41 + JigsawBlock.getFrontFacing($$14.state()).getStepY();
                                            if ($$10 && $$40) {
                                                $$44 = $$13 + $$42;
                                            } else {
                                                if ($$19 == -1) {
                                                    $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Heightmap.Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                                }

                                                $$44 = $$19 - $$41;
                                            }

                                            int $$45 = $$44 - $$38;
                                            $$46 = $$37.moved(0, $$45, 0);
                                            $$47 = $$36.offset(0, $$45, 0);
                                            if ($$33 > 0) {
                                                $$49 = Math.max($$33 + 1, $$46.maxY() - $$46.minY());
                                                $$46.encapsulate(new BlockPos($$46.minX(), $$46.minY() + $$49, $$46.minZ()));
                                            }
                                        } while(Shapes.joinIsNotEmpty((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46).deflate(0.25)), BooleanOp.ONLY_SECOND));

                                        $$26.setValue(Shapes.joinUnoptimized((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46)), BooleanOp.ONLY_FIRST));
                                        $$49 = pPiece.getGroundLevelDelta();
                                        int $$51;
                                        if ($$40) {
                                            $$51 = $$49 - $$42;
                                        } else {
                                            $$51 = $$28.getGroundLevelDelta();
                                        }

                                        PoolElementStructurePiece $$52 = new PoolElementStructurePiece(this.structureTemplateManager, $$28, $$47, $$51, $$29, $$46);
                                        int $$55;
                                        if ($$10) {
                                            $$55 = $$13 + $$18;
                                        } else if ($$40) {
                                            $$55 = $$44 + $$41;
                                        } else {
                                            if ($$19 == -1) {
                                                $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Heightmap.Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                            }

                                            $$55 = $$19 + $$42 / 2;
                                        }

                                        pPiece.addJunction(new JigsawJunction($$17.getX(), $$55 - $$18 + $$49, $$17.getZ(), $$42, $$39));
                                        $$52.addJunction(new JigsawJunction($$16.getX(), $$55 - $$41 + $$51, $$16.getZ(), -$$42, $$9));
                                        this.pieces.add($$52);
                                        if (pDepth + 1 <= this.maxDepth) {
                                            this.placing.addLast(new SetRotJigsawPlacement.PieceState($$52, $$26, pDepth + 1));
                                        }
                                        continue label129;
                                    }
                                }
                            }
                        }
                    }
                }

                return;
            }
        }

        private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo pStructureBlockInfo) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(pStructureBlockInfo.nbt().getString("pool")));
        }
    }

    static final class PieceState {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece pPiece, MutableObject<VoxelShape> pFree, int pDepth) {
            this.piece = pPiece;
            this.free = pFree;
            this.depth = pDepth;
        }
    }
}
