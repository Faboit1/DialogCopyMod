package dev.faboit.dialogcopy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 * Hangs the "what is this input" block off every widget an input control produces.
 */
@Environment(EnvType.CLIENT)
public final class DialogInputTooltips {
	private DialogInputTooltips() {
	}

	public static void apply(Widget widget, DialogInput input) {
		Text details = DialogElementTooltips.describeInput(input);

		widget.forEachChild(child -> apply(child, details));
	}

	private static void apply(ClickableWidget widget, Text details) {
		Optional<Text> existing = Tooltips.existingContent(widget);
		widget.setTooltip(Tooltip.of(DialogElementTooltips.join(existing, details)));
	}
}
