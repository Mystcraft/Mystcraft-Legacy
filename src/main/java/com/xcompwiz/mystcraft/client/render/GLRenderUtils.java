package com.xcompwiz.mystcraft.client.render;

import org.lwjgl.opengl.GL11;

public final class GLRenderUtils {

	public static class GLRenderState {
		public boolean	GL_ALPHA_TEST;
		public boolean	GL_BLEND;
		public boolean	GL_COLOR_LOGIC_OP;
		public boolean	GL_COLOR_MATERIAL;
		public boolean	GL_CULL_FACE;
		public boolean	GL_DEPTH_TEST;
		public boolean	GL_FOG;
		public boolean	GL_LIGHTING;
		public boolean	GL_TEXTURE_2D;
		public boolean	checked;
		public int		SmoothMode;
		public int		AlphaFunc;
		public int		DepthFunc;
	}

	public static final GLRenderState	state	= new GLRenderState();

	public static void grabState() {
		state.GL_ALPHA_TEST = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);
		state.GL_BLEND = GL11.glGetBoolean(GL11.GL_BLEND);
		state.GL_COLOR_LOGIC_OP = GL11.glGetBoolean(GL11.GL_COLOR_LOGIC_OP);
		state.GL_COLOR_MATERIAL = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
		state.GL_CULL_FACE = GL11.glGetBoolean(GL11.GL_CULL_FACE);
		state.GL_DEPTH_TEST = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		state.GL_FOG = GL11.glGetBoolean(GL11.GL_FOG);
		state.GL_LIGHTING = GL11.glGetBoolean(GL11.GL_LIGHTING);
		state.GL_TEXTURE_2D = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);

		state.SmoothMode = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
		state.AlphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		state.DepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);

		state.checked = true;
	}

	public static void revertState() {
		setFlag(state.GL_ALPHA_TEST, GL11.GL_ALPHA_TEST);
		setFlag(state.GL_BLEND, GL11.GL_BLEND);
		setFlag(state.GL_COLOR_LOGIC_OP, GL11.GL_COLOR_LOGIC_OP);
		setFlag(state.GL_COLOR_MATERIAL, GL11.GL_COLOR_MATERIAL);
		setFlag(state.GL_CULL_FACE, GL11.GL_CULL_FACE);
		setFlag(state.GL_DEPTH_TEST, GL11.GL_DEPTH_TEST);
		setFlag(state.GL_FOG, GL11.GL_FOG);
		setFlag(state.GL_LIGHTING, GL11.GL_LIGHTING);
		setFlag(state.GL_TEXTURE_2D, GL11.GL_TEXTURE_2D);

		state.checked = false;
	}

	public static void setFlag(boolean state, int flag) {
		if (state) {
			GL11.glEnable(flag);
		} else {
			GL11.glDisable(flag);
		}
	}
}
