package net.igneo.imv.entity.rafflropter;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RafflropterModel extends GeoModel<RafflropterEntity> {
    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/rafflropter.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/rafflropter.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/rafflropter.animation.json");


    @Override
    public ResourceLocation getModelResource(RafflropterEntity rafflropterEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(RafflropterEntity rafflropterEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(RafflropterEntity rafflropterEntity) {
        return this.animations;
    }
}
