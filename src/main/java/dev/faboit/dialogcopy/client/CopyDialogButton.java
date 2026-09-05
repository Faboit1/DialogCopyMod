package dev.faboit.dialogcopy.client;

import dev.faboit.dialogcopy.DialogCopyMod;
import dev.faboit.dialogcopy.mixin.DialogScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;

/**
 * The little "Copy JSON" button pinned to the top right corner of every dialog screen.
 */
@Environment(EnvType.CLIENT)
public final class CopyDialogButton {
	private static final Text LABEL = Text.translatable("dialogcopy.button.copy");
	private static final Text TOOLTIP = Text.translatable("dialogcopy.button.copy.tooltip");
	private static final int MARGIN = 4;
	private static final int HEIGHT = 16;
	private static final int GAP = 4;

	private CopyDialogButton() {
	}

	public static void addTo(Screen screen) {
		MinecraftClient client = MinecraftClient.getInstance();
		int width = client.textRenderer.getWidth(LABEL) + 10;

		ButtonWidget button = ButtonWidget.builder(LABEL, ignored -> copy(screen))
				.dimensions(0, MARGIN, width, HEIGHT)
				.tooltip(Tooltip.of(TOOLTIP))
				.build();

		button.setX(leftEdgeFor(screen, width));
		Screens.getButtons(screen).add(button);
	}

	/**
	 * Sit in the top right corner, unless vanilla's warning button is already sitting where we
	 * want to be — in that case slide left of it so both stay clickable.
	 */
	private static int leftEdgeFor(Screen screen, int width) {
		int x = screen.width - width - MARGIN;

		ButtonWidget warningButton = ((DialogScreenAccessor) screen).dialogcopy$getWarningButton();
		if (warningButton != null && warningButton.visible && overlaps(warningButton, x, width)) {
			x = warningButton.getX() - width - GAP;
		}

		return Math.max(MARGIN, x);
	}

	private static boolean overlaps(ButtonWidget warningButton, int x, int width) {
		boolean horizontally = x < warningButton.getX() + warningButton.getWidth() + GAP
				&& warningButton.getX() < x + width + GAP;
		boolean vertically = MARGIN < warningButton.getY() + warningButton.getHeight()
				&& warningButton.getY() < MARGIN + HEIGHT;
		return horizontally && vertically;
	}

	private static void copy(Screen screen) {
		MinecraftClient client = MinecraftClient.getInstance();
		Dialog dialog = ((DialogScreenAccessor) screen).dialogcopy$getDialog();

		if (dialog == null) {
			toast(client, "dialogcopy.toast.failed", Text.translatable("dialogcopy.toast.no_dialog"));
			return;
		}

		try {
			String json = DialogJsonSerializer.toJson(dialog);
			client.keyboard.setClipboard(json);
			toast(client, "dialogcopy.toast.copied", Text.translatable("dialogcopy.toast.copied.description"));
		} catch (Exception exception) {
			DialogCopyMod.LOGGER.error("Could not copy dialog JSON to the clipboard", exception);
			toast(client, "dialogcopy.toast.failed", Text.literal(String.valueOf(exception.getMessage())));
		}
	}

	private static void toast(MinecraftClient client, String titleKey, Text description) {
		SystemToast.add(client.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION,
				Text.translatable(titleKey), description);
	}
}
