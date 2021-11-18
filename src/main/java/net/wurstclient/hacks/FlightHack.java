/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixin.PlayerMoveC2SPacketAccessor;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FlyHack", "fly hack", "flying"})
public final class FlightHack extends Hack
	implements UpdateListener, IsPlayerInWaterListener, PacketOutputListener
{
	public final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);
	public final CheckboxSetting fakeGround = new CheckboxSetting(
		"Fake being on ground",
		"Tell the server you are standing on\n" +
		"ground when you are in fact flying.",
		false);
	
	public FlightHack()
	{
		super("Flight");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(fakeGround);
	}
	
	@Override
	public void onEnable()
	{
		WURST.getHax().creativeFlightHack.setEnabled(false);
		WURST.getHax().jetpackHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
		EVENTS.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
		EVENTS.remove(PacketOutputListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		
		player.getAbilities().flying = false;
		player.flyingSpeed = speed.getValueF();
		
		player.setVelocity(0, 0, 0);
		Vec3d velocity = player.getVelocity();
		
		if(MC.options.keyJump.isPressed())
			player.setVelocity(velocity.add(0, speed.getValue(), 0));
		
		if(MC.options.keySneak.isPressed())
			player.setVelocity(velocity.subtract(0, speed.getValue(), 0));
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		event.setInWater(false);
	}

	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		if(fakeGround.isChecked() &&
			event.getPacket() instanceof PlayerMoveC2SPacket)
			((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
	}
}
