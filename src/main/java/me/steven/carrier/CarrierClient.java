package me.steven.carrier;

import me.steven.carrier.api.CarrierPlayerExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CarrierClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (client.player != null)
            ((CarrierPlayerExtension) client.player).setCanCarry((client.player.isSneaking() && KEY_BINDING.isUnbound()) || KEY_BINDING.isPressed());
        });
    }

    public static final KeyBinding KEY_BINDING =
            KeyBindingHelper.registerKeyBinding(
                    new KeyBinding("carrier.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.carrier")
            );
}
