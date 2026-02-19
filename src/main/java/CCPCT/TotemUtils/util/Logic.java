package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.mixin.PlayerInventoryMixin;
import CCPCT.TotemUtils.util.PacketHandler.Packet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;
        ItemStack mainhandStack = player.getMainHandStack();

        int slot = Logic.getSlotWithSpareTotem(0);

        // replenish main hand
        mainhand:
        if (ModConfig.get().replenishMainHandTotem  && totemOnOffhand()) {
            if (!ModConfig.get().smartReplanishHotbar || smartTotemSlot != -1) {
                // dont allow moving 2 items by pressing 1 button!!
                // if totem not on offhand refill offhand first
                int selectedSlot = ModConfig.get().smartReplanishHotbar ? smartTotemSlot : ((PlayerInventoryMixin) player.getInventory()).getSelectedSlot();
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
                if (mainhandStack.getCount()==mainhandStack.getMaxCount()||mainhandStack.getCount()>8) return; // dont need to replenish
                int replenishSlot = getSlotWithItem(mainhandStack.getItem(),0);
                if (replenishSlot<=8) return; // cant replenish
                int hotslot = ((PlayerInventoryMixin) player.getInventory()).getSelectedSlot() + 36;
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.currentScreenHandler.getSlot(45).getStack().getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static int getTotemCount(boolean refresh) {
        if (refresh) {
            System.out.println("Counting totems...");
            getItemCount(Items.TOTEM_OF_UNDYING);
        }
        return totemCount;
    }

    public static void getItemCount(Item item) {
        //prefer take from inventory
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        //take from hotbar
        int count = 0;
        for (Slot slot : player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 9; i < player.currentScreenHandler.slots.size(); i++) { //-1 to not count offhand
            ItemStack stack = player.currentScreenHandler.getSlot(i).getStack();
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.currentScreenHandler.getSlot(i).getStack();
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
            ItemStack stack = player.currentScreenHandler.getSlot(i).getStack();
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
        PacketHandler.swapItem(fromSlot,40,false);
        Packet.empty();
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

    public static ItemStack getMainhandStack() {
        if (MinecraftClient.getInstance().player == null) return ItemStack.EMPTY;
        PlayerInventoryMixin inventory = ((PlayerInventoryMixin) MinecraftClient.getInstance().player.getInventory());
        return inventory.getMain().get(inventory.getSelectedSlot());
    }
}