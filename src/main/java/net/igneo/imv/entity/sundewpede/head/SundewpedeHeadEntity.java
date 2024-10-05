package net.igneo.imv.entity.sundewpede.head;

import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.entity.ai.CrystalSentryAttackGoal;
import net.igneo.imv.entity.ai.SundewpedePanicGoal;
import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.igneo.imv.entity.sundewpede.tail.SundewpedeTailEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SundewpedeHeadEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlay("walk");

    private boolean walking;

    private boolean summonedChildren = false;
    public SundewpedeHeadEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 6,this::animController));
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

    @Override
    public void push(Entity pEntity) {

    }

    @Override
    public void push(double pX, double pY, double pZ) {

    }
    @Override
    public void tick() {
        if (!summonedChildren) {
            if (!this.level().isClientSide) {
                ServerLevel level = (ServerLevel) this.level();
                SundewpedeBodyEntity lastSummoned = null;
                for (int i = 0; i < 10; ++i) {
                    if (i == 9) {
                        ModEntities.SUNDEWPEDE_TAIL.get().spawn(level, blockPosition(), MobSpawnType.TRIGGERED).setParent(lastSummoned);
                    } else {
                        Entity parent = lastSummoned;
                        if (parent == null) {
                            parent = this;
                        }
                        lastSummoned = ModEntities.SUNDEWPEDE_BODY.get().spawn(level, blockPosition(), MobSpawnType.TRIGGERED);
                        lastSummoned.setParent(parent);
                    }
                }
                summonedChildren = true;
            }
        }
        super.tick();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SundewpedePanicGoal(this, 1));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.5,40));
        this.goalSelector.addGoal(2, new FloatGoal(this));
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
}
