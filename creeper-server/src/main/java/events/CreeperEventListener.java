package events;

import org.glassfish.jersey.media.sse.EventOutput;

import java.io.IOException;

public interface CreeperEventListener {

    void creeperEvent(CreeperEvent creeperEvent) throws IOException;

    boolean isActive();
}
