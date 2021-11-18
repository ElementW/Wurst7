/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.BlockMatchHack;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.util.BlockUtils;

import java.awt.*;

@SearchTags({"cave finder"})
public final class CaveFinderHack extends BlockMatchHack
	implements UpdateListener, RenderListener
{
	private final ColorSetting color = new ColorSetting("Color",
		"Caves will be highlighted\n" + "in this color.", Color.RED);
	
	private final SliderSetting opacity = new SliderSetting("Opacity",
		"How opaque the highlights should be.\n" + "0 = breathing animation", 0,
		0, 1, 0.01,
		v -> v == 0 ? "Breathing" : SliderSetting.ValueDisplay.PERCENTAGE.getValueString(v));

	public CaveFinderHack()
	{
		super("CaveFinder");
		setCategory(Category.RENDER);
		addSetting(color);
		addSetting(opacity);
		Block caveAir = BlockUtils.getBlockFromName("minecraft:cave_air");
		setBlockMatcher(b -> b == caveAir);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();

		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}

	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);

		super.onDisable();
	}

	@Override
	public void onUpdate()
	{
		updateSearch();
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		float alpha;

		if (opacity.getValue() > 0)
			alpha = opacity.getValueF();
		else {
			float x = System.currentTimeMillis() % 2000 / 1000F;
			alpha = 0.25F + 0.25F * MathHelper.sin(x * (float)Math.PI);
		}

		float[] colorF = color.getColorF();
		render(matrixStack, colorF[0], colorF[1], colorF[2], alpha);
	}
}
