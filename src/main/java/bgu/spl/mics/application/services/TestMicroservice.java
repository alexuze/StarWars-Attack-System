package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestEvent;

/**
 * A microservice that we will use in the tests of the MessageBus
 */

public class TestMicroservice extends MicroService {
    private boolean recievedEvent;
    private boolean recievedBrodcast;
    public TestMicroservice()
    {
        super("Tester");
        recievedEvent = false;
        recievedBrodcast =false;
    }
    @Override
    protected void initialize() {

    }

    public void setRecievedEvent(boolean recievedEvent)
    {
        this.recievedEvent = recievedEvent;
    }

    public void setRecievedBrodcast(boolean recievedBrodcast)
    {
        this.recievedBrodcast =recievedBrodcast;
    }
}
