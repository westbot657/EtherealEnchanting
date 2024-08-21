package com.westbot.ethereal_enchanting.client.render.entity;

import com.westbot.ethereal_enchanting.client.networking.TrailSyncBatcher;
import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;


@Environment(EnvType.CLIENT)
public class CelestialTrailEntityRenderer extends EntityRenderer<CelestialTrailEntity> {
    private static final Identifier TEXTURE = Identifier.of("ethereal_enchanting", "textures/entity/celestial_trail.png");
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("ethereal_enchanting", "entity/celestial_trail"), "main");

    private final CelestialTrailEntityModel model;

    public CelestialTrailEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new CelestialTrailEntityModel(ctx.getPart(MODEL_LAYER));
    }

    @Override
    public Identifier getTexture(CelestialTrailEntity entity) {
        return TEXTURE;
    }

    public static Vector3f angleBetween(Vec3d start, Vec3d end) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        // Yaw calculation
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        // Pitch calculation
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) Math.toDegrees(Math.atan2(-dy, horizontalDistance));

        return new Vector3f((float) Math.toRadians(-90+pitch), (float) Math.toRadians(-yaw), 0);
    }

    @Override
    public void render(CelestialTrailEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (!entity.synced) {
            TrailSyncBatcher.requestSync(entity);
        }

        for (CelestialTrailEntity.LineSegment segment : entity.cachedLineSegments) {

            model.bone.yScale = (float) (segment.length);

            Vector3f rot = angleBetween(segment.start, segment.end);
            model.bone.rotate(new Vector3f(0, rot.y, 0));
            model.bone.rotate(new Vector3f(rot.x, 0, 0));
            model.bone.translate(segment.start.subtract((float) entity.getPos().x, (float) entity.getPos().y, (float) entity.getPos().z).multiply(16).add(0, -21, 0).toVector3f());
            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), light, OverlayTexture.DEFAULT_UV);
            model.bone.resetTransform();

        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

    }

    @Override
    protected int getBlockLight(CelestialTrailEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(CelestialTrailEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getShadowRadius(CelestialTrailEntity entity) {
        return 0;
    }
}
