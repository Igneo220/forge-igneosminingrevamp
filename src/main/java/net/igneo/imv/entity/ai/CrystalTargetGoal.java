package net.igneo.imv.entity.ai;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class CrystalTargetGoal extends NearestAttackableTargetGoal {
    private final Entity entity;
    public CrystalTargetGoal(Mob pMob, Class pTargetType, boolean pMustSee) {
        super(pMob, pTargetType, pMustSee);
        this.entity = pMob;
    }

    @Override
    protected AABB getTargetSearchArea(double pTargetDistance) {
        return super.getTargetSearchArea(pTargetDistance*20);
    }

    @Override
    protected void findTarget() {
        if (!CrystalManager.getDetected().isEmpty()) {
            ServerPlayer closestTarget = null;
            float lastDist = 50;
            for (ServerPlayer target : CrystalManager.getDetected()) {
                if (target.distanceTo(entity) < lastDist) {
                    lastDist = target.distanceTo(entity);
                    closestTarget = target;
                }
            }
            if (closestTarget != null) {
                this.target = closestTarget;
            }
        }
    }
}
