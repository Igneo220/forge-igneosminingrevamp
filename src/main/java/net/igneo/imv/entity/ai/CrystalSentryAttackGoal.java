package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.igneo.imv.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationController;

public class CrystalSentryAttackGoal extends MeleeAttackGoal {
    private final CrystalSentryEntity entity;
    private int attackDelay = 50;
    private int ticksUntilNextAttack = 20;
    private boolean shouldCountTillNextAttack = false;
    public CrystalSentryAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((CrystalSentryEntity) pMob);
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 50;
        ticksUntilNextAttack = 26;
    }

    @Override
    public void tick() {
        super.tick();
        if (!entity.isTransitioning() && entity.isAttacking()) {
            if (attackDelay == 45) {
                entity.level().playSound(null, entity.blockPosition(), ModSounds.CRYSTAL_SENTRY_ATTACK.get(), SoundSource.HOSTILE, 0.2F, 1);
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

            if(this.attackDelay == 50) {
                entity.setAttacking(true);
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
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }

    @Override
    protected void resetAttackCooldown() {
        attackDelay = 50;
        ticksUntilNextAttack = 26;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= 35;
    }
    protected void performAttack(LivingEntity pEnemy) {
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
        pEnemy.setDeltaMovement(new Vec3(entity.getX()-pEnemy.getX(),1,entity.getZ()-pEnemy.getZ()).scale(0.3));
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }

}
