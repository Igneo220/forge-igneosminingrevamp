package net.igneo.imv.entity.florachnid;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FlorachnidRenderer extends GeoEntityRenderer<FlorachnidEntity> {
    public FlorachnidRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FlorachnidModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public boolean shouldRender(FlorachnidEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}
