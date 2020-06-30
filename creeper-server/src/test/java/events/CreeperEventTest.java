package events;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class CreeperEventTest {

    private final ObjectMapper mapper = Creeper.registerJdkModuleAndGetMapper();

    @Test
    public void testSerialization() throws Exception {

        CreeperEvent creeperEvent = new CreeperEvent.Builder()
                .creeperEventType(CreeperEventType.GOSSIP)
                .epochTimestamp(System.currentTimeMillis())
                .payload("json")
                .playerId("asdfsaf")
                .build();

        String creeperEventJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(creeperEvent);

        CreeperEvent creeperEvent1 = mapper.readValue(creeperEventJson, CreeperEvent.class);

        Assert.assertEquals(creeperEvent, creeperEvent1);

    }

}