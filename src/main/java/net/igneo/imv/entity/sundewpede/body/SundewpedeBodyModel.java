package net.igneo.imv.entity.sundewpede.body;

import net.igneo.imv.IMV;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SundewpedeBodyModel extends GeoModel<SundewpedeBodyEntity> {
    private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/sundewpedebody.geo.json");
    private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/sundewpede.png");
    private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/sundewpedebody.animation.json");


    @Override
    public ResourceLocation getModelResource(SundewpedeBodyEntity sundewpedeBodyEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(SundewpedeBodyEntity sundewpedeBodyEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SundewpedeBodyEntity sundewpedeBodyEntity) {
        return this.animations;
    }
}
