package dev.faboit.dialogcopy.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.RegistryOps;

/**
 * Turns the dialog the client is currently showing back into the JSON a datapack would ship.
 */
public final class DialogJsonSerializer {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private DialogJsonSerializer() {
	}

	public static String toJson(Dialog dialog) {
		JsonElement encoded = Dialog.CODEC.encodeStart(createOps(), dialog)
				.getOrThrow(error -> new IllegalStateException("Failed to encode dialog: " + error));
		return GSON.toJson(encoded);
	}

	/**
	 * Dialog bodies can reference registry content (items, for instance), so encode against the
	 * registries the server sent us when we have them, and fall back to plain JSON ops otherwise.
	 */
	private static DynamicOps<JsonElement> createOps() {
		MinecraftClient client = MinecraftClient.getInstance();

		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler != null) {
			return RegistryOps.of(JsonOps.INSTANCE, networkHandler.getRegistryManager());
		}

		if (client.world != null) {
			return RegistryOps.of(JsonOps.INSTANCE, client.world.getRegistryManager());
		}

		return JsonOps.INSTANCE;
	}
}
