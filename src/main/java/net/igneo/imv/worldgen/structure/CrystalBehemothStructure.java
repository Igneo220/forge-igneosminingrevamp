package net.igneo.imv.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import org.jetbrains.annotations.NotNull;
import org.joml.Random;

import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Predicate;

public class CrystalBehemothStructure extends Structure {
    public static final Codec<CrystalBehemothStructure> CODEC = RecordCodecBuilder.<CrystalBehemothStructure>mapCodec(instance ->
            instance.group(CrystalBehemothStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, CrystalBehemothStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public CrystalBehemothStructure(Structure.StructureSettings config,
                                Holder<StructureTemplatePool> startPool,
                                Optional<ResourceLocation> startJigsawName,
                                int size,
                                HeightProvider startHeight,
                                Optional<Heightmap.Types> projectStartToHeightmap,
                                int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }


    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        ChunkPos pos1 = pContext.chunkPos();
        Random random = new Random();
        BlockPos pos2 = new BlockPos(pos1.getMinBlockX(), random.nextInt(250), pos1.getMinBlockZ());
        NoiseColumn blockReader = pContext.chunkGenerator().getBaseColumn(pos2.getX(), pos2.getZ(),pContext.heightAccessor() ,pContext.randomState());

        boolean searching = true;
        int tries = 0;
        int fx = pos2.getX();
        int fz = pos2.getZ();
        while (searching) {
            if (blockReader.getBlock(pos2.getY() + 15).is(BlockTags.REPLACEABLE) && (blockReader.getBlock(pos2.getY() + 30).is(BlockTags.REPLACEABLE) && blockReader.getBlock(pos2.getY() + 4).is(BlockTags.REPLACEABLE) && !blockReader.getBlock(pos2.getY()).is(BlockTags.REPLACEABLE) && blockReader.getBlock(pos2.getY() + 5).is(BlockTags.REPLACEABLE))) {
                boolean blocked = false;
                for (int i = 0; i <= 3; ++i) {
                    int d0 = 0;
                    int d1 = 0;
                    if (i == 0) {
                        d0 = 10;
                        d1 = 0;
                    } else if (i == 1) {
                        d0 = -10;
                        d1 = 0;
                    } else if (i == 2) {
                        d1 = 10;
                        d0 = 0;
                    } else if (i == 3) {
                        d1 = -10;
                        d0 = 0;
                    }
                    NoiseColumn tempblockReader = pContext.chunkGenerator().getBaseColumn(pos2.getX() + d0, pos2.getZ() + d1,pContext.heightAccessor() ,pContext.randomState());
                    if (tempblockReader.getBlock(pos2.getY()).is(BlockTags.REPLACEABLE)) {
                        blocked = true;
                        i = 10;
                    }
                }
                if (!blocked) {
                    searching = false;
                } else {
                    pos2 = new BlockPos(pos2.getX(), pos2.getY() - 1, pos2.getZ());
                }
            } else {
                pos2 = new BlockPos(pos2.getX(), pos2.getY() - 1, pos2.getZ());
            }

            if (pos2.getY() < 40) {
                if (tries > 3) {
                    break;
                } else {
                    System.out.println("TRYING NEW LOCATION");
                    ++tries;
                    int tx = 0;
                    int tz = 0;
                    int neg = 1;
                    if (Math.random() > 0.5) {
                        if (Math.random() > 0.5) {
                            neg = -1;
                        } else {
                            neg = 1;
                        }
                        tx = (15 * neg);
                    }
                    if (Math.random() > 0.5) {
                        if (Math.random() > 0.5) {
                            neg = -1;
                        } else {
                            neg = 1;
                        }
                        tz = (15 * neg);
                    }
                    pos2 = new BlockPos(fx + tx,random.nextInt(250), fz + tz);
                }
            }
        }
        if (searching) {
            System.out.println("PLACE FAILED");
            return Optional.empty();
        }
        System.out.println("generated at: " + pos2);
        return JigsawPlacement.addPieces(pContext, this.startPool, this.startJigsawName, this.size, pos2, false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    public StructureType<?> type() {
        return ModStructures.CRYSTAL_BEHEMOTH.get();
    }
}