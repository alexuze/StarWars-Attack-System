package bgu.spl.mics.application.services;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
    private List<Future> futures;
    private int attacksPerformed;
    private int finshedSubscribing;
	
    public LeiaMicroservice(Attack[] attacks)
    {
        super("Leia");
		this.attacks = attacks;
        diary = Diary.getInstance();
        futures = new LinkedList<>();
        attacksPerformed = 0;
        finshedSubscribing = 0;
    }
    @Override
    protected void initialize() {
        try {

            subscribeBroadcast(TerminateBroadcast.class,(terminate)->{
                diary.setLeiaTerminate(this,System.currentTimeMillis());
                terminate();
            });


            subscribeBroadcast(AttackHandledBroadcast.class,(attackHandled) -> {
                attacksPerformed++;
                if(attacksPerformed == attacks.length) // if we handled all the attacks that were sent
                {
                    Future<Boolean> r2d2Future=null;
                    try {
                        r2d2Future = sendEvent(new DeactivationEvent()); // now r2d2 can try to deactivate the shield generator
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(r2d2Future != null && r2d2Future.get()!= null && r2d2Future.get() == true)//if r2d2 finished the deactivation successfully
                    {
                        try {
                            sendEvent(new BombDestroyerEvent()); // notify Lando he can safely bomb the Death star
                        } catch (Exception e) {

                        }
                    }
                }
            });


            subscribeBroadcast(DeathStarBombedBroadcast.class,(deathStarBombed)->{
                sendBroadcast(new TerminateBroadcast());
            });
            Main.leiaSignal.await(); // wait until everyone finished subscribing to their messages so that no message will be lost
            for(int i=0;i<attacks.length;i++) // send the attack events
            {
                try {
                    AttackEvent event = new AttackEvent(attacks[i]);
                    Future<Boolean> future = sendEvent(event);
                    futures.add(future);
                } catch (Exception e) {

                }
            }
            sendBroadcast(new AllAttackSentBroadcast()); // notify that all the attacks were sent
        } catch (Exception e) {

        }

    }
    public void call(TerminateBroadcast terminateBroadcast)
    {
        diary.setLeiaTerminate(this,System.currentTimeMillis());
        terminate();
    }
    public void call(AttackHandledBroadcast attackHandledBroadcast)
    {
        attacksPerformed++;
        if(attacksPerformed == attacks.length) // if we handled all the attacks that were sent
        {
            Future<Boolean> r2d2Future=null;
            try {
                r2d2Future = sendEvent(new DeactivationEvent()); // now r2d2 can try to deactivate the shield generator
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(r2d2Future != null && r2d2Future.get()!= null && r2d2Future.get() == true)//if r2d2 finished the deactivation successfully
            {
                try {
                    sendEvent(new BombDestroyerEvent()); // notify Lando he can safely bomb the Death star
                } catch (Exception e) {

                }
            }
        }
    }
    public void call(DeathStarBombedBroadcast deathStarBombedBroadcast)
    {
        // after lando bombed the death star we can gracefully terminate the program
        sendBroadcast(new TerminateBroadcast());
    }
}
