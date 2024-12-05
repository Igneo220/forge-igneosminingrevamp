package net.igneo.imv.block.custom;

import net.igneo.imv.block.entity.CrystalHeartBlockEntity;
import net.igneo.imv.block.entity.ModBlockEntities;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.networking.ModMessages;
import net.igneo.imv.networking.packet.CrystalSentrySummonSoundS2CPacket;
import net.igneo.imv.networking.packet.FloraSummonSoundS2CPacket;
import net.igneo.imv.networking.packet.RafflSummonSoundS2CPacket;
import net.igneo.imv.networking.packet.ScreenshakeS2CPacket;
import net.igneo.imv.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CrystalHeartBlock extends BaseEntityBlock {

    public CrystalHeartBlock(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrystalHeartBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.CRYSTAL_HEART_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

}
