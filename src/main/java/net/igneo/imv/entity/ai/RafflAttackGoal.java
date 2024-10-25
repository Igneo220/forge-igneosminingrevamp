package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.entity.rafflropter.RafflropterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class RafflAttackGoal extends Goal {
    private static final int WANDER_THRESHOLD = 22;
    private int shootDelay = 50;
    private RafflropterEntity entity;
    public RafflAttackGoal(RafflropterEntity pMob)  {
        this.setFlags(EnumSet.of(Flag.MOVE));
        entity = pMob;
    }

    public boolean canUse() {
        return entity.getNavigation().isDone() && entity.getTarget() != null && entity.getStamina() > 0;
    }

    public boolean canContinueToUse() {
        return entity.getNavigation().isInProgress() && !entity.getNavigation().isStuck() && entity.getTarget() != null && entity.getStamina() > 0;
    }

    public void start() {
        Vec3 vec3 = entity.getTarget().position().add(0,6,0);
        entity.getNavigation().moveTo(entity.getNavigation().createPath(BlockPos.containing(vec3), 4), 1.0);
        //entity.getMoveControl().setWantedPosition(vec3.x,vec3.y,vec3.z,1);
        System.out.println(vec3);
    }

    @Override
    public void tick() {
        --shootDelay;
        if (shootDelay <= 8) {
            entity.setAttacking(true);
            if (shootDelay <= 0) {
                entity.addStamina(-1);
                shootDelay = 50;
                ModEntities.SAMARANADE.get().spawn((ServerLevel) entity.level(), entity.blockPosition(), MobSpawnType.TRIGGERED).setDeltaMovement(0,1,0);
            }
        } else {
            entity.setAttacking(false);
        }
        super.tick();
    }

    @Nullable
    private Vec3 findPos() {
        Vec3 vec3;
        double d0;
        double d1;
        double d2;
        if (Math.random() > 0.5) {
            d0 = Math.random();
        } else {
            d0 = Math.random() * -1;
        }
        if (Math.random() > 0.5) {
            d2 = Math.random();
        } else {
            d2 = Math.random() * -1;
        }

        vec3 = entity.getTarget().getViewVector(0.0F);
        //vec3 = new Vec3(entity.getX() + d0,d1, entity.getZ() + d2);
        Vec3 vec32 = HoverRandomPos.getPos(entity, 8, 7, vec3.x, vec3.z, 1.5707964F, 3, 1);
        return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(entity, 4, 4, -2, entity.getTarget().getX(), entity.getTarget().getZ(), 1.5707963705062866);
    }
}
