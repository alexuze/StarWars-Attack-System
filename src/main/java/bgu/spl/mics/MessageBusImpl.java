package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {

	private  Semaphore sendEventSemaphore; // we will use it in the sendEvent function
	// we will use BlockingQueue because all queuing methods achieve their effects atomically which is good for out implementation because we wan't to avoid deadlocks and multi threading related mistakes
	private  Map<Class<? extends Message>, BlockingQueue> messageSubscribers;// queue of subscribers to each message - to help implement round robin
	private  Map<MicroService, BlockingQueue> receivedMessages; // A message queue for each microservice
	private  Map<Message,Future> futures; // events and corresponding future objects

	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl()
	{
		messageSubscribers = new ConcurrentHashMap<>();
		sendEventSemaphore = new Semaphore(1); // only one thread shall send an event at a time
		receivedMessages = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
	}
	public static MessageBus getInstance() {
		return SingletonHolder.instance;
	}


	private void subscribeMessage(Class<? extends Message> type,MicroService m)
	{
		if(!messageSubscribers.containsKey(type)) // create a queue only if there is no event of this type
		{
			BlockingQueue<MicroService> newSubscriberQueue = new LinkedBlockingQueue<>();
			messageSubscribers.putIfAbsent(type,newSubscriberQueue); // create new entry in the map
		}
		BlockingQueue<MicroService> eventQueue = messageSubscribers.get(type);
		try {
			eventQueue.put(m); // add the microservice to the queue of subscribers to this Message
		} catch (InterruptedException e) {

		}
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)  {
		subscribeMessage(type,m);

	}
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws Exception {
		subscribeMessage(type,m);

	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if(futures.containsKey(e)) {
			Future<T> future = futures.get(e);
			future.resolve(result);
		}

	}

	@Override
	public void sendBroadcast(Broadcast b)  {
		if(messageSubscribers.containsKey(b.getClass())) // check if anyone is subscribed to get this broadcast
		{
			BlockingQueue<MicroService> registeredMicroservices = messageSubscribers.get(b.getClass());
			for (MicroService m : registeredMicroservices) {
				try {
					receivedMessages.get(m).put(b); // add the broadcast to the queue of every microservice that subcscribed to receive this broadcast
				} catch (InterruptedException e) {

				}
			}
		}

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e)  {
		Future<T> toReturn = null;
		if(messageSubscribers.containsKey(e.getClass())) // check if someone is even registered to this event
		{

			try {
				sendEventSemaphore.acquire(); // once someone is sending an event no one should be able to send in order to maintain round robin distribution of events
				BlockingQueue<MicroService> registeredServices = messageSubscribers.get(e.getClass()); // get the services that are registered to this event
				if (!registeredServices.isEmpty()) {
					MicroService reciever = registeredServices.take(); // get the first subscriber (head of the queue)
					toReturn = new Future<>();
					futures.putIfAbsent(e, toReturn); // store the future<T> with the corresponding event
					receivedMessages.get(reciever).put(e); // insert the event to the queue of the reciever
					registeredServices.put(reciever); // insert the reciever back to the tail of the queue to maintain round-robin
				}
				sendEventSemaphore.release(); // once were done sending the event someone can try and send another event
			} catch (InterruptedException interruptedException) {
			}
		}
		return toReturn;


	}


	@Override
	public void register(MicroService m)  {
		receivedMessages.putIfAbsent(m,new LinkedBlockingQueue());

	}

	@Override
	public void unregister(MicroService m) {
		if (receivedMessages.containsKey(m)) {
			for (BlockingQueue registerdServices : messageSubscribers.values()) // remove m from all the queues of the MessageTypeMap
			{
				if (registerdServices.contains(m)) {
					registerdServices.remove(m);
				}
			}
			receivedMessages.remove(m); // remove m from the registered services queue as well
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!receivedMessages.containsKey(m))
			throw new IllegalStateException("Microservice was never registered");

		BlockingQueue<Message> serviceMessages = receivedMessages.get(m);

		return serviceMessages.take();

	}
}
