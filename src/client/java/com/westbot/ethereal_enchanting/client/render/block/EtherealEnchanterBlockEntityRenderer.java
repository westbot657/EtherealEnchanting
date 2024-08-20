package com.westbot.ethereal_enchanting.client.render.block;

import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class EtherealEnchanterBlockEntityRenderer implements BlockEntityRenderer<EtherealEnchanterBlockEntity> {

    public static final SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/enchanting_table_book"));;

    private final BookModel book;

    public EtherealEnchanterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(EtherealEnchanterBlockEntity etherealEnchanterBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5F, 0.75F, 0.5F);
        float g = (float) etherealEnchanterBlockEntity.ticks + f;
        matrixStack.translate(0.0F, 0.1F + MathHelper.sin(g * 0.1F) * 0.01F, 0.0F);

        float h;
        for(h = etherealEnchanterBlockEntity.bookRotation - etherealEnchanterBlockEntity.lastBookRotation; h >= 3.1415927F; h -= 6.2831855F) {
        }

        while(h < -3.1415927F) {
            h += 6.2831855F;
        }

        float k = etherealEnchanterBlockEntity.lastBookRotation + h * f;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-k));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
        float l = MathHelper.lerp(f, etherealEnchanterBlockEntity.pageAngle, etherealEnchanterBlockEntity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
        float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
        float o = MathHelper.lerp(f, etherealEnchanterBlockEntity.pageTurningSpeed, etherealEnchanterBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.renderBook(matrixStack, vertexConsumer, i, j, -1);
        matrixStack.pop();
    }
}
