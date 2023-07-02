package io.github.quantizr.dungeonrooms.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Taken from Skytils under the GNU Affero General Public License v3.0
 * https://github.com/Skytils/SkytilsMod/blob/0.x/LICENSE
 * @author My-Name-Is-Jeff (lily)
 */
@Cancelable
public class PacketEvent extends Event {

    public Direction direction;
    public Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public static class ReceiveEvent extends PacketEvent {
        public ReceiveEvent(Packet<?> packet) {
            super(packet);
            this.direction = Direction.INBOUND;
        }
    }

    public static class SendEvent extends PacketEvent {
        public SendEvent(Packet<?> packet) {
            super(packet);
            this.direction = Direction.OUTBOUND;
        }
    }

    enum Direction {
        INBOUND,
        OUTBOUND
    }

}