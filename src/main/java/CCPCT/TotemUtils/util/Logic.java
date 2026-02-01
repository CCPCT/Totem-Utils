package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;

public class Logic {
    public static boolean overlayactive = false;
    public static boolean totemCountActive = false;
    public static int totemCountValue = 0;

    public static void refillTotem() {
        if (!Packets.isQueueEmpty()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;
        ItemStack mainhandStack = player.getMainHandStack();

        int slot = Logic.getSlotWithSpareTotem(0);
        // replenish main hand
        if (ModConfig.get().replenishMainHandTotem && mainhandStack.isEmpty()){
            if (slot==-1) {
                Chat.send("§cNo totem!", true);
            }
            if (slot >= 9) {
                // move totem from inv to mainhand
                Chat.send("§aRefilled mainhand", true);
                int selectedSlot = player.getInventory().getSelectedSlot();
                Packets.swapItem(slot, selectedSlot, true);
                Packets.sendNull();
                if (ModConfig.get().forceClientUpdate) {
                    ScreenHandler screenHandler = player.currentScreenHandler;
                    screenHandler.getSlot(selectedSlot+36).setStackNoCallbacks(screenHandler.getSlot(slot).getStack());
                    screenHandler.getSlot(slot).setStackNoCallbacks(ItemStack.EMPTY);
                }
                slot = Logic.getSlotWithSpareTotem(1);
            } // dont allow moving 2 items by pressing 1 button!!
        }

        // move totem to offhand
        if (totemOnOffhand()) {
            if (ModConfig.get().replenishGeneralItem){
                // replenish item if totem on offhand
                if (mainhandStack.getCount()==mainhandStack.getMaxCount()||mainhandStack.getCount()>8) return; // dont need to replenish
                int replenishSlot = getSlotWithItem(mainhandStack.getItem(),0);
                if (replenishSlot<=8) return; // cant replenish
                int hotslot = player.getInventory().getSelectedSlot() + 36;
                Packets.clickItem(hotslot, ItemStack.EMPTY,true);
                Packets.doubleClickItem(hotslot,mainhandStack,true);
                mainhandStack.setCount(Math.min(mainhandStack.getMaxCount(),getItemCount(mainhandStack.getItem(),false))); //update content
                Packets.clickItem(hotslot, mainhandStack,true);
                Packets.sendNull();

            }
            return;
        }

        Chat.send("§aRefilled offhand",true);
        if (slot == -1) {
            Chat.send("§cNo Totem!!",true);
            return;
        }
        moveTotemToOffhand(slot);
    }

    public static boolean totemOnOffhand(){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static int getTotemCount() {
        return getItemCount(Items.TOTEM_OF_UNDYING, true);
    }

    public static int getItemCount(Item item, boolean offhand) {
        //prefer take from inventory
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        //take from hotbar
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count+=stack.getCount();
            }

        }
        // count offhand
        ItemStack stack = player.getOffHandStack();
        if (offhand && !stack.isEmpty() && stack.getItem() == item) count+=stack.getCount();;

        return count;
    }

    public static int getSlotWithSpareTotem(int ignoring) {
        return getSlotWithItem(Items.TOTEM_OF_UNDYING, ignoring);
    }

    public static int getSlotWithItem(Item item, int ignoring) {        //prefer take from inventory
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 9; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                if (ignoring > 0){
                    ignoring--;
                } else {
                    return i;
                }
            }
        }
        //take from hotbar
        return getSlotWithItemInHotbar(item,ignoring);
    }

    public static int getSlotWithItemInHotbar(Item item, int ignoring){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                if (ignoring > 0){
                    ignoring--;
                } else {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getEmptySlotInHotbar(int ignoring) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) {
                if (ignoring > 0){
                    ignoring--;
                } else {
                    return i;
                }
            }
        }
        return -1;
    }

    private static void moveTotemToOffhand(int fromSlot) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        PlayerInventory inventory = player.getInventory();

        if (fromSlot < 9) {
            // hotbar case
            // Select Totem Slot
            Packets.selectHotbarSlot(fromSlot,false);

            // Swap Totem to Offhand
            Packets.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                    BlockPos.ORIGIN,
                    Direction.DOWN
            ),true);

            // Restore Old Hotbar Slot
            Packets.selectHotbarSlot(inventory.getSelectedSlot(),true);
            //delay
            Packets.sendNull();
            Packets.sendNull();

        } else {
            Packets.swapItem(fromSlot,40,false);
            Packets.sendNull();
            if (ModConfig.get().forceClientUpdate) {
                ScreenHandler screenHandler = player.currentScreenHandler;
                screenHandler.getSlot(45).setStackNoCallbacks(screenHandler.getSlot(fromSlot).getStack());
                screenHandler.getSlot(fromSlot).setStackNoCallbacks(ItemStack.EMPTY);
            }
        }
    }
    public static void stopTotemSound() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.getSoundManager().stopSounds(
                    Identifier.of("minecraft:item.totem.use"),
                    SoundCategory.PLAYERS
            );
        }
    }
    public static void playCustomSound() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(SoundEvent.of(Identifier.of(ModConfig.get().customSoundName)),ModConfig.get().customSoundVolume,1.0f);
        }
    }
}