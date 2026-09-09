package dev.faboit.dialogcopy.mixin;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** A tooltip keeps its text private; we need it to build an extended copy. */
@Mixin(Tooltip.class)
public interface TooltipAccessor {
	@Accessor("content")
	Text dialogcopy$getContent();
}
