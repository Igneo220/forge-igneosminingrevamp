package net.igneo.imv.entity.sundewpede.tail;

import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SundewpedeTailRenderer extends GeoEntityRenderer<SundewpedeTailEntity> {
    public SundewpedeTailRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SundewpedeTailModel());
    }
}
