package net.igneo.imv.entity.crystalsentry;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.igneo.imv.IMV;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.model.GeoModel;

public class CrystalSentryModel extends GeoModel<CrystalSentryEntity> {
	private final ResourceLocation model = new ResourceLocation(IMV.MOD_ID, "geo/crystalsentry.geo.json");
	private final ResourceLocation texture = new ResourceLocation(IMV.MOD_ID, "textures/entity/crystalsentry.png");
	private final ResourceLocation animations = new ResourceLocation(IMV.MOD_ID, "animations/crystalsentry.animation.json");


	@Override
	public ResourceLocation getModelResource(CrystalSentryEntity crystalSentryEntity) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CrystalSentryEntity crystalSentryEntity) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CrystalSentryEntity crystalSentryEntity) {
		return this.animations;
	}
}