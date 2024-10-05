package net.igneo.imv.entity.ai;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class SundewpedePanicGoal extends PanicGoal {
    private int panicTime = 0;
    private final Entity entity;
    public SundewpedePanicGoal(PathfinderMob pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        entity = pMob;
    }

    @Override
    protected boolean shouldPanic() {
        boolean detected = false;
        if (!entity.level().isClientSide) {
            ServerLevel level = (ServerLevel) entity.level();
            for (ServerPlayer target : CrystalManager.getDetected()) {
                if (target.distanceTo(entity) <= 15 && target != entity) {
                    detected = true;
                    System.out.println("RUNNING");
                    this.panicTime = 10;
                    break;
                }
            }
            for (ServerPlayer target : level.players()) {
                if (target.distanceTo(entity) <= 5 && target != entity) {
                    detected = true;
                    System.out.println("RUNNING");
                    this.panicTime = 50;
                    CrystalManager.detect(target);
                    break;
                }
            }
            if (panicTime > 0) {
                System.out.println(panicTime);
                --panicTime;
            }
        }
        return (detected || panicTime > 0);
    }
}
