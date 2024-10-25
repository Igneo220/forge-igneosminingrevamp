package net.igneo.imv.entity.samaranade;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SamaranadeRenderer extends GeoEntityRenderer<SamaranadeEntity> {
    public SamaranadeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SamaranadeModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
