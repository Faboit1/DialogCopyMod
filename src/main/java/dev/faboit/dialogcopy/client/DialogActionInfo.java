package dev.faboit.dialogcopy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * What a dialog button is wired up to do, carried from the moment vanilla builds the button
 * through to the tooltip we keep refreshed on it.
 *
 * <p>The click event is held as a supplier rather than a value because a dynamic action builds its
 * command from the dialog's inputs — so it only reads true once the player has filled them in.
 */
@Environment(EnvType.CLIENT)
public final class DialogActionInfo {
	private final Optional<DialogAction> action;
	private final Supplier<Optional<ClickEvent>> clickEvent;
	private final Optional<Text> baseTooltip;
	private final AfterAction afterAction;

	/** The tooltip text we last applied, so a tick that changes nothing costs nothing. */
	private Text appliedTooltip;

	public DialogActionInfo(Optional<DialogAction> action, Supplier<Optional<ClickEvent>> clickEvent,
			Optional<Text> baseTooltip, AfterAction afterAction) {
		this.action = action;
		this.clickEvent = clickEvent;
		this.baseTooltip = baseTooltip;
		this.afterAction = afterAction;
	}

	public Optional<DialogAction> action() {
		return action;
	}

	public Optional<ClickEvent> resolveClickEvent() {
		return clickEvent.get();
	}

	public Optional<Text> baseTooltip() {
		return baseTooltip;
	}

	public AfterAction afterAction() {
		return afterAction;
	}

	public boolean hasApplied(Text tooltip) {
		return tooltip.equals(appliedTooltip);
	}

	public void markApplied(Text tooltip) {
		this.appliedTooltip = tooltip;
	}
}
