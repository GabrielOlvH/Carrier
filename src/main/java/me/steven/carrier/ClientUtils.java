package me.steven.carrier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class ClientUtils {
    public static BlockEntityRendererFactory.Context defaultBlockCtx() {
        MinecraftClient client = MinecraftClient.getInstance();
        return new BlockEntityRendererFactory.Context(client.getBlockEntityRenderDispatcher(), client.getBlockRenderManager(), client.getEntityModelLoader(), client.textRenderer);
    }

    public static EntityRendererFactory.Context defaultEntityCtx() {
        MinecraftClient client = MinecraftClient.getInstance();
        return new EntityRendererFactory.Context(client.getEntityRenderDispatcher(), client.getItemRenderer(), client.getResourceManager(), client.getEntityModelLoader(), client.textRenderer);
    }
}
