package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.florachnid.FlorachnidEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FlorachnidJumpGoal extends Goal {
    private final FlorachnidEntity entity;
    private int jumpDelay = 20;
    private int jumpAnimDelay = 7;

    public FlorachnidJumpGoal(PathfinderMob pMob) {
        entity = ((FlorachnidEntity) pMob);
    }
    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.getStamina() > 5 && entity.getTarget().distanceTo(entity) > 8 && entity.getAttacking() == 0;
    }

    @Override
    public void start() {
        jumpAnimDelay = 7;
        jumpDelay = 20;
        super.start();
    }

    @Override
    public boolean isInterruptable() {
        return !canContinueToUse();
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getAttacking() == 3 && entity.getTarget() != null;
    }

    @Override
    public void tick() {
        this.entity.getNavigation().stop();
        System.out.println("running jump");
        if (jumpDelay == 20) {
            entity.setAttacking(3);
        }
        --jumpDelay;
        --jumpAnimDelay;
        if (jumpDelay <= 0 || (entity.slam && this.entity.onGround())) {
            entity.setAttacking(0);
            stop();
        }
        if (jumpAnimDelay == 0 && entity.getTarget() != null) {
            this.entity.addStamina(-5);
            double d0 = entity.getTarget().position().x - entity.position().x;
            double d1 = entity.getTarget().position().y - entity.position().y;
            double d2 = entity.getTarget().position().z - entity.position().z;
            entity.setDeltaMovement(d0/4.5,0.6 + d1,d2/4.5);
            System.out.println("setting slam");
            entity.slam = true;
        }
        if (jumpAnimDelay == -5) {


        }
        super.tick();
    }
}
