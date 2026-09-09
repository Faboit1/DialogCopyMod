package dev.faboit.dialogcopy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

/**
 * Keeps every dialog button's tooltip telling the truth about what clicking it will do.
 *
 * <p>Refreshed on screen tick because a dynamic command is built from the dialog's inputs — type a
 * different name and the command in the tooltip changes with it.
 */
@Environment(EnvType.CLIENT)
public final class DialogActionTooltips {
	private DialogActionTooltips() {
	}

	public static void refresh(Screen screen) {
		DialogWidgets.forEachClickable(screen, DialogActionTooltips::refreshWidget);
	}

	private static void refreshWidget(ClickableWidget widget) {
		if (!(widget instanceof DialogActionInfoHolder holder)) {
			return;
		}

		DialogActionInfo info = holder.dialogcopy$getActionInfo();

		if (info == null) {
			return;
		}

		Text tooltip = DialogElementTooltips.join(info.baseTooltip(), DialogElementTooltips.describeAction(info));

		if (info.hasApplied(tooltip)) {
			return;
		}

		widget.setTooltip(Tooltip.of(tooltip));
		info.markApplied(tooltip);
	}
}
