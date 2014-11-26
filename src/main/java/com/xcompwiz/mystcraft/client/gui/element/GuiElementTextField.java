package com.xcompwiz.mystcraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.opengl.GL11;

public class GuiElementTextField extends GuiElement {
	public interface IGuiOnTextChange {
		void onTextChange(GuiElementTextField caller, String text);
	}

	public interface IGuiTextProvider {
		String getText(GuiElementTextField caller);
	}

	/**
	 * Have the font renderer from GuiScreen to render the textbox text into the screen.
	 */
	private final FontRenderer	fontRenderer;
	private IGuiOnTextChange	textchangehandler;
	private IGuiTextProvider	textprovider;
	private String				id;

	/** Have the current text being edited on the textbox. */
	private int					cursorCounter;
	private int					maxLength				= 256;
	private boolean				enableBackgroundDrawing	= true;

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen.
	 */
	private boolean				canLoseFocus			= true;

	/**
	 * If this value is true along isEnabled, keyTyped will process the keys.
	 */
	private boolean				isFocused				= false;

	/**
	 * Setting this value prevents the text from being edited.
	 */
	private boolean				readonly				= false;

	/**
	 * The current character index that should be used as start of the rendered text.
	 */
	private int					lineScrollOffset;
	private int					cursorPosition;
	private String				override;
	private long				lasttyped;

	/** other selection position, maybe the same as the cursor */
	private int					selectionEnd;
	private int					enabledColor			= 14737632;
	private int					disabledColor			= 7368816;

	public GuiElementTextField(IGuiTextProvider textprovider, IGuiOnTextChange changehandler, String id, int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.id = id;
		this.fontRenderer = mc.fontRenderer;
		this.textprovider = textprovider;
		this.textchangehandler = changehandler;
	}

	public String getId() {
		return id;
	}

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	public void setCanLoseFocus(boolean b) {
		this.canLoseFocus = b;
	}

	public void setReadOnly(boolean b) {
		this.readonly = b;
	}

	public boolean isReadOnly() {
		return this.readonly;
	}

	public void setMaxLength(int v) {
		this.maxLength = v;
	}

	/**
	 * Increments the cursor counter
	 */
	@Override
	public void _onTick() {
		++this.cursorCounter;
	}

	/**
	 * Sets the text of the textbox.
	 */
	private void setText(String text) {
		if (this.isReadOnly()) return;
		if (text.length() > this.maxLength) {
			text = text.substring(0, this.maxLength);
		}
		this.override = text;
		this.lasttyped = Minecraft.getSystemTime();
		if (this.textchangehandler != null) {
			this.textchangehandler.onTextChange(this, text);
		}
	}

	/**
	 * Returns the text being edited on the textbox.
	 */
	public String getText() {
		String text = null;
		if (override != null) {
			if (this.lasttyped + 1000L > Minecraft.getSystemTime()) {
				//if (this.isFocused) {
				text = override;
			} else {
				override = null;
			}
		}
		if (text == null && textprovider != null) {
			text = this.textprovider.getText(this);
		}
		if (text == null) {
			text = "";
		}
		if (this.cursorPosition > text.length()) {
			setCursorPosition(text.length());
		}

		return text;
	}

	/**
	 * @return returns the text between the cursor and selectionEnd
	 */
	public String getSelectedtext() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.getText().substring(i, j);
	}

	/**
	 * replaces selected text, or inserts text at the position on the cursor
	 */
	public void writeText(String par1Str) {
		if (this.isReadOnly()) return;
		String s1 = "";
		String s2 = ChatAllowedCharacters.filerAllowedCharacters(par1Str);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = this.maxLength - Math.min(this.getText().length(), this.maxLength) - (i - this.selectionEnd);

		if (this.getText().length() > 0) {
			s1 = s1 + this.getText().substring(0, i);
		}

		int l;

		if (k < s2.length()) {
			s1 = s1 + s2.substring(0, k);
			l = k;
		} else {
			s1 = s1 + s2;
			l = s2.length();
		}

		if (this.getText().length() > 0 && j < this.getText().length()) {
			s1 = s1 + this.getText().substring(j);
		}

		this.setText(s1);
		this.moveCursorBy(i - this.selectionEnd + l);
	}

	/**
	 * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of the cursor.
	 */
	public void deleteWords(int par1) {
		if (this.getText().length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
			}
		}
	}

	/**
	 * delete the selected text, otherwise deletes characters from either side of the cursor.
	 * @param par1 Number of characters to delete. Negative numbers delete to the left of the cursor.
	 */
	public void deleteFromCursor(int par1) {
		if (this.isReadOnly()) return;
		if (this.getText().length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = par1 < 0;
				int j = flag ? this.cursorPosition + par1 : this.cursorPosition;
				int k = flag ? this.cursorPosition : this.cursorPosition + par1;
				String s = "";

				if (j >= 0) {
					s = this.getText().substring(0, j);
				}

				if (k < this.getText().length()) {
					s = s + this.getText().substring(k);
				}

				this.setText(s);

				if (flag) {
					this.moveCursorBy(par1);
				}
			}
		}
	}

	/**
	 * see @getNthNextWordFromPos() params: N, position
	 */
	public int getNthWordFromCursor(int par1) {
		return this.getNthWordFromPos(par1, this.getCursorPosition());
	}

	/**
	 * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
	 */
	public int getNthWordFromPos(int par1, int par2) {
		return this.func_73798_a(par1, this.getCursorPosition(), true);
	}

	public int func_73798_a(int par1, int par2, boolean par3) {
		int k = par2;
		boolean flag1 = par1 < 0;
		int l = Math.abs(par1);

		for (int i1 = 0; i1 < l; ++i1) {
			if (flag1) {
				while (par3 && k > 0 && this.getText().charAt(k - 1) == 32) {
					--k;
				}

				while (k > 0 && this.getText().charAt(k - 1) != 32) {
					--k;
				}
			} else {
				int j1 = this.getText().length();
				k = this.getText().indexOf(32, k);

				if (k == -1) {
					k = j1;
				} else {
					while (par3 && k < j1 && this.getText().charAt(k) == 32) {
						++k;
					}
				}
			}
		}

		return k;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the selection
	 */
	public void moveCursorBy(int par1) {
		this.setCursorPosition(this.selectionEnd + par1);
	}

	/**
	 * sets the position of the cursor to the provided index
	 */
	public void setCursorPosition(int par1) {
		this.cursorPosition = par1;
		int j = this.getText().length();

		if (this.cursorPosition < 0) {
			this.cursorPosition = 0;
		}

		if (this.cursorPosition > j) {
			this.cursorPosition = j;
		}

		this.setSelectionPos(this.cursorPosition);
	}

	/**
	 * sets the cursors position to the beginning
	 */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	/**
	 * sets the cursors position to after the text
	 */
	public void setCursorPositionEnd() {
		this.setCursorPosition(this.getText().length());
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	@Override
	public boolean _onKeyPress(char c, int i) {
		if (this.isEnabled() && this.isFocused) {
			switch (c) {
			case 1:
				this.setCursorPositionEnd();
				this.setSelectionPos(0);
				return true;
			case 3:
				GuiScreen.setClipboardString(this.getSelectedtext());
				return true;
			case 22:
				this.writeText(GuiScreen.getClipboardString());
				return true;
			case 24:
				GuiScreen.setClipboardString(this.getSelectedtext());
				this.writeText("");
				return true;
			default:
				switch (i) {
				case 14:
					if (GuiScreen.isCtrlKeyDown()) {
						this.deleteWords(-1);
					} else {
						this.deleteFromCursor(-1);
					}
					return true;
				case 199:
					if (GuiScreen.isShiftKeyDown()) {
						this.setSelectionPos(0);
					} else {
						this.setCursorPositionZero();
					}
					return true;
				case 203:
					if (GuiScreen.isShiftKeyDown()) {
						if (GuiScreen.isCtrlKeyDown()) {
							this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
						} else {
							this.setSelectionPos(this.getSelectionEnd() - 1);
						}
					} else if (GuiScreen.isCtrlKeyDown()) {
						this.setCursorPosition(this.getNthWordFromCursor(-1));
					} else {
						this.moveCursorBy(-1);
					}
					return true;
				case 205:
					if (GuiScreen.isShiftKeyDown()) {
						if (GuiScreen.isCtrlKeyDown()) {
							this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
						} else {
							this.setSelectionPos(this.getSelectionEnd() + 1);
						}
					} else if (GuiScreen.isCtrlKeyDown()) {
						this.setCursorPosition(this.getNthWordFromCursor(1));
					} else {
						this.moveCursorBy(1);
					}
					return true;
				case 207:
					if (GuiScreen.isShiftKeyDown()) {
						this.setSelectionPos(this.getText().length());
					} else {
						this.setCursorPositionEnd();
					}
					return true;
				case 211:
					if (GuiScreen.isCtrlKeyDown()) {
						this.deleteWords(1);
					} else {
						this.deleteFromCursor(1);
					}
					return true;
				default:
					if (ChatAllowedCharacters.isAllowedCharacter(c)) {
						this.writeText(Character.toString(c));
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Args: x, y, buttonClicked
	 * @return
	 */
	@Override
	public boolean _onMouseDown(int mouseX, int mouseY, int button) {
		boolean flag = this.contains(mouseX, mouseY);

		if (this.canLoseFocus) {
			this.setFocused(this.isEnabled() && flag);
		}

		if (this.isFocused && button == 0) {
			int l = mouseX - this.getLeft();

			if (this.enableBackgroundDrawing) {
				l -= 4;
			}

			String s = this.fontRenderer.trimStringToWidth(this.getText().substring(this.lineScrollOffset), this.getInnerWidth());
			this.setCursorPosition(this.fontRenderer.trimStringToWidth(s, l).length() + this.lineScrollOffset);
		}
		if (this.isFocused && button == 1) {
			this.setText("");
		}
		return flag;
	}

	/**
	 * Draws the textbox
	 */
	@Override
	//FIXME: Renders border outside its bounds
	public void _renderBackground(float f, int mouseX, int mouseY) {
		if (!this.isVisible()) return;
		int guiLeft = getLeft();
		int guiTop = getTop();

		if (this.getEnableBackgroundDrawing()) {
			drawRect(guiLeft, guiTop, guiLeft + this.xSize, guiTop + this.ySize, -6250336);
			drawRect(guiLeft + 1, guiTop + 1, guiLeft + this.xSize - 1, guiTop + this.ySize - 1, -16777216);
		}

		int i = (this.isEnabled() && !this.isReadOnly()) ? this.enabledColor : this.disabledColor;
			int j = this.cursorPosition - this.lineScrollOffset;
			int k = this.selectionEnd - this.lineScrollOffset;
		String s = this.fontRenderer.trimStringToWidth(this.getText().substring(this.lineScrollOffset), this.getInnerWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
		int l = this.enableBackgroundDrawing ? guiLeft + 4 : guiLeft;
		int i1 = this.enableBackgroundDrawing ? guiTop + (this.ySize - 8) / 2 : guiTop;
			int j1 = l;

			if (k > s.length()) {
				k = s.length();
			}

			if (s.length() > 0) {
				String s1 = flag ? s.substring(0, j) : s;
				j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i);
			}

			boolean flag2 = this.cursorPosition < this.getText().length();
			int k1 = j1;

			if (!flag) {
				k1 = j > 0 ? l + this.xSize : l;
			} else if (flag2) {
				k1 = j1 - 1;
				--j1;
			}

			if (s.length() > 0 && flag && j < s.length()) {
				this.fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i);
			}

			if (flag1) {
				if (flag2) {
					Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
				} else {
					this.fontRenderer.drawStringWithShadow("_", k1, i1, i);
				}
			}

			if (k != j) {
				int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
				this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
			}
		}

	/**
	 * draws the vertical line cursor in the textbox
	 */
	private void drawCursorVertical(int par1, int par2, int par3, int par4) {
		int i1;

		if (par1 < par3) {
			i1 = par1;
			par1 = par3;
			par3 = i1;
		}

		if (par2 < par4) {
			i1 = par2;
			par2 = par4;
			par4 = i1;
		}

		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		tessellator.startDrawingQuads();
		tessellator.addVertex(par1, par4, 0.0D);
		tessellator.addVertex(par3, par4, 0.0D);
		tessellator.addVertex(par3, par2, 0.0D);
		tessellator.addVertex(par1, par2, 0.0D);
		tessellator.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/**
	 * get enable drawing background and outline
	 */
	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	/**
	 * enable drawing background and outline
	 */
	public void setEnableBackgroundDrawing(boolean par1) {
		this.enableBackgroundDrawing = par1;
	}

	/**
	 * Sets the text colour for this textbox (disabled text will not use this colour)
	 */
	public void setTextColor(int par1) {
		this.enabledColor = par1;
	}

	public void setDisabledTextColour(int par1) {
		this.disabledColor = par1;
	}

	/**
	 * setter for the focused field
	 */
	public void setFocused(boolean par1) {
		if (par1 && !this.isFocused) {
			this.cursorCounter = 0;
		}

		this.isFocused = par1;
	}

	/**
	 * getter for the focused field
	 */
	public boolean isFocused() {
		return this.isFocused;
	}

	/**
	 * the side of the selection that is not the cursor, maye be the same as the cursor
	 */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/**
	 * returns the width of the textbox depending on if the the box is enabled
	 */
	public int getInnerWidth() {
		return this.getEnableBackgroundDrawing() ? this.xSize - 8 : this.xSize;
	}

	/**
	 * Sets the position of the selection anchor (i.e. position the selection was started at)
	 */
	public void setSelectionPos(int par1) {
		int j = this.getText().length();

		if (par1 > j) {
			par1 = j;
		}

		if (par1 < 0) {
			par1 = 0;
		}

		this.selectionEnd = par1;

		if (this.fontRenderer != null) {
			if (this.lineScrollOffset > j) {
				this.lineScrollOffset = j;
			}

			int k = this.getInnerWidth();
			String s = this.fontRenderer.trimStringToWidth(this.getText().substring(this.lineScrollOffset), k);
			int l = s.length() + this.lineScrollOffset;

			if (par1 == this.lineScrollOffset) {
				this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.getText(), k, true).length();
			}

			if (par1 > l) {
				this.lineScrollOffset += par1 - l;
			} else if (par1 <= this.lineScrollOffset) {
				this.lineScrollOffset -= this.lineScrollOffset - par1;
			}

			if (this.lineScrollOffset < 0) {
				this.lineScrollOffset = 0;
			}

			if (this.lineScrollOffset > j) {
				this.lineScrollOffset = j;
			}
		}
	}

}
