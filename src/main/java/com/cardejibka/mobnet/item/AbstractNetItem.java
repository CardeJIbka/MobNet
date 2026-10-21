package com.cardejibka.mobnet.item;

import com.cardejibka.mobnet.MobNet;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class AbstractNetItem extends Item {
    private static final String CAPTURED_MOB_KEY = "CapturedMob";
    private static final String MOB_NAME_KEY = "MobName";

    private final String netId;

    protected AbstractNetItem(Item.Properties properties, String netId) {
        super(properties);
        this.netId = netId;
    }

    protected abstract boolean canCapture(Mob mob);

    protected Component styleName(Component name) {
        return name;
    }

    protected static boolean hasMob(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(CAPTURED_MOB_KEY);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item." + MobNet.MOD_ID + "." + netId + ".tooltip")
                .withStyle(ChatFormatting.GRAY));
        if (hasMob(stack)) {
            tooltip.add(Component.translatable("item.mobnet.net.tooltip.filled")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player,
                                                  LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide || hasMob(stack) || !(target instanceof Mob mob) || !canCapture(mob)) {
            return InteractionResult.PASS;
        }

        CompoundTag mobData = new CompoundTag();
        if (!mob.save(mobData)) {
            return InteractionResult.FAIL;
        }

        ItemStack filled = stack.copy();
        String mobName = target.getName().getString();
        CustomData.update(DataComponents.CUSTOM_DATA, filled, tag -> {
            tag.put(CAPTURED_MOB_KEY, mobData);
            tag.putString(MOB_NAME_KEY, mobName);
        });
        player.setItemInHand(hand, filled);
        mob.discard();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (level.isClientSide || !hasMob(stack)) {
            return InteractionResult.PASS;
        }

        try {
            CompoundTag mobData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getCompound(CAPTURED_MOB_KEY);
            Optional<EntityType<?>> type = EntityType.by(mobData);
            if (type.isPresent()) {
                Entity entity = type.get().create(level);
                if (entity instanceof Mob mob) {
                    mob.load(mobData);
                    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                    Player player = context.getPlayer();
                    mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                            player != null ? player.getYRot() : 0.0F, 0.0F);
                    level.addFreshEntity(mob);
                    stack.remove(DataComponents.CUSTOM_DATA); // сеть снова пустая
                }
            }
            return InteractionResult.SUCCESS;
        } catch (Exception e) {
            if (context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.literal("Ошибка при выпуске моба!"));
            }
            return InteractionResult.FAIL;
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            if (tag.contains(MOB_NAME_KEY)) {
                return styleName(Component.translatable(
                        "item." + MobNet.MOD_ID + "." + netId + "_with", tag.getString(MOB_NAME_KEY)));
            }
        }
        return styleName(super.getName(stack));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasMob(stack);
    }
}