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
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        if (pLevel instanceof ServerLevel && pEntity instanceof ServerPlayer) {
            CrystalManager.detect((ServerPlayer) pEntity);
        }
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        super.wasExploded(pLevel, pPos, pExplosion);
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
