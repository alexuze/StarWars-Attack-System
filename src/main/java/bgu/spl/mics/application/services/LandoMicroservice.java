package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeathStarBombedBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private Diary diary;
    private long duration;
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        //System.out.println("thread "+ getName() + " started working at:"+ System.currentTimeMillis());
        try {
            subscribeEvent(BombDestroyerEvent.class,(bombDestroyerEvent)->{
                try {
                    Thread.sleep(duration);
                    complete(bombDestroyerEvent,true);
                    sendBroadcast(new DeathStarBombedBroadcast());
                } catch (InterruptedException e) {
                    terminate();

                }
            });

            subscribeBroadcast(TerminateBroadcast.class,(terminate)->{
                diary.setLandoTerminate(this,System.currentTimeMillis());
                terminate();
            });
            Main.leiaSignal.countDown(); // announce that we finished subscribing to all we needed here
        } catch (Exception e) {

        }
    }
    public void call(BombDestroyerEvent bombDestroyerEvent)
    {
        try {
            Thread.sleep(duration);
            complete(bombDestroyerEvent,true);
            sendBroadcast(new DeathStarBombedBroadcast());
        } catch (InterruptedException e) {
            terminate();
        }
    }
    public void call(TerminateBroadcast terminateBroadcast){
        diary.setLandoTerminate(this,System.currentTimeMillis());
        terminate();
    }
}
