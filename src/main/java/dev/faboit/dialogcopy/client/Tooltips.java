package dev.faboit.dialogcopy.client;

import dev.faboit.dialogcopy.mixin.ClickableWidgetAccessor;
import dev.faboit.dialogcopy.mixin.TooltipAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class Tooltips {
	private Tooltips() {
	}

	/** The tooltip text a widget already has, if any. */
	public static Optional<Text> existingContent(ClickableWidget widget) {
		TooltipState state = ((ClickableWidgetAccessor) widget).dialogcopy$getTooltipState();

		if (state == null) {
			return Optional.empty();
		}

		Tooltip tooltip = state.getTooltip();
		return tooltip == null ? Optional.empty() : Optional.ofNullable(((TooltipAccessor) tooltip).dialogcopy$getContent());
	}
}
