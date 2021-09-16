package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long Duration;
    private Diary diary;
    public R2D2Microservice(long duration) {
        super("R2D2");
        this.Duration=duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        //System.out.println("thread "+ getName() + " started working at:"+ System.currentTimeMillis());
        try{
            subscribeEvent(DeactivationEvent.class,(deactivationEvent)->{
                try {
                    Thread.sleep(Duration); // simulate deactivation by sleeping
                    diary.setR2D2Deactivate(this,System.currentTimeMillis());
                    this.complete(deactivationEvent,true);
                     // write the finish time of r2d2
                } catch (InterruptedException e) {
                    terminate();
                }
            });


            subscribeBroadcast(TerminateBroadcast.class,(terminate)->{
                diary.setR2D2Terminate(this,System.currentTimeMillis());
                this.terminate();
            });
            Main.leiaSignal.countDown(); // announce that we finished subscribing to all we needed here
        }
        catch (Exception e){

        }
    }

    public void call(DeactivationEvent deactivationEvent){
        try {
            Thread.sleep(Duration); // simulate deactivation by sleeping
            this.complete(deactivationEvent,true);
            diary.setR2D2Deactivate(this,System.currentTimeMillis()); // write the finish time of r2d2
        } catch (InterruptedException e) {
            terminate();
        }
    }

    public void call(TerminateBroadcast terminateBroadcast){
        this.terminate();
        diary.setR2D2Terminate(this,System.currentTimeMillis());
    }
}
