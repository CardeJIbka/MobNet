package com.cardejibka.mobnet.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;

public class WoodenNetItem extends AbstractNetItem {
    public WoodenNetItem(Properties properties) {
        super(properties, "wooden_net");
    }

    @Override
    protected boolean canCapture(Mob mob) {
        return !(mob instanceof Enemy);
    }
}