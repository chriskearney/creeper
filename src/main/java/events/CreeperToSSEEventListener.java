package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;

import java.io.IOException;
import java.util.Date;

public class CreeperToSSEEventListener implements CreeperEventListener {

    private final EventOutput eventOutput;
    private final ObjectMapper objectMapper;
    private final String playerId;

    public CreeperToSSEEventListener(String playerId, EventOutput eventOutput, ObjectMapper objectMapper) {
        this.playerId = playerId;
        this.eventOutput = eventOutput;
        this.objectMapper = objectMapper;
    }

    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        try {

            if ((creeperEvent.getAudience() == CreeperEvent.Audience.PLAYER_ONLY && creeperEvent.getPlayerId().get().equals(playerId)) || creeperEvent.getAudience() == CreeperEvent.Audience.EVERYONE) {
                final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
                final String timestamp = Long.toString((new Date()).getTime());
                eventBuilder.id(timestamp);
                eventBuilder.name("creeperEvent");
                eventBuilder.data(objectMapper.writeValueAsString(creeperEvent));
                eventOutput.write(eventBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        try {
            eventOutput.write(new OutboundEvent.Builder().name("ping").data(String.class, "EOM").build());
            return true;
        } catch (Exception e) {
            try {
                eventOutput.close();
            } catch (Exception ei) {
                ei.printStackTrace();
            }
            return false;
        }
    }
}
