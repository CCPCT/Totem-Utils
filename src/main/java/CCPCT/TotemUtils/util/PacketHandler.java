package CCPCT.TotemUtils.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Queue;

public class PacketHandler implements ClientModInitializer {
    private static final Queue<Packet> packetsToSend = new ArrayDeque<>();

    public static void clearPackets() {
        packetsToSend.clear();
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (packetsToSend.isEmpty()){
                return;
            }
            var packet = packetsToSend.poll();
            if (packet==null || packet.type == null ){
                return;
            }
            sendPacket(packet);
        });
    }

    private static void sendPacket(Packet packet) {
        MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || interactionManager == null) return;
        interactionManager.handleContainerInput(player.containerMenu.containerId, packet.slot, packet.button, packet.type, player);
    }

    public static boolean isQueueEmpty(){
        return packetsToSend.isEmpty();
    }

    public record Packet(int slot, int button, ContainerInput type) {
        // Helper constructor for a standard slot click
        public static void click(int slot, int button, ContainerInput type) {
            packetsToSend.add(new Packet(slot, button, type));
        }

        public static void clickNow(int slot, int button, ContainerInput type) {
            MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            interactionManager.handleContainerInput(player.containerMenu.containerId, slot, button, type, player);
        }

        // update
        public static void empty() {
            packetsToSend.add(new Packet(0, 0, null));
        }
    }

    public static void clickItem(int slot, ItemStack holding, boolean delay) {
        if (delay) {
            Packet.click(slot, 0, ContainerInput.PICKUP);
        } else {
            Packet.clickNow(slot, 0, ContainerInput.PICKUP);
        }
    }

    public static void doubleClickItem(int slot, ItemStack holding, boolean delay) {
        if (delay) {
            Packet.click(slot, 0, ContainerInput.PICKUP_ALL);
        } else {
            Packet.clickNow(slot, 0, ContainerInput.PICKUP_ALL);
        }
    }

    public static void swapItem(int slot, int to, boolean delay) {
        if (delay) {
            Packet.click(slot, to, ContainerInput.SWAP);
        } else {
            Packet.clickNow(slot, to, ContainerInput.SWAP);
        }
    }
}