package net.igneo.imv.entity.samaranade;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SamaranadeModel extends GeoModel<SamaranadeEntity> {
    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/samaranade.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/samaranade.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/samaranade.animation.json");


    @Override
    public ResourceLocation getModelResource(SamaranadeEntity samaranadeEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(SamaranadeEntity samaranadeEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SamaranadeEntity samaranadeEntity) {
        return this.animations;
    }
}
