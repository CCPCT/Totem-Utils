package CCPCT.TotemUtils.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;


public class Packets implements ClientModInitializer {
    private static final ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (packetsToSend.isEmpty()){
                return;
            }
            if (packetsToSend.getFirst() == null){
                packetsToSend.removeFirst();
                return;
            }

            sendPacket(packetsToSend.getFirst(),false);

            packetsToSend.removeFirst();
        });
    }

    public static boolean isQueueEmpty(){
        return packetsToSend.isEmpty();
    }

    public static void sendPacket(Packet<?> packet,boolean delay) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;
        if (delay) {
            packetsToSend.add(packet);
        } else {
            networkHandler.sendPacket(packet);
        }
    }

    //packet methods
    public static void selectHotbarSlot(int slot, boolean delay) {
        // use protocal number
        if (slot < 0 || slot > 8) return; // validate slot
        sendPacket(new UpdateSelectedSlotC2SPacket(slot),delay);

    }
}