package net.igneo.imv.entity.crystalsentry;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.ai.CrystalSentryAttackGoal;
import net.igneo.imv.entity.ai.CrystalTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CrystalSentryEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation BITE_ANIM = RawAnimation.begin().thenPlay("bite");
    protected static final RawAnimation HIDE_ANIM = RawAnimation.begin().thenPlay("hide");

    private long idleSoundDelay = 0;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 6,this::animController));
    }

    protected <E extends GeoEntity> PlayState animController(final AnimationState<E> event) {
        if (event.getController().getAnimationState().equals(AnimationController.State.TRANSITIONING)) {
            this.setTransitioning(true);
        } else {
            this.setTransitioning(false);
        }
        if (event.getController().getAnimationState().equals(AnimationController.State.TRANSITIONING) || event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            idleSoundDelay = 0;
        }
        if (this.entityData.get(ATTACKING)) {
            idleSoundDelay = 0;
            return event.setAndContinue(BITE_ANIM);
        }
        if (event.isCurrentAnimation(IDLE_ANIM) && !event.getController().getAnimationState().equals(AnimationController.State.TRANSITIONING) && !event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            //System.out.println(this.level());
            if (idleSoundDelay == 0) {
                idleSoundDelay = System.currentTimeMillis() - 530;
            }
            if (System.currentTimeMillis() >= idleSoundDelay + 1000) {
                //this.level().playSound(null,this.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE);
                System.out.println("BITE!!");
                this.level().playLocalSound(this.getX(),this.getY(),this.getZ(),SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.HOSTILE,1,1,false);
                idleSoundDelay = System.currentTimeMillis();
            }
        }
        if (!this.isAwake()) {
            return event.setAndContinue(HIDE_ANIM);
        } else {
            return event.setAndContinue(IDLE_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AWAKE =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRANSITIONING =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    public CrystalSentryEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;
    private Vec3 planted = null;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel) this.level();
            for (ServerPlayer target : CrystalManager.getDetected()) {
                if (target.distanceTo(this) <= 50) {

                    break;
                }
            }
            if (this.getTarget() != null) {
                entityData.set(AWAKE, true);
            } else {
                entityData.set(AWAKE, false);
            }
        }
        if (this.planted == null) {
            this.planted = this.position();
        }
        this.setPos(this.planted);

        if(this.level().isClientSide) {
        }
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setTransitioning(boolean transitioning) {
        this.entityData.set(TRANSITIONING, transitioning);
    }

    public boolean isTransitioning() {
        return this.entityData.get(TRANSITIONING);
    }

    public void setAwake(boolean transitioning) {
        this.entityData.set(AWAKE, transitioning);
    }

    public boolean isAwake() {
        return this.entityData.get(AWAKE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(TRANSITIONING, false);
        this.entityData.define(AWAKE, false);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.PLAYER_ATTACK) || pSource.is(DamageTypes.PLAYER_EXPLOSION)) {
            if (!this.level().isClientSide) {
                CrystalManager.detect((ServerPlayer) pSource.getEntity());
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this, Player.class));
        this.goalSelector.addGoal(2, new CrystalSentryAttackGoal(this, 0, true));

        this.targetSelector.addGoal(1, new CrystalTargetGoal(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10D)
                .add(Attributes.ARMOR, 10D)
                .add(Attributes.ATTACK_DAMAGE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, -0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 999999999D)
                .add(Attributes.FOLLOW_RANGE, 50D);
    }


}
