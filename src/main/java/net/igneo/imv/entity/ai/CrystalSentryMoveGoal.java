package net.igneo.imv.entity.ai;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CrystalSentryMoveGoal extends Goal {
    private CrystalSentryEntity entity;
    public CrystalSentryMoveGoal(CrystalSentryEntity pMob) {
        entity = pMob;
    }
    @Override
    public boolean canUse() {
        return entity.moveDelay == 0 && entity.getTarget() != null && CrystalManager.getDetected().contains(entity.getTarget());
    }

    @Override
    public void tick() {
        entity.setAwake(false);
        --entity.moveAnimDelay;
        if (entity.moveAnimDelay == 0) {
            boolean searching = true;
            int d0 = 0;
            int d1 = 0;
            int d2 = 0;
            double i = 0.4;
            if (entity.getTarget().getLookAngle().x > i) {
                d0 = -3;
            } else if (entity.getTarget().getLookAngle().x < -i) {
                d0 = 3;
            }
            if (entity.getTarget().getLookAngle().z > i) {
                d1 = -3;
            } else if (entity.getTarget().getLookAngle().z < -i) {
                d1 = 3;
            }
            int checks = 0;
            BlockPos oPos = entity.getTarget().blockPosition();
            BlockPos nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1,oPos.getZ() + d2);
            ServerLevel level = (ServerLevel) entity.level();
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
                nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1,oPos.getZ() + d2);
                ++checks;
                while (yChecks < 5) {
                    if (level.getBlockState(nPos).is(BlockTags.REPLACEABLE)) {
                        if (!level.getBlockState(nPos.below()).is(BlockTags.REPLACEABLE)) {
                            nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1,oPos.getZ() + d2);
                            searching = false;
                            break;
                        } else {
                            ++yChecks;
                            --d1;
                            nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1,oPos.getZ() + d2);
                        }
                    } else {
                        ++yChecks;
                        ++d1;
                        nPos = new BlockPos(oPos.getX() + d0, oPos.getY() + d1,oPos.getZ() + d2);
                    }
                }
                if (checks >= 5) {
                    nPos = oPos;
                    break;
                }
            }
            entity.setRoot(nPos);
            entity.moveDelay = 100;
            entity.moveAnimDelay = 30;
            entity.setAwake(true);
        }
    }
}
