package net.igneo.imv.block.entity;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalHeartBlockEntity extends BlockEntity {
    public CrystalHeartBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRYSTAL_HEART_BE.get(), pPos, pBlockState);
    }

    private boolean angered = false;

    private int attackTicks = 500;

    private int attacktype = 0;

    private int heartbeat = 20;

    public void tick(Level flevel, BlockPos pPos, BlockState pState) {
        if (flevel instanceof ServerLevel) {
            ServerLevel pLevel = (ServerLevel) flevel;
            ++heartbeat;
            for (ServerPlayer player : (pLevel.players())) {
                float f = (float) (player.getBlockX() - pPos.getX());
                float f1 = (float) (player.getBlockY() - pPos.getY());
                float f2 = (float) (player.getBlockZ() - pPos.getZ());
                float dist = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                if (dist < 40 && !player.isCreative() && !angered) {
                    CrystalManager.detect(player);
                    angered = true;
                    pLevel.playSound(null, pPos, ModSounds.HEART_ANGER.get(), SoundSource.HOSTILE);
                }
                if (this.angered) {
                    attackTicks(pLevel, player, dist);
                }
                if (heartbeat >= 20) {
                    pLevel.playSound(null, pPos, ModSounds.HEART_BEAT.get(), SoundSource.HOSTILE);
                    if (dist < 5) {
                        ModMessages.sendToPlayer(new ScreenshakeS2CPacket(10, 3), player);
                    } else if (dist < 10) {
                        ModMessages.sendToPlayer(new ScreenshakeS2CPacket(10, 2), player);
                    } else if (dist < 15) {
                        ModMessages.sendToPlayer(new ScreenshakeS2CPacket(10, 1), player);
                    }
                    heartbeat = 0;
                }
            }
        }
    }

    private void attackTicks(ServerLevel pLevel, ServerPlayer player, float dist) {
        if (CrystalManager.getDetected().contains(player)) {
            ++attackTicks;
            if (attackTicks >= 1000) {
                if (summon(pLevel, player, dist)) {
                    attackTicks = 0;
                }
            }
        }
    }

    private boolean summon(ServerLevel pLevel, ServerPlayer player, float dist) {
        System.out.println(attacktype);
        switch (attacktype) {
            case 0: {
                for (ServerPlayer target : pLevel.players()) {
                    if (dist < 50) {
                        BlockPos summonSpot = findSummonSpot(player);
                        ModMessages.sendToPlayer(new FloraSummonSoundS2CPacket(),target);
                        ModEntities.FLORACHNID.get().spawn(pLevel, summonSpot , MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        ++attacktype;
                        return true;
                    }
                }
            }
            case 1: {
                for (ServerPlayer target : pLevel.players()) {
                    if (dist < 50) {
                        BlockPos summonSpot = findSummonSpot(player);
                        ModMessages.sendToPlayer(new CrystalSentrySummonSoundS2CPacket(),target);
                        ModEntities.CRYSTAL_SENTRY.get().spawn(pLevel, findSummonSpot(player), MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        summonSpot = findSummonSpot(player);
                        ModEntities.CRYSTAL_SENTRY.get().spawn(pLevel, findSummonSpot(player), MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        ++attacktype;
                        return true;
                    }
                }
            }
            case 2: {
                for (ServerPlayer target : pLevel.players()) {
                    if (dist < 50) {
                        BlockPos summonSpot = findSummonSpot(player);
                        ModMessages.sendToPlayer(new RafflSummonSoundS2CPacket(),target);
                        ModEntities.RAFFLROPTER.get().spawn(pLevel, findSummonSpot(player), MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        summonSpot = findSummonSpot(player);
                        ModEntities.RAFFLROPTER.get().spawn(pLevel, findSummonSpot(player), MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        summonSpot = findSummonSpot(player);
                        ModEntities.RAFFLROPTER.get().spawn(pLevel, findSummonSpot(player), MobSpawnType.TRIGGERED).setPos(summonSpot.getX(),summonSpot.getY() - 0.5,summonSpot.getZ());
                        attacktype = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public BlockPos findSummonSpot(ServerPlayer enemy) {
        boolean searching = true;
        int d0 = 0;
        int d1 = 0;
        int d2 = 0;
        double i = 0.4;
        if (enemy.getLookAngle().x > i) {
            d0 = -3;
        } else if (enemy.getLookAngle().x < -i) {
            d0 = 3;
        }
        if (enemy.getLookAngle().z > i) {
            d1 = -3;
        } else if (enemy.getLookAngle().z < -i) {
            d1 = 3;
        }
        int checks = 0;
        BlockPos oPos = enemy.blockPosition();
        BlockPos nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1, oPos.getZ() + d2);
        ServerLevel level = (ServerLevel) enemy.level();
        while (searching) {
            int yChecks = 0;
            if (Math.random() > 0.5) {
                d0 = (int) (Math.random() * 4);
            } else {
                d0 = (int) (Math.random() * -4);
            }
            if (Math.random() > 0.5) {
                d2 = (int) (Math.random() * 4);
            } else {
                d2 = (int) (Math.random() * -4);
            }
            nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1, oPos.getZ() + d2);
            ++checks;
            while (yChecks < 5) {
                if (level.getBlockState(nPos).is(BlockTags.REPLACEABLE)) {
                    if (!level.getBlockState(nPos.below()).is(BlockTags.REPLACEABLE)) {
                        nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1, oPos.getZ() + d2);
                        searching = false;
                        break;
                    } else {
                        ++yChecks;
                        --d1;
                        nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1, oPos.getZ() + d2);
                    }
                } else {
                    ++yChecks;
                    ++d1;
                    nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1, oPos.getZ() + d2);
                }
            }
            if (checks >= 5) {
                nPos = oPos;
                break;
            }
        }
        return nPos;
    }



}
