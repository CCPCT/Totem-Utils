package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.config.ModConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Unique;


import java.util.ArrayList;

public class totemlogic {
    public static boolean overlayactive = false;
    public static boolean totemCountActive = false;
    public static int totemCountValue = 0;
    public static ArrayList<Packet<?>> packetsToSend = new ArrayList<>();
    public static ArrayList<Packet<?>> getPacketsToSend(){
        return packetsToSend;
    }
    public static void popPacketsToSend(){
        packetsToSend.removeFirst();
    }

    public static void refillTotem(boolean force) {
        if (force){
            moveTotemToOffhand();
        } else if (packetsToSend.isEmpty()) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null) return;

            if (totemOnOffhand()) {
                IngameChat.sendColourChat("You already have a totem.", "yellow");
                return;
            }

            IngameChat.sendColourChat("Refilling totem!", "green");
            int spareTotemSlot = getSlotWithSpareTotem();
            if (spareTotemSlot == -1) {
                IngameChat.sendColourChat("No totem!", "red");
                return;
            }
            moveTotemToOffhand();
        }
    }

    public static boolean totemOnOffhand(){
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        return player.getInventory().offHand.getFirst().getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static int getTotemCount() {
        //prefer take from inventory
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        //take from hotbar
        int count = 0;
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }
        }
        return count;
    }

    @Unique
    private static int getSlotWithSpareTotem() {
        //prefer take from inventory
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        for (int i = 9; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        //take from hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().main.get(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    private static void moveTotemToOffhand() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        ScreenHandler screenHandler = player.currentScreenHandler;
        int fromSlot = getSlotWithSpareTotem();
        PlayerInventory inventory = player.getInventory();
        ItemStack totemStack = inventory.getStack(fromSlot).copy();

        if (fromSlot < 9) {
            // Select Totem Slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(fromSlot));

            // Move Totem To Offhand
            packetsToSend.add(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                    BlockPos.ORIGIN,
                    Direction.DOWN
            ));

            // Restore Old Hotbar Slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(inventory.selectedSlot));

            packetsToSend.add(null);
        } else {

            packetsToSend.add(new ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.getRevision(),
                    fromSlot,
                    0,
                    SlotActionType.PICKUP,
                    ItemStack.EMPTY,
                    new Int2ObjectOpenHashMap<>()
            ));

            packetsToSend.add(new ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.getRevision(),
                    45,
                    0,
                    SlotActionType.PICKUP,
                    totemStack,
                    new Int2ObjectOpenHashMap<>()
            ));

            packetsToSend.add(null);
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