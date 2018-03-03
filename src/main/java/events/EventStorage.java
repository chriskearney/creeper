package events;

import java.util.UUID;

public interface EventStorage {

    void storeEvent(CreeperEvent creeperEvent);

    CreeperEvent get(UUID eventUUID);

}
