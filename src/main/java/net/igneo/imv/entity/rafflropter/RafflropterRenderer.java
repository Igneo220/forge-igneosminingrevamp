package net.igneo.imv.entity.rafflropter;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RafflropterRenderer extends GeoEntityRenderer<RafflropterEntity> {
    public RafflropterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RafflropterModel());
    }
}
