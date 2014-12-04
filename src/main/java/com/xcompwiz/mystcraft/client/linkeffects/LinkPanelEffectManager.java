package com.xcompwiz.mystcraft.client.linkeffects;

import java.util.ArrayList;
import java.util.Collection;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;

public class LinkPanelEffectManager {

	private static Collection<ILinkPanelEffect>	effects	= new ArrayList<ILinkPanelEffect>();

	public static void registerEffect(ILinkPanelEffect effect) {
		effects.add(effect);
	}

	public static Collection<ILinkPanelEffect> getEffects() {
		return effects;
	}
}
