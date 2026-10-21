package com.cardejibka.mobnet.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.neoforged.neoforge.common.Tags;

public class GoldenNetItem extends AbstractNetItem {
    public GoldenNetItem(Properties properties) {
        super(properties, "golden_net");
    }

    @Override
    protected boolean canCapture(Mob mob) {
        if (mob instanceof EnderDragon || mob instanceof WitherBoss) return false;
        if (mob.getType().is(Tags.EntityTypes.BOSSES)) return false;
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getPath();
        return !path.contains("boss");
    }
}