package com.cardejibka.mobnet;

import com.cardejibka.mobnet.blocks.CompressedWebBlock;
import com.cardejibka.mobnet.item.GoldenNetItem;
import com.cardejibka.mobnet.item.NetherStarNetItem;
import com.cardejibka.mobnet.item.WoodenNetItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(MobNet.MOD_ID)
public class MobNet {
    public static final String MOD_ID = "mobnet";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredItem<WoodenNetItem> WOODEN_NET =
            ITEMS.registerItem("wooden_net", WoodenNetItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<GoldenNetItem> GOLDEN_NET =
            ITEMS.registerItem("golden_net", GoldenNetItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<NetherStarNetItem> NETHER_STAR_NET =
            ITEMS.registerItem("nether_star_net", NetherStarNetItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredBlock<CompressedWebBlock> COMPRESSED_WEB =
            BLOCKS.registerBlock("compressed_web", CompressedWebBlock::new,
                    BlockBehaviour.Properties.of()
                            .strength(12.0F)
                            .noOcclusion()
                            .noCollission()
                            .pushReaction(PushReaction.DESTROY));
    public static final DeferredItem<?> COMPRESSED_WEB_ITEM =
            ITEMS.registerSimpleBlockItem("compressed_web", COMPRESSED_WEB);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOB_NET_CREATIVE_TAB =
            CREATIVE_MODE_TABS.register("mobnet_tab", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> WOODEN_NET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(WOODEN_NET.get());
                        output.accept(GOLDEN_NET.get());
                        output.accept(NETHER_STAR_NET.get());
                        output.accept(COMPRESSED_WEB_ITEM.get());
                    })
                    .title(Component.translatable("tab.mobnet.mobnet_tab"))
                    .build());

    public MobNet(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Инициализация MobNet мода");
        if (Config.ENABLE_FEATURE_X.get()) {
            LOGGER.info("Функция X включена");
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Клиентская часть MobNet инициализирована");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Сервер MobNet запущен");
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.getTargetBlock().is(COMPRESSED_WEB.get())) {
            event.setCanHarvest(CompressedWebBlock.isProperTool(event.getEntity().getMainHandItem()));
        }
    }
}