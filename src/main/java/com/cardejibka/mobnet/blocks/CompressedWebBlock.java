package com.cardejibka.mobnet.blocks;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class CompressedWebBlock extends Block {
    public static final MapCodec<CompressedWebBlock> CODEC = simpleCodec(CompressedWebBlock::new);

    public CompressedWebBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public static boolean isProperTool(ItemStack tool) {
        return tool.getItem() instanceof SwordItem || tool.getItem() instanceof ShearsItem;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.05, 0.05, 0.05));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool != null) {
            if (tool.getItem() instanceof ShearsItem) {
                return List.of(new ItemStack(this));
            }
            if (tool.getItem() instanceof SwordItem) {
                return List.of(new ItemStack(Items.STRING, 4), new ItemStack(Items.COBWEB, 5));
            }
        }
        return List.of();
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float base = super.getDestroyProgress(state, player, level, pos);
        return isProperTool(player.getMainHandItem()) ? base * 3.0F : base;
    }
}