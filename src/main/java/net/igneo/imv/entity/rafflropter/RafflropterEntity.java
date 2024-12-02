package net.igneo.imv.entity.rafflropter;

import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.ai.*;
import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.example.entity.BatEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RafflropterEntity extends Monster implements GeoEntity, FlyingAnimal {
    public RafflropterEntity(EntityType<? extends RafflropterEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.navigation = this.createNavigation(pLevel);
        this.setNoGravity(true);
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("fly");
    protected static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("shoot");
    protected static final RawAnimation LAND_ANIM = RawAnimation.begin().thenPlay("land");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "rafflropter", 6,this::animController));
    }

    protected <E extends GeoEntity> PlayState animController(final AnimationState<E> event) {
        if (this.onGround()) {
            return event.setAndContinue(IDLE_ANIM);
        }

        if (this.isAttacking()) {
            return event.setAndContinue(SHOOT_ANIM);
        }

        if (this.isFlying()) {
            if (this.getStamina() == 0) {
                return event.setAndContinue(LAND_ANIM);
            } else {
                return event.setAndContinue(FLY_ANIM);
            }
        }
        return event.setAndContinue(IDLE_ANIM);

    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    public static boolean canSpawnOnGround(EntityType<RafflropterEntity> entityType, ServerLevelAccessor level,
                                           MobSpawnType category, BlockPos pos, RandomSource random) {
        // Ensure the mob spawns only on solid blocks that are near the ground
        return !level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).isAir();
    }

    @Override
    public MoveControl getMoveControl() {
        return super.getMoveControl();
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.PLAYER_ATTACK) || pSource.is(DamageTypes.PLAYER_EXPLOSION) && pSource.getEntity() instanceof ServerPlayer) {
            if (!this.level().isClientSide) {
                CrystalManager.detect((ServerPlayer) pSource.getEntity());
                this.setTarget((LivingEntity) pSource.getEntity());
            }
        }
        if (pSource.is(DamageTypes.EXPLOSION) || pSource.is(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        }
        this.addStamina(1);
        return super.hurt(pSource, pAmount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.ARMOR_TOUGHNESS, 10D)
                .add(Attributes.ARMOR, 10D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.ATTACK_KNOCKBACK, -0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, -3D)
                .add(Attributes.FOLLOW_RANGE, 50D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RafflAttackGoal(this));
        this.goalSelector.addGoal(2, new FlyingWanderGoal(this));
        this.goalSelector.addGoal(3, new RafflropterLandGoal(this));

        this.targetSelector.addGoal(1, new CrystalTargetGoal(this, Player.class, false));
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            @Override
            public boolean isStableDestination(BlockPos p_27947_) {
                return true;
            }

        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }
    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public void tick() {
        if (this.getStamina() == 0) {
            this.addDeltaMovement(new Vec3(0, -0.03, 0));
        }
        super.tick();
    }

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(RafflropterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STAMINA =
            SynchedEntityData.defineId(RafflropterEntity.class, EntityDataSerializers.INT);

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void addStamina(int stamina) {
        this.entityData.set(STAMINA, this.entityData.get(STAMINA) + stamina);
    }

    public int getStamina() {
        return this.entityData.get(STAMINA);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(STAMINA, 5);
    }

}
