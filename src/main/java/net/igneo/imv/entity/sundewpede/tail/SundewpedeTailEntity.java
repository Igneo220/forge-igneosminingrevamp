package net.igneo.imv.entity.sundewpede.tail;

import net.igneo.imv.networking.ModMessages;
import net.igneo.imv.networking.packet.SundewpedeSyncS2CPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SundewpedeTailEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlay("walk");

    private boolean walking;

    private int pushtries = 0;
    private int pushtriesDown = 0;

    private static final EntityDataAccessor<Integer> PARENT_ID =
            SynchedEntityData.defineId(SundewpedeTailEntity.class, EntityDataSerializers.INT);
    private Entity parent;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 3,this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected <E extends GeoEntity> PlayState animController(final AnimationState<E> event) {
        if (event.isMoving()) {
            this.walking = true;
            return event.setAndContinue(WALK_ANIM);
        }
        this.walking = false;
        return event.setAndContinue(IDLE_ANIM);
    }

    public boolean isWalking() {
        return this.walking;
    }
    public SundewpedeTailEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ARMOR_TOUGHNESS, 10D)
                .add(Attributes.ARMOR, 10D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.ATTACK_KNOCKBACK, -0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 999999999D)
                .add(Attributes.FOLLOW_RANGE, 50D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARENT_ID, -1);
    }

    public void setParent(Entity parent) {
        if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel) this.level();
            for (ServerPlayer player : level.players()) {
                ModMessages.sendToPlayer(new SundewpedeSyncS2CPacket(this.getId(), parent.getId()), player);
            }
        }
        this.parent = parent;
    }

    public Entity getParent() {
        return this.parent;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.FALL) || pSource.is(DamageTypes.IN_WALL)) {
            if (pSource.is(DamageTypes.IN_WALL)) {
                Vec3 behind = parent.position().add(parent.getLookAngle().scale(1).reverse().add(0,1,0));
                this.setPos(behind);
            }
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        if (this.parent != null) {
            if (pDamageSource.is(DamageTypes.FALL) || pDamageSource.is(DamageTypes.IN_WALL)) {
                //super.actuallyHurt(pDamageSource, 0);
                if (pDamageSource.is(DamageTypes.IN_WALL)) {
                    Vec3 behind = parent.position().add(parent.getLookAngle().scale(1).reverse().add(0,1,0));
                    this.setPos(behind);
                }
            } else {
                this.parent.hurt(pDamageSource, pDamageAmount);
                super.actuallyHurt(pDamageSource, 0);
            }
        } else {
            super.actuallyHurt(this.damageSources().genericKill(), 99999999);
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            if (parent != null && !parent.isAlive()) {
                this.discard();
            }
        }
        if (this.parent != null && this.parent.isAlive()) {
            Vec3 behind = parent.position().add(parent.getLookAngle().scale(0.8).reverse());
            Vec3 modPos = new Vec3(this.getX(), behind.y, this.getZ());
            //this.setPos(behind);

             if (modPos.distanceTo(behind) > 0.5) {
                this.setDeltaMovement((behind.x - this.getX())/4, 0,(behind.z - this.getZ())/4);
                this.lookAt(this.parent, 90, 25);
                 if (modPos.distanceTo(behind) > 0.65) {
                     this.setDeltaMovement((behind.x - this.getX())/4, 0.5,(behind.z - this.getZ())/4);
                     this.lookAt(this.parent, 90, 25);
                 }
            }
            if (this.position().distanceTo(parent.position()) < 0.6 || this.position().distanceTo(behind) > 3) {
                if (pushtriesDown < 10) {
                    this.setDeltaMovement((behind.x - this.getX())/4, -2,(behind.z - this.getZ())/4);
                    this.lookAt(this.parent, 90, 25);
                    ++pushtriesDown;
                } else {
                    this.setPos(behind);
                    this.lookAt(this.parent, 90, 25);
                    pushtriesDown = 0;
                }
            } else {
                pushtriesDown = 0;
            }
            if (this.getY() - this.parent.getY() > 0 && modPos.distanceTo(behind) > 0.2) {
                if (pushtries < 10) {
                    this.setDeltaMovement((behind.x - this.getX())/4, -2,(behind.z - this.getZ())/4);
                    this.lookAt(this.parent, 90, 25);
                    ++pushtries;
                } else {
                    this.setPos(this.getX(), this.parent.getY(), this.getZ());
                    this.lookAt(this.parent, 90, 25);
                    pushtries = 0;
                }
            } else {
                pushtries = 0;
            }
        } else if (!this.level().isClientSide) {
            this.discard();
        }
        super.tick();
    }

    @Override
    public void checkDespawn() {

    }
}
