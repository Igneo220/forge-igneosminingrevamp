package net.igneo.imv.entity.crystalsentry;

import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.ai.CrystalSentryAttackGoal;
import net.igneo.imv.entity.ai.CrystalSentryMoveGoal;
import net.igneo.imv.entity.ai.CrystalTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
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
    protected static final RawAnimation HIDING_ANIM = RawAnimation.begin().thenLoop("hiddenidle");

    private long idleSoundDelay = 0;
    private boolean hiding;
    public int moveDelay = 100;
    public int moveAnimDelay = 30;

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
            if (idleSoundDelay == 0) {
                idleSoundDelay = System.currentTimeMillis() - 530;
            }
            if (System.currentTimeMillis() >= idleSoundDelay + 1000) {
                //this.level().playSound(null,this.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE);
                this.level().playLocalSound(this.getX(),this.getY(),this.getZ(),SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.HOSTILE,1,1,false);
                idleSoundDelay = System.currentTimeMillis();
            }
        }
        if (!this.isAwake()) {
            return event.setAndContinue(HIDE_ANIM);
        } else {
            hiding = false;
            return event.setAndContinue(IDLE_ANIM);
        }
    }

    public static boolean canSpawnOnGround(EntityType<CrystalSentryEntity> entityType, ServerLevelAccessor level,
                                           MobSpawnType category, BlockPos pos, RandomSource random) {
        // Ensure the mob spawns only on solid blocks that are near the ground
        return !level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).isAir();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    public CrystalSentryEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;
    public Vec3 planted = null;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel) this.level();
            if ((level.getBlockState(this.blockPosition().below()).is(BlockTags.REPLACEABLE))) {
                System.out.println("moving down");
                setRoot(this.blockPosition().below());
            }
            if (moveDelay < 0) {
                moveDelay = 0;
            }
            if (this.getTarget() != null) {

                boolean nullify = true;
                for (ServerPlayer target : CrystalManager.getDetected()) {
                    if (target == this.getTarget()) {
                        nullify = false;
                        break;
                    }
                }
                if (nullify) {
                    this.setTarget(null);
                    entityData.set(AWAKE, false);
                }

            } else {
                entityData.set(AWAKE, false);
            }
        }
        if (this.planted == null) {
            this.planted = this.position();
        } else if (!this.getRoot().equals(BlockPos.ZERO)) {
            this.planted = this.getRoot().getCenter().add(0,-0.5,0);
        }
        this.setPos(this.planted);

        if(this.level().isClientSide) {
        }


    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AWAKE =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRANSITIONING =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> ROOT =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> MOVEDELAY =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOVEANIMDELAY =
            SynchedEntityData.defineId(CrystalSentryEntity.class, EntityDataSerializers.INT);

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

    public void setRoot(BlockPos pos) {
        this.entityData.set(ROOT, pos);
    }

    public BlockPos getRoot() {
        return this.entityData.get(ROOT);
    }

    public void setMoveDelay(int delay) {
        this.entityData.set(MOVEDELAY, delay);
    }

    public int getMoveDelay() {
        return this.entityData.get(MOVEDELAY);
    }

    public void setMoveAnimDelay(int delay) {
        this.entityData.set(MOVEANIMDELAY, delay);
    }

    public int getMoveAnimDelay() {
        return this.entityData.get(MOVEANIMDELAY);
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
        this.entityData.define(ROOT, BlockPos.ZERO);
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
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CrystalSentryMoveGoal(this));
        this.goalSelector.addGoal(2, new CrystalSentryAttackGoal(this, 0, true));

        this.targetSelector.addGoal(1, new CrystalTargetGoal(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10D)
                .add(Attributes.ARMOR, 10D)
                .add(Attributes.ATTACK_DAMAGE, 5D)
                .add(Attributes.ATTACK_KNOCKBACK, -0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 999999999D)
                .add(Attributes.FOLLOW_RANGE, 20D);
    }


}
