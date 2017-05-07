package com.xcompwiz.mystcraft.client.gui.element;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElement extends Gui {

	protected Minecraft			mc;

	private GuiElement			parent;
	private List<GuiElement>	elements	= new LinkedList<GuiElement>();

	private boolean				visible		= true;
	private boolean				enabled		= true;

	private int					guiLeft;
	private int					guiTop;

	protected int				xSize;
	protected int				ySize;

	private float				zLevel;

	public GuiElement(int guiLeft, int guiTop, int xSize, int ySize) {
		this.mc = Minecraft.getMinecraft();
		this.guiLeft = guiLeft;
		this.guiTop = guiTop;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public void setPosition(int x, int y) {
		this.guiLeft = x;
		this.guiTop = y;
		this.adjustSize();
	}

	//XXX: Handle anchor points (ex. topright, bottomleft, or center of parent) 
	public final int getLeft() {
		if (parent != null) return this.guiLeft + parent.getLeft();
		return guiLeft;
	}

	public final int getTop() {
		if (parent != null) return this.guiTop + parent.getTop();
		return guiTop;
	}

	public final int getWidth() {
		return xSize;
	}

	public final int getHeight() {
		return ySize;
	}

	public void setLeft(int v) {
		this.guiLeft = v;
		this.adjustSize();
	}

	public void setTop(int v) {
		this.guiTop = v;
		this.adjustSize();
	}

	public void setWidth(int v) {
		this.xSize = v;
		this.adjustSize();
	}

	public void setHeight(int v) {
		this.ySize = v;
		this.adjustSize();
	}

	public void setZLevel(float zLevel) {
		this.zLevel = zLevel;
	}

	public float getZLevel() {
		if (this.parent != null) return this.parent.getZLevel() + this.zLevel;
		return this.zLevel;
	}

	public void focus() {
		if (this.parent == null) return;
		this.parent.bringToFront(this);
		this.parent.focus();
	}

	public void bringToFront(GuiElement element) {
		addElement(element);
	}

	public void addElement(GuiElement element) {
		if (element.parent != null) {
			element.parent.elements.remove(element);
		}
		element.parent = this;
		elements.add(element);
		element.adjustSize();
	}

	public void removeElement(GuiElement element) {
		if (element.parent == this) {
			elements.remove(element);
		}
	}

	public void clearAllElements() {
		for (GuiElement elem : elements) {
			elem.parent = null;
		}
		elements.clear();
	}

	public void addElements(Collection<GuiElement> elements) {
		for (GuiElement elem : elements) {
			elem.parent = this;
			elem.adjustSize();
		}
		this.elements.addAll(elements);
	}

	public List<GuiElement> getElements() {
		return Collections.unmodifiableList(elements);
	}

	public final GuiElement getParent() {
		return parent;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isEnabled() {
		return enabled && isVisible();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	//TODO: This function doesn't pay attention to occlusion, but is used to see if the mosue is over elements.
	//Perhaps this use case should be replaced with an onMouseEnter/Leave
	public boolean contains(int xpos, int ypos) {
		return GuiUtils.contains(xpos, ypos, getLeft(), getTop(), xSize, ySize) && (getParent() == null || getParent().contains(xpos, ypos));
	}

	//TODO: Perhaps this should be split into "expand" and "shrink", as that can be called more generally
	//Things which want to expand generally want to do so whenever their parents change size
	//Things which want to shrink generally want to do so whenever their children change size
	//Need to avoid infinite loop of calling onParentSizeChange and onChildSizeChange
	public final void adjustSize() {
		for (GuiElement elem : elements) {
			elem.adjustSize();
		}
		this._adjustSize();
	}

	public void _adjustSize() {}

	public final void onTick() {
		for (GuiElement elem : elements) {
			elem.onTick();
		}
		this._onTick();
	}

	protected void _onTick() {}

	public final boolean onKeyPress(char c, int i) {
		if (!isEnabled()) return false;
		for (GuiElement elem : elements) {
			if (elem.onKeyPress(c, i)) { return true; }
		}
		return this._onKeyPress(c, i);
	}

	protected boolean _onKeyPress(char c, int i) {
		return false;
	}

	//XXX: Change to onMouseWheel/onMouseMove
	public final void handleMouseInput() {
		if (!isEnabled()) return;
		for (GuiElement elem : elements) {
			elem.handleMouseInput();
		}
		this._handleMouseInput();
	}

	protected void _handleMouseInput() {};

	public final boolean onMouseUp(int mouseX, int mouseY, int button) {
		if (!isEnabled()) return false;
		for (GuiElement elem : elements) {
			if (elem.onMouseUp(mouseX, mouseY, button)) { return true; }
		}
		return this._onMouseUp(mouseX, mouseY, button);
	}

	protected boolean _onMouseUp(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) { return true; }
		return false;
	}

	public final boolean onMouseDown(int mouseX, int mouseY, int button) {
		if (!isEnabled()) return false;
		for (GuiElement elem : elements) {
			if (elem.onMouseDown(mouseX, mouseY, button)) { return true; }
		}
		return this._onMouseDown(mouseX, mouseY, button);
	}

	protected boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) { return true; }
		return false;
	}

	public final boolean onMouseDrag(int mouseX, int mouseY, int clicked_id, long lastclick) {
		if (!isEnabled()) return false;
		for (GuiElement elem : elements) {
			if (elem.onMouseDrag(mouseX, mouseY, clicked_id, lastclick)) { return true; }
		}
		return this._onMouseDrag(mouseX, mouseY, clicked_id, lastclick);
	}

	protected boolean _onMouseDrag(int mouseX, int mouseY, int clicked_id, long lastclick) {
		if (this.contains(mouseX, mouseY)) { return true; }
		return false;
	}

	public final void renderBackground(float f, int mouseX, int mouseY) {
		if (!isVisible()) return;
		this._renderBackground(f, mouseX, mouseY);
		for (int i = elements.size() - 1; i >= 0; --i) {
			GuiElement elem = elements.get(i);
			elem.renderBackground(f, mouseX, mouseY);
		}
		this._renderBackgroundPost(f, mouseX, mouseY);
	}

	protected void _renderBackground(float f, int mouseX, int mouseY) {}

	protected void _renderBackgroundPost(float f, int mouseX, int mouseY) {}

	public final void renderForeground(int mouseX, int mouseY) {
		if (!isVisible()) return;
		this._renderForeground(mouseX, mouseY);
		for (int i = elements.size() - 1; i >= 0; --i) {
			GuiElement elem = elements.get(i);
			elem.renderForeground(mouseX, mouseY);
		}
		this._renderForegroundPost(mouseX, mouseY);
	}

	protected void _renderForeground(int mouseX, int mouseY) {}

	protected void _renderForegroundPost(int mouseX, int mouseY) {}

	//TODO: Allow the element to change the font renderer in use (for item's getFontRenderer)
	public final List<String> getTooltipInfo() {
		if (!isVisible()) return null;
		for (GuiElement elem : elements) {
			List<String> list = elem.getTooltipInfo();
			if (list != null) { return list; }
		}
		return this._getTooltipInfo();
	}

	protected List<String> _getTooltipInfo() {
		return null;
	}
}
