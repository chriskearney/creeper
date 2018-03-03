package events;

        import com.google.common.eventbus.EventBus;

public class CreeperEventBus extends EventBus {

    public void registerListener(Object object) {
        register(object);
    }

}
