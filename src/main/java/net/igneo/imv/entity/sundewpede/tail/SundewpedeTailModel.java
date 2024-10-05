package net.igneo.imv.entity.sundewpede.tail;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SundewpedeTailModel extends GeoModel<SundewpedeTailEntity> {
    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/sundewpedetail.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/sundewpede.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/sundewpedetail.animation.json");


    @Override
    public ResourceLocation getModelResource(SundewpedeTailEntity sundewpedeTailEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(SundewpedeTailEntity sundewpedeTailEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SundewpedeTailEntity sundewpedeTailEntity) {
        return this.animations;
    }
}
