package io.github.ennuil.libzoomer_test;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.SpyglassMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.item.Item;
import net.minecraft.item.SpyglassItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LibZoomerTestMod implements ModInitializer, ClientModInitializer, ClientTickEvents.EndTick {
	// Michael's Zoom Instance
	private static final ZoomInstance MICHAEL_ZOOM = new ZoomInstance(
		new Identifier("libzoomer_test:zoom"),
		10.0F, new SmoothTransitionMode(),
		new SpyglassMouseModifier(),
		new SpyglassZoomOverlay(new Identifier("libzoomer_test:textures/misc/michael.png"))
	);

	// Michelle's Zoom Instance
	private static final ZoomInstance MICHELLE_ZOOM = new ZoomInstance(
		new Identifier("libzoomer_test:zoom_2"),
		3.0F, new InstantTransitionMode(),
		new CinematicCameraMouseModifier(),
		null
	);

	// Michael. He's a reimplementation of the spyglass. Tests if the spyglass can be replicated.
	private static final Item MICHAEL_ITEM = new SpyglassItem(new Item.Settings().maxCount(1));

	// Michelle. She's an implementation of a very simple zoom key. Tests if there are zoom instance conflicts and spyglass-unrelated things.
	private static final KeyBind MICHELLE_KEY = new KeyBind(
		"key.libzoomer_test.michelle",
		GLFW.GLFW_KEY_V,
		"key.libzoomer_test.category"
	);

	@Override
	public void onInitialize() {
		// Register the Michael item
		Registry.register(Registries.ITEM, new Identifier("libzoomer_test:michael"), MICHAEL_ITEM);
	}

	@Override
	public void onInitializeClient() {
		// This prints out all zoom instances registered so far and some extra info
		for (ZoomInstance instance : ZoomRegistry.getZoomInstances()) {
			System.out.println("Id: " + instance.getInstanceId() + " | Zooming: " + instance.getZoom() + " | Divisor: " + instance.getZoomDivisor());
		}

		KeyBindingHelper.registerKeyBinding(MICHELLE_KEY);
		ClientTickEvents.END_CLIENT_TICK.register(this);
	}

	@Override
	public void onEndTick(MinecraftClient client) {
		// This is how you get a spyglass-like zoom working
		if (client.player == null) return;

		MICHAEL_ZOOM.setZoom(
			client.options.getPerspective().isFirstPerson()
			&& (
				client.player.isUsingItem()
				&& client.player.getActiveItem().isOf(MICHAEL_ITEM)
			)
		);

		// And this is how you get a simple zoom button working
		MICHELLE_ZOOM.setZoom(MICHELLE_KEY.isPressed());
	}
}
