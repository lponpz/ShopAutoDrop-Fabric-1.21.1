package com.example.shopautodrop;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class ShopAutoDropClient implements ClientModInitializer {
    private static boolean enabled = true;
    private static KeyBinding toggleKey;
    private static final Map<Item, Integer> previousCounts = new HashMap<>();
    private static boolean initialized = false;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shop_auto_drop.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.shop_auto_drop.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Shop Auto Drop: " + (enabled ? "ON" : "OFF")), true);
                }
            }

            if (client.player == null || client.world == null) {
                previousCounts.clear();
                initialized = false;
                return;
            }

            var inventory = client.player.getInventory();
            Map<Item, Integer> currentCounts = new HashMap<>();
            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);
                if (!stack.isEmpty()) currentCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }

            if (!initialized) {
                previousCounts.putAll(currentCounts);
                initialized = true;
                return;
            }

            if (enabled && client.currentScreen != null && client.interactionManager != null) {
                int newBones = Math.max(0, currentCounts.getOrDefault(Items.BONE, 0) - previousCounts.getOrDefault(Items.BONE, 0));
                int newBoneMeal = Math.max(0, currentCounts.getOrDefault(Items.BONE_MEAL, 0) - previousCounts.getOrDefault(Items.BONE_MEAL, 0));
                if (newBones > 0) dropNewItems(client, Items.BONE, newBones);
                if (newBoneMeal > 0) dropNewItems(client, Items.BONE_MEAL, newBoneMeal);
            }

            previousCounts.clear();
            previousCounts.putAll(currentCounts);
        });
    }

    private static void dropNewItems(net.minecraft.client.MinecraftClient client, Item item, int amount) {
        if (client.player == null || client.interactionManager == null) return;
        ScreenHandler handler = client.player.currentScreenHandler;
        int remaining = amount;

        for (int slotId = 0; slotId < handler.slots.size() && remaining > 0; slotId++) {
            Slot slot = handler.slots.get(slotId);
            if (slot.inventory == client.player.getInventory() && !slot.getStack().isEmpty() && slot.getStack().getItem() == item) {
                int toDrop = Math.min(slot.getStack().getCount(), remaining);
                for (int n = 0; n < toDrop; n++) {
                    client.interactionManager.clickSlot(handler.syncId, slotId, 0, SlotActionType.THROW, client.player);
                }
                remaining -= toDrop;
            }
        }
    }
}
