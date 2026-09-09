package dev.faboit.dialogcopy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.Consumer;

/**
 * A dialog screen keeps its buttons and inputs nested inside a container rather than flat on the
 * screen, so anything that wants to touch them all has to walk the tree.
 */
@Environment(EnvType.CLIENT)
public final class DialogWidgets {
	private DialogWidgets() {
	}

	public static void forEachClickable(Screen screen, Consumer<ClickableWidget> consumer) {
		for (Element child : screen.children()) {
			visit(child, consumer);
		}
	}

	private static void visit(Element element, Consumer<ClickableWidget> consumer) {
		if (element instanceof ClickableWidget widget) {
			consumer.accept(widget);
		}

		if (element instanceof ParentElement parent) {
			for (Element child : parent.children()) {
				visit(child, consumer);
			}
		}
	}
}
