package net.igneo.imv.entity.ai;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.florachnid.FlorachnidEntity;
import net.igneo.imv.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;

public class FlorachnidAttackGoal extends MeleeAttackGoal {
    private final FlorachnidEntity entity;
    private int attackDelay = 30;
    private int ticksUntilNextAttack = 14;
    private boolean shouldCountTillNextAttack = false;
    public FlorachnidAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((FlorachnidEntity) pMob);
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 30;
        ticksUntilNextAttack = 14;
    }

    @Override
    public void tick() {
        super.tick();
        if (entity.getAttacking() == 1) {
            if (attackDelay == 30) {
                entity.level().playSound(null,entity.blockPosition(), ModSounds.FLORA_ATTACK.get(), SoundSource.HOSTILE,0.5F,(float)Math.random() + 0.5F);
                //entity.level().playSound(null, entity.blockPosition(), ModSounds.CRYSTAL_SENTRY_ATTACK.get(), SoundSource.HOSTILE, 0.2F, 1);
            }
            if (shouldCountTillNextAttack) {
                --this.ticksUntilNextAttack;
            }
            if (attackDelay > 0) {
                --this.attackDelay;
            }
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {

            if(this.attackDelay >= 29) {
                entity.setAttacking(1);
                shouldCountTillNextAttack = true;
            }

            if(this.ticksUntilNextAttack == 0) {
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
            }


        }
        if (this.ticksUntilNextAttack <= 0 && this.attackDelay <= 0) {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(0);
            stop();
        }
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    protected void resetAttackCooldown() {
        attackDelay = 30;
        ticksUntilNextAttack = 14;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= 25;
    }
    protected void performAttack(LivingEntity pEnemy) {
        System.out.println("running attack");
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
        pEnemy.addDeltaMovement(new Vec3(0,0.5,0));
    }

    @Override
    public void stop() {
        entity.setAttacking(0);
        super.stop();
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && CrystalManager.getDetected().contains(entity.getTarget()) && entity.getAttacking() == 0 && entity.getStamina() < 11;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getTarget() != null && CrystalManager.getDetected().contains(entity.getTarget()) && entity.getStamina() < 11;
    }
}
