package net.igneo.imv.entity.sundewpede.head;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SundewpedeHeadModel extends GeoModel<SundewpedeHeadEntity> {
    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/sundewpedehead.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/sundewpede.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/sundewpedehead.animation.json");


    @Override
    public ResourceLocation getModelResource(SundewpedeHeadEntity sundewpedeHeadEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(SundewpedeHeadEntity sundewpedeHeadEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SundewpedeHeadEntity sundewpedeHeadEntity) {
        return this.animations;
    }
}
