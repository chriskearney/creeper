package events;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ListenerService extends AbstractScheduledService {

    private final EventBus eventBus;
    private final List<CreeperEventListener> eventListeners = Lists.newArrayList();

    public ListenerService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected void runOneIteration() throws Exception {
        Iterator<CreeperEventListener> iterator = eventListeners.iterator();
        while (iterator.hasNext()) {
            CreeperEventListener next = iterator.next();
            if (!next.isActive()) {
                eventBus.unregister(next);
                iterator.remove();
            }
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
