package freed.cam.event;

public interface BaseEventInterface<E extends MyEvent> {
    void setEventListner(E listner);
    void removeEventListner(E listner);
}
