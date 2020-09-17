package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriablePig extends EntityCarriable<PigEntity>  {

    public static final Identifier TYPE = new Identifier("carrier", "pig");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriablePig());
    private static PigEntity dummyPig;
    private static PigEntityRenderer renderer;

    private CarriablePig() {
        super(TYPE, EntityType.PIG);
    }

    @Override
    public PigEntity getEntity() {
        if (dummyPig == null)
            dummyPig = new PigEntity(EntityType.PIG, MinecraftClient.getInstance().world);
        return dummyPig;
    }

    @Override
    public EntityRenderer<PigEntity> getEntityRenderer() {
        if (renderer == null)
            renderer = new PigEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return renderer;
    }


    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }

}
