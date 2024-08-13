package com.westbot.ethereal_enchanting.client.render;

import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.blocks.AltarBlock;
import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AltarBlockEntityRenderer implements BlockEntityRenderer<AltarBlockEntity> {

    public AltarBlockEntityRenderer() {
    }

    private void renderAt(ItemStack stack, Vec3d pos, float rotation, MatrixStack matrices, int overlay, VertexConsumerProvider vertexConsumers, World world, BlockEntity blockEntity) {
        matrices.push();
        matrices.translate(pos.x, pos.y, pos.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        int lightAbove = WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(null, stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, world, lightAbove, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }

    @Override
    public void render(AltarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        World world = blockEntity.getWorld();

        if (world == null) {
            return;
        }

        BlockState altarBlock = world.getBlockState(blockEntity.getPos());
        if (!altarBlock.isOf(ModBlocks.ALTAR_BLOCK)) {
            return;
        }

        Vec3d rightPos = new Vec3d(0.5, 0, 0.5).offset(altarBlock.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
        Vec3d backPos = new Vec3d(0.5, 0, 0.5).offset(altarBlock.get(AltarBlock.FACING).getOpposite(), 3);
        Vec3d leftPos = new Vec3d(0.5, 0, 0.5).offset(altarBlock.get(AltarBlock.FACING).rotateYClockwise(), 3);

        if (blockEntity.getStack(0) != ItemStack.EMPTY) {
            if (altarBlock.get(AltarBlock.FACING) == Direction.NORTH || altarBlock.get(AltarBlock.FACING) == Direction.SOUTH) {
                renderAt(blockEntity.getStack(0), new Vec3d(0.5, 1.5, 0.5), altarBlock.get(AltarBlock.FACING).asRotation(), matrices, overlay, vertexConsumers, blockEntity.getWorld(), blockEntity);
            } else {
                renderAt(blockEntity.getStack(0), new Vec3d(0.5, 1.5, 0.5), altarBlock.get(AltarBlock.FACING).getOpposite().asRotation(), matrices, overlay, vertexConsumers, blockEntity.getWorld(), blockEntity);
            }
        }

        Vec3d[][] leftStates = Util.orbitItems(blockEntity.getStack(1), blockEntity.getStack(2), blockEntity.getStack(3), blockEntity.getStack(4), blockEntity.getWorld());

        int i = 1;
        if (leftStates != null) {
            for (Vec3d[] pos_rotation : leftStates) {
                renderAt(blockEntity.getStack(i), leftPos.add(pos_rotation[0]).add(0,1.5,0), altarBlock.get(AltarBlock.FACING).rotateYCounterclockwise().asRotation() + (float) pos_rotation[1].y, matrices, overlay, vertexConsumers, blockEntity.getWorld(), blockEntity);
                i++;
            }
        }

        Vec3d[][] backStates = Util.orbitItems(blockEntity.getStack(5), blockEntity.getStack(6), blockEntity.getStack(7), blockEntity.getStack(8), blockEntity.getWorld());

        i = 5;
        if (backStates != null) {
            for (Vec3d[] pos_rotation : backStates) {
                renderAt(blockEntity.getStack(i), backPos.add(pos_rotation[0]).add(0,1.5,0), altarBlock.get(AltarBlock.FACING).getOpposite().asRotation() + (float) pos_rotation[1].y, matrices, overlay, vertexConsumers, blockEntity.getWorld(), blockEntity);
                i++;
            }
        }

        Vec3d[][] rightStates = Util.orbitItems(blockEntity.getStack(9), blockEntity.getStack(10), blockEntity.getStack(11), blockEntity.getStack(12), blockEntity.getWorld());

        i = 9;
        if (rightStates != null) {
            for (Vec3d[] pos_rotation : rightStates) {
                renderAt(blockEntity.getStack(i), rightPos.add(pos_rotation[0]).add(0,1.5,0), altarBlock.get(AltarBlock.FACING).rotateYClockwise().asRotation() + (float) pos_rotation[1].y, matrices, overlay, vertexConsumers, blockEntity.getWorld(), blockEntity);
                i++;
            }
        }

    }

}
