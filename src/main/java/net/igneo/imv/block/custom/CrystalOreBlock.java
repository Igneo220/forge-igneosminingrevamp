package net.igneo.imv.block.custom;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class CrystalOreBlock extends Block {
    public CrystalOreBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
        if (!pLevel.isClientSide) {
            CrystalManager.detect((ServerPlayer) pPlayer);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide) {
            CrystalManager.detect((ServerPlayer) player);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);

    }
}
