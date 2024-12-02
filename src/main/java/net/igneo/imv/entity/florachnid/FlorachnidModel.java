package net.igneo.imv.entity.florachnid;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class FlorachnidModel  extends GeoModel<FlorachnidEntity> {

    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/florachnid.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/florachnid.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/florachnid.animation.json");


    @Override
    public ResourceLocation getModelResource(FlorachnidEntity florachnidEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(FlorachnidEntity florachnidEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(FlorachnidEntity florachnidEntity) {
        return this.animations;
    }
}
