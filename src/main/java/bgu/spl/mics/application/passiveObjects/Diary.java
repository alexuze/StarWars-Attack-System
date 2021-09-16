package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.services.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks;



    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private static class SingletonHolder{
        public static Diary instance = new Diary(); // make the diary singleton to share it among all the microservices
    }
    private Diary()
    {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }
    public static Diary getInstance()
    {
        return SingletonHolder.instance;
    }

    /**
     * Setters
     */


    /**
     * make sure only C3PO and hanSolo will increment the total attacks, that way they will increment if they send themselves in the function
     * @param microservice
     */
    public void increaseAttack(HanSoloMicroservice microservice)
    {
        Integer currTotal;
        do {
             currTotal = totalAttacks.get();
        }
        while(!totalAttacks.compareAndSet(currTotal,currTotal+1));
    }
    public void increaseAttack(C3POMicroservice microservice)
    {
        Integer currTotal;
        do {
            currTotal = totalAttacks.get();
        }
        while(!totalAttacks.compareAndSet(currTotal,currTotal+1));
    }
    public void setC3POFinish(C3POMicroservice microservice,long c3POFinish)
    {
        C3POFinish = c3POFinish;
    }
    public void setHanSoloFinish(HanSoloMicroservice microservice,long hanSoloFinish)
    {
        HanSoloFinish = hanSoloFinish;
    }
    public void setR2D2Deactivate(R2D2Microservice microservice,long r2D2Deactivate)
    {
        R2D2Deactivate = r2D2Deactivate;
    }
    public void setLeiaTerminate(LeiaMicroservice microservice,long leiaTerminate)
    {
        LeiaTerminate = leiaTerminate;
    }
    public void setHanSoloTerminate(HanSoloMicroservice microservice,long hanSoloTerminate)
    {
        HanSoloTerminate = hanSoloTerminate;
    }
    public void setR2D2Terminate(R2D2Microservice microservice,long r2D2Terminate)
    {
        R2D2Terminate = r2D2Terminate;
    }
    public void setLandoTerminate(LandoMicroservice microservice,long landoTerminate)
    {
        LandoTerminate = landoTerminate;
    }
    public void setC3POTerminate(C3POMicroservice microservice,long c3POTerminate)
    {
        C3POTerminate = c3POTerminate;
    }


    /**
     * Getters
     */
    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public void resetNumberAttacks() // this function is used in tests only
    {
        Integer currTotal;
        do {
            currTotal = totalAttacks.get();
        }
        while(!totalAttacks.compareAndSet(currTotal,0));
    }
}
