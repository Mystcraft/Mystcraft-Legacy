package com.xcompwiz.mystcraft.data;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinkbook;
import com.xcompwiz.mystcraft.page.Page;

public class AchievementsMyst {

	private static int			symbol_id	= 5000;
	private static int			agebook_id	= 5001;
	private static int			linkbook_id	= 5002;
	private static int			quinn_id	= 5003;
	private static int			write_id	= 5004;

	public static Achievement	symbol;
	public static Achievement	write;
	public static Achievement	agebook;
	public static Achievement	linkbook;
	public static Achievement	quinn;

	public static void loadConfigs(MystConfig config) {
		symbol_id = config.get("achievements", "achievement.symbol", symbol_id).getInt();
		agebook_id = config.get("achievements", "achievement.agebook", agebook_id).getInt();
		linkbook_id = config.get("achievements", "achievement.linkbook", linkbook_id).getInt();
		quinn_id = config.get("achievements", "achievement.quinn", quinn_id).getInt();
		write_id = config.get("achievements", "achievement.write", write_id).getInt();
	}

	public static void init() {
		symbol = new Achievement("achievement.myst.symbol", "myst.symbol", 0, -2, Page.createSymbolPage("BioConSingle"), null).registerStat();
		write = new Achievement("achievement.myst.write", "myst.write", 0, 0, Page.createPage(), symbol).registerStat();
		agebook = new Achievement("achievement.myst.agebook", "myst.agebook", 0, 3, ItemAgebook.instance, null).initIndependentStat().registerStat();
		linkbook = new Achievement("achievement.myst.linkbook", "myst.linkbook", 2, 3, ItemLinkbook.instance, null).initIndependentStat().registerStat();
		quinn = new Achievement("achievement.myst.quinn", "myst.quinn", 4, 3, ItemLinkbook.instance, null).registerStat();

		AchievementPage page = new AchievementPage(MystObjects.ACHIEVEMENT_PAGE, agebook, linkbook, quinn, write, symbol);
		AchievementPage.registerAchievementPage(page);
	}

}
