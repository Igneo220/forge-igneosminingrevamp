package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.rafflropter.RafflropterEntity;
import net.igneo.imv.entity.rafflropter.RafflropterRenderer;
import net.minecraft.world.entity.ai.goal.Goal;

public class RafflropterLandGoal extends Goal {
    private RafflropterEntity entity;
    private int regenDelay = 40;
    public RafflropterLandGoal(RafflropterEntity pMob) {
        entity = pMob;
    }
    @Override
    public boolean canUse() {
        return entity.getStamina() < 3;
    }

    @Override
    public void tick() {
        if (entity.onGround()) {
            --regenDelay;
            if (regenDelay == 0) {
                entity.addStamina(1);
                regenDelay = 40;
            }
        }
        super.tick();
    }
}
