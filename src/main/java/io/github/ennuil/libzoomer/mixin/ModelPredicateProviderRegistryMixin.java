package io.github.ennuil.libzoomer.mixin;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ennuil.libzoomer.impl.SpyglassHelper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderRegistryMixin {
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void addScopingPredicateToModdedSpyglasses(CallbackInfo ci) {
		ModelPredicateProviderRegistry.register(new Identifier("libzoomer", "scoping"), (stack, clientWorld, entity, i) ->
			entity != null
				&& entity.isUsingItem()
				&& entity.getActiveItem() == stack
				&& ClientTags.isInWithLocalFallback(SpyglassHelper.SPYGLASSES, entity.getActiveItem().getItem())
				? 1.0F
				: 0.0F
		);
	}
}
