package net.igneo.imv.entity.ai;

import net.igneo.imv.entity.rafflropter.RafflropterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
 public class FlyingWanderGoal extends Goal {
     private static final int WANDER_THRESHOLD = 22;
     private RafflropterEntity entity;
     public FlyingWanderGoal(RafflropterEntity pMob)  {
         this.setFlags(EnumSet.of(Flag.MOVE));
         entity = pMob;
     }

     public boolean canUse() {
         return entity.getNavigation().isDone() && entity.getStamina() > 0;
     }

     public boolean canContinueToUse() {
         return entity.getNavigation().isInProgress() && !entity.getNavigation().isStuck() && entity.getStamina() > 0;
     }

     public void start() {
         Vec3 vec3 = this.findPos();
         if (vec3 != null) {
             entity.getNavigation().moveTo(entity.getNavigation().createPath(BlockPos.containing(vec3), 1), 1.0);
             //entity.getMoveControl().setWantedPosition(vec3.x,vec3.y,vec3.z,1);
         }
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

         vec3 = entity.getViewVector(0.0F);
         //vec3 = new Vec3(entity.getX() + d0,d1, entity.getZ() + d2);
         Vec3 vec32 = HoverRandomPos.getPos(entity, 8, 7, vec3.x, vec3.z, 1.5707964F, 3, 1);
         return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(entity, 8, 4, -2, vec3.x, vec3.z, 1.5707963705062866);
     }
 }
