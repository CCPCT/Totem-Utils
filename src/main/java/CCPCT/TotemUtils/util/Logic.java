package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
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
        int slot = Logic.getSlotWithSpareTotem(0);
        if (ModConfig.get().replaceMainHandTotem && player.getMainHandStack().isEmpty() && slot >= 9){
            // move totem to mainhand
            Chat.send("§aRefilled mainhand",true);
            Packets.swapItem(slot, player.getInventory().getSelectedSlot(), true);
            Packets.sendNull();
            slot = Logic.getSlotWithSpareTotem(1);
        }

        // move totem to offhand
        if (totemOnOffhand()) return;
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
        //prefer take from inventory
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        //take from hotbar
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }

        }
        // count offhand
        ItemStack stack = player.getOffHandStack();
        if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) count++;

        return count;
    }

    private static int getSlotWithSpareTotem(int ignoring) {
        //prefer take from inventory
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 9; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                if (ignoring > 0){
                    ignoring--;
                } else {
                    return i;
                }
            }
        }
        //take from hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
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