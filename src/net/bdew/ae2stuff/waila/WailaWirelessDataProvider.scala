/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.ae2stuff.machines.wireless.TileWireless
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.nbt.NBT
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object WailaWirelessDataProvider extends BaseDataProvider(classOf[TileWireless]) {
  override def getNBTTag(player: EntityPlayerMP, te: TileWireless, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound = {
    tag.setTag("wireless_waila",
      te.link map (link => NBT(
        "connected" -> true,
        "target" -> link,
        "channels" -> (if (te.connection != null) te.connection.getUsedChannels else 0),
        "power" -> te.getIdlePowerUsage
      )) getOrElse NBT("connected" -> false)
    )
    tag
  }

  override def getBodyStrings(target: TileWireless, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    if (acc.getNBTData.hasKey("wireless_waila")) {
      val data = acc.getNBTData.getCompoundTag("wireless_waila")
      if (data.getBoolean("connected")) {
        data.get[BlockPos]("target") map { pos =>
          List(
            Misc.toLocalF("ae2stuff.waila.wireless.connected", pos.getX, pos.getY, pos.getZ),
            Misc.toLocalF("ae2stuff.waila.wireless.channels", data.getInteger("channels")),
            Misc.toLocalF("ae2stuff.waila.wireless.power", DecFormat.short(data.getDouble("power")))
          )
        } getOrElse List(Misc.toLocal("ae2stuff.waila.wireless.notconnected"))
      } else {
        List(Misc.toLocal("ae2stuff.waila.wireless.notconnected"))
      }
    } else List.empty
  }
}
