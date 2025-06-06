package CCPCT.TotemUtils.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;

import org.spongepowered.asm.mixin.Unique;
import net.minecraft.sound.SoundEvents;


import java.util.ArrayList;

public class totemlogic {
    public static ArrayList<Packet<?>> packetsToSend = new ArrayList<>();
    public static ArrayList<Packet<?>> getPacketsToSend(){
        return packetsToSend;
    }
    public static void popPacketsToSend(){
        packetsToSend.removeFirst();
    }

    public static void refillTotem(boolean force) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) return;

        if (!force && player.getInventory().offHand.getFirst().getItem() == Items.TOTEM_OF_UNDYING) {
            IngameChat.sendColourChat("You already have a totem.", "white");
            return;
        }

        IngameChat.sendColourChat("Refilling totem!", "green");
        int spareTotemSlot = getSlotWithSpareTotem(player.getInventory());
        if (spareTotemSlot == -1) {
            IngameChat.sendColourChat("No totem!", "red");
            return;
        }
        moveTotemToOffhand(player, spareTotemSlot);
    }


    @Unique
    private static int getSlotWithSpareTotem(PlayerInventory inventory) {
        //prefer take from inventory
        for (int i = 9; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        //take from hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.main.get(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }

        return -1;
    }

    private static void moveTotemToOffhand(PlayerEntity player, int fromSlot) {
        ScreenHandler screenHandler = player.currentScreenHandler;
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
    public static void playShieldBreakSound() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            PositionedSoundInstance positionedSound = PositionedSoundInstance.master(SoundEvents.ITEM_SHIELD_BREAK,1.2f,3.0f);
            // Using built-in SoundEvents for shield break
            client.getSoundManager().play(positionedSound);
        }
    }
}

