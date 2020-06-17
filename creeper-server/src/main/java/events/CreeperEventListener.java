package events;

import com.comandante.creeper.events.CreeperEvent;

import java.io.IOException;

public interface CreeperEventListener {

    void creeperEvent(CreeperEvent creeperEvent) throws IOException;

    boolean isActive();
}
