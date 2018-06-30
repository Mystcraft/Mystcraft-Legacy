package com.xcompwiz.mystcraft.client.shaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.integration.lookingglass.DynamicLinkPanelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class ShaderUtils {

	public static int createShader(ResourceLocation resource, int shaderType) throws Exception {
		int shader = 0;
		try {

			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

			if (shader == 0) return 0;
			ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(resource));
			ARBShaderObjects.glCompileShaderARB(shader);

			if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

			return shader;
		} catch (Exception exc) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw exc;
		}
	}

	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, GL20.GL_INFO_LOG_LENGTH));
	}

	private static String readFileAsString(ResourceLocation r) throws Exception {
		StringBuilder source = new StringBuilder();

		InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(r).getInputStream();
		Exception exception = null;

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			Exception innerExc = null;
			try {
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception exc) {
				exception = exc;
			} finally {
				try {
					reader.close();
				} catch (Exception exc) {
					innerExc = exc;
				}
			}

			if (innerExc != null) throw innerExc;
		} catch (Exception exc) {
			exception = exc;
		} finally {
			try {
				in.close();
			} catch (Exception exc) {
				if (exception == null) exception = exc;
				else exc.printStackTrace();
			}

			if (exception != null) throw exception;
		}

		return source.toString();
	}

	public static void registerShaders() {
		if (OpenGlHelper.shadersSupported) {
			ResourceLocation linkingpanel = (new ResourceLocation(MystObjects.MystcraftModId, "shaders/linkingpanel.frag"));
			ResourceLocation vlinkingpanel = (new ResourceLocation(MystObjects.MystcraftModId, "shaders/linkingpanel.vert"));

			try {
				DynamicLinkPanelRenderer.vertexARB = createShader(vlinkingpanel, GL20.GL_VERTEX_SHADER);
				DynamicLinkPanelRenderer.fragmentARB = createShader(linkingpanel, GL20.GL_FRAGMENT_SHADER);
			} catch (Exception e) {}

			DynamicLinkPanelRenderer.shaderARB = ARBShaderObjects.glCreateProgramObjectARB();

			if (DynamicLinkPanelRenderer.shaderARB == 0 || DynamicLinkPanelRenderer.fragmentARB == 0) return;

			ARBShaderObjects.glAttachObjectARB(DynamicLinkPanelRenderer.shaderARB, DynamicLinkPanelRenderer.vertexARB);
			ARBShaderObjects.glAttachObjectARB(DynamicLinkPanelRenderer.shaderARB, DynamicLinkPanelRenderer.fragmentARB);

			ARBShaderObjects.glLinkProgramARB(DynamicLinkPanelRenderer.shaderARB);
			if (ARBShaderObjects.glGetObjectParameteriARB(DynamicLinkPanelRenderer.shaderARB, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) { return; }

			ARBShaderObjects.glValidateProgramARB(DynamicLinkPanelRenderer.shaderARB);
			if (ARBShaderObjects.glGetObjectParameteriARB(DynamicLinkPanelRenderer.shaderARB, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) { return; }

			DynamicLinkPanelRenderer.textureLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "u_texture");
			DynamicLinkPanelRenderer.damageLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "damage");
			DynamicLinkPanelRenderer.resLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "iResolution");
			DynamicLinkPanelRenderer.timeLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "iGlobalTime");
			DynamicLinkPanelRenderer.waveScaleLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "dWave");
			DynamicLinkPanelRenderer.colorScaleLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "dColor");
			DynamicLinkPanelRenderer.linkColorLoc = ARBShaderObjects.glGetUniformLocationARB(DynamicLinkPanelRenderer.shaderARB, "linkColor");
		}
	}
}
