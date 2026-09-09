package dev.faboit.dialogcopy.client;

import dev.faboit.dialogcopy.DialogCopyMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.dialog.DialogScreen;

@Environment(EnvType.CLIENT)
public class DialogCopyModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof DialogScreen) {
				CopyDialogButton.addTo(screen);

				DialogActionTooltips.refresh(screen);
				ScreenEvents.afterTick(screen).register(DialogActionTooltips::refresh);
			}
		});

		DialogCopyMod.LOGGER.info("DialogCopyMod ready — dialogs get a 'Copy JSON' button and element tooltips.");
	}
}
