package net.igneo.imv.block.custom;

import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.item.ModItems;
import net.igneo.imv.networking.ModMessages;
import net.igneo.imv.networking.packet.ScreenshakeS2CPacket;
import net.igneo.imv.worldgen.dimension.ModDimensions;
import net.igneo.imv.worldgen.portal.ModTeleporter;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.level.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ticket.ChunkTicketManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.simple.SimpleChannel;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.network.screenshake.PositionedScreenshakePacket;
import team.lodestar.lodestone.network.screenshake.ScreenshakePacket;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.Random;

import static net.minecraft.server.commands.PlaceCommand.placeJigsaw;

public class ModPortalBlock extends Block {
    public ModPortalBlock(Properties pProperties) {
        super(pProperties);

    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ResourceKey<Level> resourcekey = pPlayer.level().dimension() == ModDimensions.IGNEODIM_LEVEL_KEY ?
                Level.OVERWORLD : ModDimensions.IGNEODIM_LEVEL_KEY;
        if (pPlayer.canChangeDimensions()) {
            if (resourcekey != ModDimensions.IGNEODIM_LEVEL_KEY) {
                if (pPlayer.getMainHandItem().is(ModItems.CRYSTAL_HEART.get())) {
                    pPlayer.getMainHandItem().setCount(pPlayer.getMainHandItem().getCount() - 1);
                    for (Player player : pLevel.players()){
                        float f = (float) (player.getBlockX() - pPos.getX());
                        float f1 = (float) (player.getBlockY() - pPos.getY());
                        float f2 = (float) (player.getBlockZ() - pPos.getZ());
                        float dist = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                        if (dist < 10) {
                            handlePortal(player, pPos);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    if (pPlayer.level().isClientSide) {
                        pPlayer.sendSystemMessage(Component.literal("You have an unfulfilled quota. Come back with a [Crystal Heart] and try again."));
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                handlePortal(pPlayer, pPos);
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.CONSUME;
        }
    }

    private void handlePortal(Entity player, BlockPos pPos) {
        if (player.level() instanceof ServerLevel serverlevel) {
            MinecraftServer minecraftserver = serverlevel.getServer();
            ResourceKey<Level> resourcekey = player.level().dimension() == ModDimensions.IGNEODIM_LEVEL_KEY ?
                    Level.OVERWORLD : ModDimensions.IGNEODIM_LEVEL_KEY;

            ServerLevel portalDimension = minecraftserver.getLevel(resourcekey);
            if (portalDimension != null && !player.isPassenger()) {
                if(resourcekey == ModDimensions.IGNEODIM_LEVEL_KEY) {
                    BlockPos newPos = generatePos(portalDimension,true);
                    player.setPos(newPos.getCenter());
                    player.changeDimension(portalDimension, new ModTeleporter(newPos, true));
                    player.setPos(newPos.getCenter());
                    ModMessages.sendToPlayer(new ScreenshakeS2CPacket(125,2), (ServerPlayer) player);
                } else {
                    BlockPos newPos = generatePos(minecraftserver.overworld(),false);
                    player.setPos(newPos.getCenter());
                    player.changeDimension(minecraftserver.overworld(),new ModTeleporter(newPos, false));
                    player.setPos(newPos.getCenter());
                    ModMessages.sendToPlayer(new ScreenshakeS2CPacket(75,2), (ServerPlayer) player);
                }
            }
        }
    }

    private BlockPos generatePos(ServerLevel level, boolean insideDimension) {
        int y = 200;

        Random r = new Random();
        BlockPos destinationPos = new BlockPos(r.nextInt(10000) - 10000, 200, r.nextInt(10000) - 10000);

        if (!insideDimension) {
            destinationPos = new BlockPos(0,200,0);
        }



        int tries = 0;
        while (!level.getBlockState(destinationPos).is(BlockTags.REPLACEABLE) || level.getBlockState(destinationPos.below()).is(BlockTags.REPLACEABLE)) {
            System.out.println("running");
            destinationPos = destinationPos.below();
            tries++;
            if (tries == 200 || level.getBlockState(destinationPos).is(Blocks.WATER)) {
                r = new Random();
                destinationPos = new BlockPos(r.nextInt(10000) - 10000, y, r.nextInt(10000) - 10000);
            }
        }
        if (insideDimension) {
            placeJigsaw(level, destinationPos.above());
        }

        return destinationPos.above();
    }

    public static void placeJigsaw(ServerLevel level, BlockPos position) {
        // Grab the structure file
        ResourceLocation structureId = new ResourceLocation("imv", "droppod");
        StructureTemplate template = level.getStructureManager().getOrCreate(structureId);

        // Set up how we want to place it - no rotation or mirrors
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .clearProcessors();

        RandomSource random = RandomSource.create();

        // Drop the crystal ore block
        level.setBlock(position, ModBlocks.CRYSTAL_ORE.get().defaultBlockState(), Block.UPDATE_LIMIT);

        BlockPos structurePos = position.offset(-3, -7, -3);

        // Place that structure in the world
        template.placeInWorld(level, structurePos, position, settings, random, Block.UPDATE_LIMIT);
    }
}
