package dev.faboit.dialogcopy.client;

import dev.faboit.dialogcopy.mixin.ParsedTemplateAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.action.DynamicRunCommandDialogAction;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

/**
 * Turns a dialog's underlying data into the grey detail block we hang under each element's tooltip.
 */
@Environment(EnvType.CLIENT)
public final class DialogElementTooltips {
	private DialogElementTooltips() {
	}

	/** The detail block for an action button: what clicking it will actually do, right now. */
	public static Text describeAction(DialogActionInfo info) {
		MutableText lines = Text.empty();

		Optional<ClickEvent> clickEvent = info.resolveClickEvent();

		if (clickEvent.isEmpty()) {
			append(lines, entry("dialogcopy.info.action.none", null));
		} else {
			append(lines, describeClickEvent(clickEvent.get()));
		}

		info.action().flatMap(DialogElementTooltips::describeTemplate).ifPresent(template -> append(lines, template));

		append(lines, entry("dialogcopy.info.after", Text.literal(info.afterAction().asString())));
		return lines;
	}

	private static Text describeClickEvent(ClickEvent clickEvent) {
		if (clickEvent instanceof ClickEvent.RunCommand runCommand) {
			return entry("dialogcopy.info.action.run_command", command(runCommand.command()));
		}

		if (clickEvent instanceof ClickEvent.SuggestCommand suggestCommand) {
			return entry("dialogcopy.info.action.suggest_command", command(suggestCommand.command()));
		}

		if (clickEvent instanceof ClickEvent.OpenUrl openUrl) {
			return entry("dialogcopy.info.action.open_url", value(openUrl.uri().toString()));
		}

		if (clickEvent instanceof ClickEvent.OpenFile openFile) {
			return entry("dialogcopy.info.action.open_file", value(openFile.path()));
		}

		if (clickEvent instanceof ClickEvent.CopyToClipboard copyToClipboard) {
			return entry("dialogcopy.info.action.copy_to_clipboard", value(copyToClipboard.value()));
		}

		if (clickEvent instanceof ClickEvent.ChangePage changePage) {
			return entry("dialogcopy.info.action.change_page", value(String.valueOf(changePage.page())));
		}

		if (clickEvent instanceof ClickEvent.ShowDialog showDialog) {
			String target = showDialog.dialog().getKey()
					.map(key -> key.getValue().toString())
					.orElseGet(() -> Text.translatable("dialogcopy.info.inline_dialog").getString());
			return entry("dialogcopy.info.action.show_dialog", value(target));
		}

		if (clickEvent instanceof ClickEvent.Custom custom) {
			MutableText described = entry("dialogcopy.info.action.custom", value(custom.id().toString())).copy();
			custom.payload().ifPresent(payload ->
					append(described, entry("dialogcopy.info.payload", value(payload.toString()))));
			return described;
		}

		return entry("dialogcopy.info.action.unknown", value(clickEvent.getClass().getSimpleName()));
	}

	/**
	 * Dynamic actions build themselves from the inputs, so show the raw template next to the
	 * resolved value — it tells an author which inputs feed the command.
	 */
	private static Optional<Text> describeTemplate(DialogAction action) {
		if (action instanceof DynamicRunCommandDialogAction dynamicRunCommand) {
			String raw = ((ParsedTemplateAccessor) (Object) dynamicRunCommand.template()).dialogcopy$getRaw();
			return Optional.of(entry("dialogcopy.info.template", command(raw)));
		}

		return Optional.empty();
	}

	/** The detail block for an input widget: its key and the shape of the value it produces. */
	public static Text describeInput(DialogInput input) {
		MutableText lines = Text.empty();
		append(lines, entry("dialogcopy.info.input.key", value(input.key())));

		InputControl control = input.control();

		if (control instanceof TextInputControl text) {
			append(lines, entry("dialogcopy.info.input.type",
					Text.translatable(text.multiline().isPresent()
							? "dialogcopy.info.input.type.text_multiline"
							: "dialogcopy.info.input.type.text")));
			append(lines, entry("dialogcopy.info.input.initial", value(quote(text.initial()))));
			append(lines, entry("dialogcopy.info.input.max_length", value(String.valueOf(text.maxLength()))));
		} else if (control instanceof BooleanInputControl bool) {
			append(lines, entry("dialogcopy.info.input.type", Text.translatable("dialogcopy.info.input.type.boolean")));
			append(lines, entry("dialogcopy.info.input.initial", value(String.valueOf(bool.initial()))));
			append(lines, entry("dialogcopy.info.input.on_true", value(quote(bool.onTrue()))));
			append(lines, entry("dialogcopy.info.input.on_false", value(quote(bool.onFalse()))));
		} else if (control instanceof SingleOptionInputControl option) {
			append(lines, entry("dialogcopy.info.input.type", Text.translatable("dialogcopy.info.input.type.option")));
			append(lines, entry("dialogcopy.info.input.options", value(optionIds(option))));
			option.getInitialEntry().ifPresent(initial ->
					append(lines, entry("dialogcopy.info.input.initial", value(quote(initial.id())))));
		} else if (control instanceof NumberRangeInputControl range) {
			append(lines, entry("dialogcopy.info.input.type", Text.translatable("dialogcopy.info.input.type.range")));
			append(lines, entry("dialogcopy.info.input.range",
					value(trim(range.rangeInfo().start()) + " – " + trim(range.rangeInfo().end()))));
			range.rangeInfo().step().ifPresent(step ->
					append(lines, entry("dialogcopy.info.input.step", value(trim(step)))));
			// No explicit initial means vanilla starts the slider at the midpoint, so say nothing.
			range.rangeInfo().initial().ifPresent(initial ->
					append(lines, entry("dialogcopy.info.input.initial", value(trim(initial)))));
		} else {
			append(lines, entry("dialogcopy.info.input.type", value(control.getClass().getSimpleName())));
		}

		return lines;
	}

	private static String optionIds(SingleOptionInputControl option) {
		List<String> ids = option.entries().stream().map(SingleOptionInputControl.Entry::id).toList();
		return String.join(", ", ids);
	}

	/** Hangs a detail block under whatever tooltip the dialog author already wrote. */
	public static Text join(Optional<Text> baseTooltip, Text details) {
		return baseTooltip
				.map(base -> (Text) Text.empty().append(base).append("\n\n").append(details))
				.orElse(details);
	}

	private static Text entry(String labelKey, Text detail) {
		MutableText line = Text.translatable(labelKey).formatted(Formatting.GRAY);
		return detail == null ? line : line.append(Text.literal(" ")).append(detail);
	}

	private static Text command(String raw) {
		return Text.literal(raw).formatted(Formatting.YELLOW);
	}

	private static Text value(String raw) {
		return Text.literal(raw).formatted(Formatting.WHITE);
	}

	private static void append(MutableText lines, Text line) {
		if (!lines.getSiblings().isEmpty()) {
			lines.append("\n");
		}

		lines.append(line);
	}

	private static String quote(String raw) {
		return '"' + raw + '"';
	}

	/** Floats in dialogs are usually whole numbers; 5 reads better than 5.0. */
	private static String trim(float value) {
		return value == Math.floor(value) && !Float.isInfinite(value)
				? String.valueOf((long) value)
				: String.valueOf(value);
	}
}
