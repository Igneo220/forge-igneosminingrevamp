package net.igneo.imv.block.custom;

import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalArmorBlock extends Block {
    public CrystalArmorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return false;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        for (int x = 10; x > 0; --x) {
            for (int y = 10; y > 0; --y) {
                for (int z = 10; z > 0; --z) {
                    if (pLevel.getBlockState(pPos.subtract(new BlockPos(-x,-y+5,-z))).is(ModBlocks.CRYSTAL_HEART.get())) {
                        System.out.println("emergency start");
                        pLevel.scheduleTick(pPos.subtract(new BlockPos(-x,-y+5,-z)),ModBlocks.CRYSTAL_HEART.get(),0);
                    }
                }
            }
        }
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        for (int x = 10; x > 0; --x) {
            for (int y = 10; y > 0; --y) {
                for (int z = 10; z > 0; --z) {
                    if (pLevel.getBlockState(pPos.subtract(new BlockPos(-x,-y+5,-z))).is(ModBlocks.CRYSTAL_HEART.get())) {
                        System.out.println("emergency start");
                        pLevel.scheduleTick(pPos.subtract(new BlockPos(-x,-y+5,-z)),ModBlocks.CRYSTAL_HEART.get(),0);
                    }
                }
            }
        }
        if (pLevel instanceof ServerLevel && pEntity instanceof ServerPlayer) {
            CrystalManager.detect((ServerPlayer) pEntity);
        }
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        super.wasExploded(pLevel, pPos, pExplosion);
        for (int x = 10; x > 0; --x) {
            for (int y = 10; y > 0; --y) {
                for (int z = 10; z > 0; --z) {
                    if (pLevel.getBlockState(pPos.subtract(new BlockPos(-x,-y+5,-z))).is(ModBlocks.CRYSTAL_HEART.get())) {
                        System.out.println("emergency start");
                        pLevel.scheduleTick(pPos.subtract(new BlockPos(-x,-y+5,-z)),ModBlocks.CRYSTAL_HEART.get(),0);
                    }
                }
            }
        }
        if (pLevel instanceof ServerLevel) {
            for (ServerPlayer player : ((ServerLevel) pLevel).players()) {
                float f = (float)(player.getBlockX() - pPos.getX());
                float f1 = (float)(player.getBlockY() - pPos.getY());
                float f2 = (float)(player.getBlockZ() - pPos.getZ());
                float dist = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                if (dist < 100) {
                    CrystalManager.detect(player);
                }
            }
        }
    }
}
