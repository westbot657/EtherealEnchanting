package com.westbot.ethereal_enchanting.client.render.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

public class CelestialTrailEntityModel extends EntityModel<MobEntity> {
	public final ModelPart bone;
	public CelestialTrailEntityModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(4, 0).cuboid(-1.0F, -16.0F, 0.0F, 2.0F, 16.0F, 0.0F, new Dilation(0.0F))
		.uv(0, -2).cuboid(0.0F, -16.0F, -1.0F, 0.0F, 16.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bone.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public void setAngles(MobEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}