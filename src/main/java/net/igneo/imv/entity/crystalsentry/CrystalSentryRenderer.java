package net.igneo.imv.entity.crystalsentry;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrystalSentryRenderer extends GeoEntityRenderer<CrystalSentryEntity> {

    public CrystalSentryRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CrystalSentryModel());
    }
}
