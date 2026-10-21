package com.cardejibka.mobnet.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class NetherStarNetItem extends AbstractNetItem {
    public NetherStarNetItem(Properties properties) {
        super(properties, "nether_star_net");
    }

    @Override
    protected boolean canCapture(Mob mob) {
        return true;
    }

    @Override
    protected Component styleName(Component name) {
        return name.copy().withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}