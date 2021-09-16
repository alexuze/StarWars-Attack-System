package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Ewoks ewoks;
    private Diary diary;
    private Object myLock;

    public HanSoloMicroservice() {
        super("Han");
        ewoks = Ewoks.GetEwoksInstance();
        diary = Diary.getInstance();
        myLock = new Object();
    }
    @Override
    protected void initialize() {
        try {
            subscribeEvent(AttackEvent.class,(attackEvent)-> {
                List<Integer> serials = attackEvent.getSerials();
                ewoks.AssignEwoks(serials);
                try {
                    Thread.sleep(attackEvent.getDuration()); // simulate the attack by sleeping
                    diary.increaseAttack(this);
                    ewoks.ReleaseEwoks(serials);
                    complete(attackEvent, true);
                    sendBroadcast(new AttackHandledBroadcast()); // notify that han solo finshed his attack
                } catch (InterruptedException e) {
                    terminate();
                }
            });


            subscribeBroadcast(TerminateBroadcast.class,(terminate)-> {
                diary.setHanSoloTerminate(this,System.currentTimeMillis());
                terminate();
            });


            subscribeBroadcast(AllAttackSentBroadcast.class,(allAttacksSentBrod) -> {
                diary.setHanSoloFinish(this,System.currentTimeMillis());
            });
            Main.leiaSignal.countDown(); // announce that we finished subscribing to all we needed here
        } catch (Exception e) {

        }
    }

}
