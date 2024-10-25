package net.igneo.imv.entity.samaranade;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SamaranadeEntity extends Monster implements GeoEntity {

    public SamaranadeEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation EXPLODE = RawAnimation.begin().thenLoop("explode");
    protected static final RawAnimation FALL = RawAnimation.begin().thenPlay("fall");

    private int fuse = 20;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "samaranade", 6,this::animController));
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    protected <E extends GeoEntity> PlayState animController(final AnimationState<E> event) {
        if (this.onGround()) {
            return event.setAndContinue(EXPLODE);
        } else {
            return event.setAndContinue(FALL);
        }

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ARMOR_TOUGHNESS, 0D)
                .add(Attributes.ARMOR, 0D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.ATTACK_KNOCKBACK, -0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 999999999D)
                .add(Attributes.FOLLOW_RANGE, 50D)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0.3);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.EXPLOSION) || pSource.is(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        } else {
            this.discard();
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround()) {
            --fuse;
        }
        if (fuse == 0) {
            if (this.level() instanceof ServerLevel) {
                ServerLevel level = (ServerLevel) this.level();
                level.explode(this,this.getX(),this.getY(),this.getZ(),4, Level.ExplosionInteraction.NONE);
            }
            this.discard();
        }
    }
}
