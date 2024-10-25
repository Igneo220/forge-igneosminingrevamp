package net.igneo.imv.entity.sundewpede.head;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SundewpedeHeadRenderer extends GeoEntityRenderer<SundewpedeHeadEntity> {
    public SundewpedeHeadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SundewpedeHeadModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
