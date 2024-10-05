package net.igneo.imv.entity.sundewpede.body;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SundewpedeBodyRenderer extends GeoEntityRenderer<SundewpedeBodyEntity> {
    public SundewpedeBodyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SundewpedeBodyModel());
    }
}
