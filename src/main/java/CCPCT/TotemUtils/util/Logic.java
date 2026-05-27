package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.PacketHandler.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Logic {
    public static boolean overlayactive = false;
    public static boolean totemCountActive = false;
    public static int totemCountValue = 0;
    public static int totemCount = 0;
    public static int smartTotemSlot = -1;

    public static void resetStatus() {
        PacketHandler.clearPackets();
        overlayactive = false;
        smartTotemSlot = -1;
        TotemUtilsClient.moveMouseToTotem = false;
        TotemUtilsClient.popped = false;

    }

    public static void refillTotem() {
        if (!PacketHandler.isQueueEmpty()) return;
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;
        ItemStack mainhandStack = player.getMainHandItem();

        int slot = Logic.getSlotWithSpareTotem(0);

        // replenish main hand
        mainhand:
        if (ModConfig.get().replenishMainHandTotem  && totemOnOffhand()) {
            if (!ModConfig.get().smartReplanishHotbar || smartTotemSlot != -1) {
                // dont allow moving 2 items by pressing 1 button!!
                // if totem not on offhand refill offhand first
                int selectedSlot = ModConfig.get().smartReplanishHotbar ? smartTotemSlot : player.getInventory().getSelectedSlot();
                if (ModConfig.get().smartReplanishHotbar) {
                    if (smartTotemSlot==-1) break mainhand;
                } else {
                    if (!getMainhandStack().isEmpty()) break mainhand;
                }

                if (slot <= 35 && slot >= 0) {
                    // move totem from inv to mainhand
                    Chat.send("§aRefilled mainhand", true);
                    PacketHandler.swapItem(slot, selectedSlot, true);
                    Packet.empty();
                    slot = Logic.getSlotWithSpareTotem(1);
                } else {
                    Chat.send("§cNo totem!", true);
                }
            }
        }

        // move totem to offhand
        if (totemOnOffhand()) {
            if (ModConfig.get().replenishGeneralItem){
                // replenish item if totem on offhand
                if (mainhandStack.getCount()==mainhandStack.getMaxStackSize()||mainhandStack.getCount()>8) return; // dont need to replenish
                int replenishSlot = getSlotWithItem(mainhandStack.getItem(),0);
                if (replenishSlot<=8) return; // cant replenish
                int hotslot = player.getInventory().getSelectedSlot() + 36;
                PacketHandler.clickItem(hotslot, ItemStack.EMPTY,true);
                PacketHandler.doubleClickItem(hotslot,mainhandStack,true);
                //mainhandStack.setCount(Math.min(mainhandStack.getMaxCount(),getItemCount(mainhandStack.getItem(),false))); //update content
                PacketHandler.clickItem(hotslot, mainhandStack,true);
                Packet.empty();

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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.containerMenu.getSlot(45).getItem().getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static int getTotemCount(boolean refresh) {
        if (refresh) {
            getItemCount(Items.TOTEM_OF_UNDYING);
        }
        return totemCount;
    }

    public static void getItemCount(Item item) {
        //prefer take from inventory
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        //take from hotbar
        int count = 0;
        for (Slot slot : player.containerMenu.slots) {
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && stack.getItem() == item) {
                count+=stack.getCount();
            }
        }
        totemCount=count;
    }

    public static int getSlotWithSpareTotem(int ignoring) {
        return getSlotWithItem(Items.TOTEM_OF_UNDYING, ignoring);
    }

    public static int getSlotWithItem(Item item, int ignoring) {        //prefer take from inventory
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        for (int i = 9; i < player.containerMenu.slots.size(); i++) { //-1 to not count offhand
            ItemStack stack = player.containerMenu.getSlot(i).getItem();
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

    public static int getSlotWithItemInHotbar(Item item, int ignoring){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.containerMenu.getSlot(i).getItem();
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.containerMenu.getSlot(i).getItem();
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        PacketHandler.swapItem(fromSlot,40,false);
        Packet.empty();
    }
    public static void stopTotemSound() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.getSoundManager().stop(
                    Identifier.withDefaultNamespace("item.totem.use"),
                    SoundSource.PLAYERS
            );
        }
    }
    public static void playCustomSound() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.playSound(
                    SoundEvent.createVariableRangeEvent(
                            Identifier.withDefaultNamespace(
                                    ModConfig.get().customSoundName.replace("minecraft:",""))),
                    ModConfig.get().customSoundVolume,1.0f);
        }
    }

    public static ItemStack getMainhandStack() {
        if (Minecraft.getInstance().player == null) return ItemStack.EMPTY;
        return Minecraft.getInstance().player.getMainHandItem();
    }
}