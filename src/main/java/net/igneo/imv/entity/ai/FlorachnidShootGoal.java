package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.entity.florachnid.FlorachnidEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;

public class FlorachnidShootGoal extends MeleeAttackGoal {

    private final FlorachnidEntity entity;
    private int shootAnimDelay = 20;
    private int shootDelay = 55;
    private int shot = 0;

    public FlorachnidShootGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((FlorachnidEntity) pMob);
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.getStamina() > 10 && entity.getAttacking() == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getTarget() != null && entity.getStamina() > 2 && shot != 8;
    }

    @Override
    public void start() {
        entity.setRoot(entity.position());
        shootAnimDelay = 20;
        shootDelay = 55;
        shot = 0;
        entity.setAttacking(2);
        super.start();
    }
    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= 25;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {



        }
        if(this.shootDelay == 39) {
            entity.setRoot(entity.position());
            entity.setAttacking(2);
        }
        if(this.shootAnimDelay <= 0) {
            this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
            if (shot <= 8) {
                performAttack();
            }
        }
        if (this.shootAnimDelay <= 0 && this.shootDelay <= 0) {
            resetAttackCooldown();
            entity.setAttacking(0);
            stop();
        }
    }


    @Override
    public void tick() {
        System.out.println(shot);
        super.tick();
        //if (entity.getAttacking() == 2) {
            if (shootDelay == 45) {
                //entity.level().playSound(null, entity.blockPosition(), ModSounds.CRYSTAL_SENTRY_ATTACK.get(), SoundSource.HOSTILE, 0.2F, 1);
            }
            if (shootDelay > 0) {
                --this.shootAnimDelay;
                --this.shootDelay;

            }
            System.out.println(this.shootAnimDelay);
            System.out.println(this.shootDelay);
        //}
    }

    @Override
    protected void resetAttackCooldown() {
        shootDelay = 55;
        shootAnimDelay = 20;
    }

    protected void performAttack() {
        ++shot;
        double d0 = 0;
        double d1 = 0;
        if (shot == 1 || shot == 2 || shot == 3) {
            d0 = 0.4;
        } else if (shot == 7 || shot == 6 || shot == 5) {
            d0 = -0.4;
        }
        if (shot == 3 || shot == 4 || shot == 5) {
            d0 = 0.4;
        } else if (shot == 1 || shot == 8 || shot == 7) {
            d0 = -0.4;
        }
        shootAnimDelay = 2;
        ModEntities.SAMARANADE.get().spawn((ServerLevel) entity.level(),entity.blockPosition().above(), MobSpawnType.TRIGGERED).addDeltaMovement(new Vec3(d0,1,d1));
        entity.addStamina(-1);
    }

    @Override
    public boolean isInterruptable() {
        return entity.getAttacking() != 2;
    }

    @Override
    public void stop() {
        resetAttackCooldown();
        entity.setAttacking(0);
        super.stop();
    }
}
