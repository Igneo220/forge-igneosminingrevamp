package net.igneo.imv.entity.florachnid;

import net.igneo.imv.block.custom.PointedHuestoneBlock;
import net.igneo.imv.block.custom.PointedValueniteBlock;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.entity.ai.CrystalTargetGoal;
import net.igneo.imv.entity.ai.FlorachnidAttackGoal;
import net.igneo.imv.entity.ai.FlorachnidJumpGoal;
import net.igneo.imv.entity.ai.FlorachnidShootGoal;
import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Iterator;

public class FlorachnidEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public boolean slam = false;
    private int staminaDelay = 100;

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    public FlorachnidEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation BITE = RawAnimation.begin().thenPlay("bite");
    protected static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    protected static final RawAnimation JUMP = RawAnimation.begin().thenLoop("jump");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "florachnid", 6,this::animController));
    }

    protected <E extends GeoEntity> PlayState animController(final AnimationState<E> event) {
        if (this.getAttacking() == 3) {
            return event.setAndContinue(JUMP);
        }

        if (this.getAttacking() == 2) {
            return event.setAndContinue(SHOOT);
        }

        if (this.getAttacking() == 1) {
            return event.setAndContinue(BITE);
        }

        if (event.isMoving()) {
            return event.setAndContinue(RUN);
        }

      return event.setAndContinue(IDLE);
    }

    public static boolean canSpawnOnGround(EntityType<FlorachnidEntity> entityType, ServerLevelAccessor level,
                                           MobSpawnType category, BlockPos pos, RandomSource random) {
        // Ensure the mob spawns only on solid blocks that are near the ground
        return !level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).isAir();
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
                .add(Attributes.MAX_HEALTH, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.ARMOR_TOUGHNESS, 10D)
                .add(Attributes.ARMOR, 10D)
                .add(Attributes.ATTACK_DAMAGE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, 4D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 999999999D)
                .add(Attributes.FOLLOW_RANGE, 20D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FlorachnidJumpGoal(this));
        this.goalSelector.addGoal(2, new FlorachnidShootGoal(this,0.8,true));
        this.goalSelector.addGoal(3, new FlorachnidAttackGoal(this, 0.8, true));

        this.targetSelector.addGoal(1, new CrystalTargetGoal(this, Player.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
    }

    @Override
    public void tick() {
        --staminaDelay;
        if (staminaDelay <= 0) {
            this.addStamina(2);
            staminaDelay = 100;
        }
        if (this.getAttacking() == 2 && !this.getRoot().equals(new Vec3(0,0,0))) {
            this.setPos(this.getRoot());
        }
        if (this.getTarget() != null && this.getTarget() instanceof ServerPlayer) {
            CrystalManager.detect((ServerPlayer) this.getTarget());
        }
        if (slam && onGround()) {
            System.out.println("bruh");
            ServerLevel level = (ServerLevel) this.level();
            for (ServerPlayer player : level.players()) {
                if (player.distanceTo(this) < 10) {
                    this.swing(InteractionHand.MAIN_HAND);
                    this.doHurtTarget(player);
                    player.addDeltaMovement(new Vec3(0,0.5,0));
                    System.out.println("slamming!");
                }
            }
            this.slam = false;
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        if (this.horizontalCollision && ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
            boolean flag = false;
            AABB aabb = this.getBoundingBox().inflate(0.2);
            Iterator var8 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

            label62:
            while(true) {
                BlockPos blockpos;
                Block block;
                do {
                    if (!var8.hasNext()) {
                        if (!flag && this.onGround()) {
                            this.jumpFromGround();
                        }
                        break label62;
                    }

                    blockpos = (BlockPos)var8.next();
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    block = blockstate.getBlock();
                } while(!(block instanceof PointedHuestoneBlock) && !(block instanceof PointedValueniteBlock));

                flag = this.level().destroyBlock(blockpos, true, this) || flag;
            }
        }
        super.aiStep();
    }

    private static final EntityDataAccessor<Integer> ATTACKING =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AWAKE =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STAMINA =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ROOTX =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROOTY =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROOTZ =
            SynchedEntityData.defineId(FlorachnidEntity.class, EntityDataSerializers.FLOAT);
    public void addStamina(int stamina) {
        this.entityData.set(STAMINA, this.entityData.get(STAMINA) + stamina);
        if (this.getStamina() > 15) {
            this.entityData.set(STAMINA, 15);
        }
        if (this.getStamina() < 0) {
            this.entityData.set(STAMINA, 0);
        }
    }

    public int getStamina() {
        return this.entityData.get(STAMINA);
    }

    public void setAttacking(int attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public int getAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAwake(boolean transitioning) {
        this.entityData.set(AWAKE, transitioning);
    }

    public boolean isAwake() {
        return this.entityData.get(AWAKE);
    }

    public Vec3 getRoot() {
        return new Vec3(this.entityData.get(ROOTX),this.entityData.get(ROOTY),this.entityData.get(ROOTZ));
    }

    public void setRoot(Vec3 root) {
        this.entityData.set(ROOTX, (float) root.x);
        this.entityData.set(ROOTY, (float) root.y);
        this.entityData.set(ROOTZ, (float) root.z);
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, 0);
        this.entityData.define(AWAKE, false);
        this.entityData.define(STAMINA, 15);
        this.entityData.define(ROOTX, 0F);
        this.entityData.define(ROOTY, 0F);
        this.entityData.define(ROOTZ, 0F);
    }
}
