package me.steven.carrier.mixin;

import com.google.gson.JsonElement;
import me.steven.carrier.Carrier;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
    @ModifyVariable(method = "apply", at = @At("INVOKE"), argsOnly = true)
    private Map<Identifier, JsonElement> b(Map<Identifier, JsonElement> value) {
        Optional<Identifier> gloveRecipe = value.keySet().stream().filter((i) -> i.getNamespace().equals("carrier")).findFirst();
        if (gloveRecipe.isPresent() && !Carrier.CONFIG.doGlovesExist())
            value.remove(gloveRecipe.get());
        return value;
    }
}
