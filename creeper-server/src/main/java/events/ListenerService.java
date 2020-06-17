package events;

import com.comandante.creeper.events.CreeperEvent;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListenerService extends AbstractScheduledService {

    private final EventBus eventBus;
    private final List<CreeperEventListener> eventListeners = Lists.newArrayList();
    private static final Logger log = Logger.getLogger(ListenerService.class);


    public ListenerService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected void runOneIteration() {
        try {
            Iterator<CreeperEventListener> iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                CreeperEventListener next = iterator.next();
                if (!next.isActive()) {
                    eventBus.unregister(next);
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            log.error("Problem checking eventbus listener.", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 60, TimeUnit.SECONDS);
    }

    public void registerListener(CreeperEventListener creeperEventListener) {
        eventBus.register(creeperEventListener);
        eventListeners.add(creeperEventListener);
    }

    public void post(CreeperEvent creeperEvent) {
        eventBus.post(creeperEvent);
    }
}
