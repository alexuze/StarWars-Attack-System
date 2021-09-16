package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import bgu.spl.mics.application.services.TestMicroservice;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

/**
 * we changed the tests because we used a testMicroservice class and a TestEvent class instead of using hansolo and r2d2 and because we are not throwing exceptions in the message bus so we modified the functions to adjust to that change
 */

class MessageBusImplTest {

    private  MessageBus messageBus;
    private  Event sentEvent1;
    private  Event sentEvent2;
    private  Future futureToResolve1;
    private  Future futureToResolve2;
    private  Broadcast sentBroadcast;
    private TestMicroservice testMicroservice1;
    private TestMicroservice testMicroservice2;

    @BeforeEach
    public void setMessageBus(){
        messageBus = MessageBusImpl.getInstance();
        testMicroservice1 = new TestMicroservice();
        testMicroservice2 = new TestMicroservice();
    }

    /**
     * In this scenario:
     *         testMicroservice1 will register
     *         testMicroservice1 will Subscribe to an TestEvent
     *         testMicroservice1 will send TestEvent
     *         testMicroservice1 will receive the Message
     *         testMicroservice1 will complete the Event
     */
    @Test
    void Scenario1()
    {
        registerTest(testMicroservice1);
        subscribeEvent(testMicroservice1);
        sentEvent1 = new TestEvent();
        futureToResolve1 = sendEventTest(testMicroservice1, sentEvent1);
        awaitMessageTest(testMicroservice1,sentEvent1);
        completeTest(testMicroservice1, sentEvent1, futureToResolve1);
        messageBus.unregister(testMicroservice1);
    }

    /**
     * In this scenario:
     *         testMicroservice1 will register
     *         testMicroservice2 will register
     *         testMicroservice1 will Subscribe to a TestEvent
     *         testMicroservice2 will Subscribe to a TestEvent
     *         testMicroservice1 will send TestEvent
     *         testMicroservice2 will send TestEvent
     *         testMicroservice1 will receive the TestEvent
     *         testMicroservice2 will receive the TestEvent
     *         testMicroservice1 will complete the Event
     *         testMicroservice2 will complete the Event
     */
    @Test
    void Scenario2()
    {
        registerTest(testMicroservice1);
        registerTest(testMicroservice2);
        subscribeEvent(testMicroservice1);
        subscribeEvent(testMicroservice2);
        sentEvent1 = new TestEvent();
        sentEvent2 = new TestEvent();
        futureToResolve1 = sendEventTest(testMicroservice1, sentEvent1);
        futureToResolve2 = sendEventTest(testMicroservice2, sentEvent2);
        awaitMessageTest(testMicroservice1, sentEvent1);
        awaitMessageTest(testMicroservice2, sentEvent2);
        completeTest(testMicroservice1, sentEvent1, futureToResolve1);
        completeTest(testMicroservice2, sentEvent2, futureToResolve2);
        messageBus.unregister(testMicroservice1);
        messageBus.unregister(testMicroservice2);
    }


    /**
     * * In this scenario:
     *         testMicroservice1 will register
     *         testMicroservice2 will register
     *         testMicroservice1 will Subscribe to a TerminateBroadcast
     *         testMicroservice2 will Subscribe to a TerminateBroadcast
     *         testMicroservice1 will send a TerminateBroadcast
     *         testMicroservice1 will receive the Broadcast
     *         testMicroservice2 will receive the Broadcast
     */
    @Test
    void Scenario3()
    {
        registerTest(testMicroservice1);
        registerTest(testMicroservice2);
        subscribeBroadcast(testMicroservice1);
        subscribeBroadcast(testMicroservice2);

        sentBroadcast = new TerminateBroadcast();
        sendBroadcastTest(testMicroservice1,sentBroadcast);
        awaitMessageTest(testMicroservice1,sentBroadcast);
        awaitMessageTest(testMicroservice2,sentBroadcast);
        messageBus.unregister(testMicroservice1);
        messageBus.unregister(testMicroservice2);
    }

    /**
     * * In this scenario:
     *         testMicroservice1 will register
     *         testMicroservice2 will register
     *         testMicroservice1 will Subscribe to an TestEvent
     *         testMicroservice2 will Subscribe to an TestEvent
     *         testMicroservice1 will send 2 TestEvents
     *         testMicroservice1 will receive the 1st Event
     *         testMicroservice2 will receive the 2nd Event
     *         testMicroservice1 will complete the event
     *         testMicroservice2 will complete the event
     */
    @Test
    void Scenario4()
    {
        registerTest(testMicroservice1);
        registerTest(testMicroservice2);
        subscribeEvent(testMicroservice1);
        subscribeEvent(testMicroservice2);
        sentEvent1 = new TestEvent();
        sentEvent2 = new TestEvent();
        futureToResolve1 = sendEventTest(testMicroservice1, sentEvent1);
        futureToResolve2 = sendEventTest(testMicroservice1, sentEvent2);
        awaitMessageTest(testMicroservice1, sentEvent1);
        awaitMessageTest(testMicroservice2, sentEvent2);
        completeTest(testMicroservice1, sentEvent1, futureToResolve1);
        completeTest(testMicroservice2, sentEvent2, futureToResolve2);
        messageBus.unregister(testMicroservice1);
        messageBus.unregister(testMicroservice2);
    }
    private void registerTest(MicroService microService)
    {
        try {
            messageBus.register(microService);
        } catch (Exception e) {

        }

    }

    /**
     * changed this test function because we added a TestMicroservice and a TestEvent and changed the way we thought we would implement the messageBus
     * @param m
     */
    private void subscribeEvent(TestMicroservice m)
    {
        try {
            m.subscribeEvent(TestEvent.class,(test) ->{
                m.setRecievedEvent(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * changed this test function because we added a TestMicroservice and a TestEvent and changed the way we thought we would implement the messageBus
     * @param testMicroservice
     */
    private void subscribeBroadcast(TestMicroservice testMicroservice){
        try {
            testMicroservice.subscribeBroadcast(TerminateBroadcast.class,(terminate)->{
                testMicroservice.setRecievedBrodcast(true);
            });
        } catch (Exception e) {

        }
    }
    private Future<Boolean> sendEventTest(MicroService microService,Event<Boolean> event) {
        Future<Boolean> future = null;
        try {
            future=microService.sendEvent(event);
        } catch (Exception e) {

        }
        assertNotEquals(null,future); // if we got back a future object that is not null we added successfully
    return future;
    }

    private void sendBroadcastTest(MicroService microService,Broadcast broadcast) {
        sentBroadcast = broadcast;
        try {
            microService.sendBroadcast(broadcast);
        } catch (Exception e) {

        }
    }

    private void completeTest(MicroService microService, Event<Boolean> event, Future<Boolean> futureToResolve) {
        assertFalse(futureToResolve.isDone());
        try {
            microService.complete(event,Boolean.TRUE);
        } catch (Exception e) {

        }
        assertTrue(futureToResolve.isDone());
        assertEquals(Boolean.TRUE,futureToResolve.get());
    }

    /**
     * changed this method because we added a TestMicroservice and changed the way the messageBus was implemented to not throw exceptions
     * @param microService
     * @param expectedMessage
     */
    private void awaitMessageTest(TestMicroservice microService, Message expectedMessage)
    {
        try {
            Message message = messageBus.awaitMessage(microService);
            assertEquals(message.getClass(),expectedMessage.getClass());
        } catch (Exception e) {
            fail("should have recieve a Message");
        }
    }
}