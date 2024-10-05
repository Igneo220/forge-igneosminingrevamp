package net.igneo.imv.entity.sundewpede.head;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SundewpedeHeadRenderer extends GeoEntityRenderer<SundewpedeHeadEntity> {
    public SundewpedeHeadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SundewpedeHeadModel());
    }
}
