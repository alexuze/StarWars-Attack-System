package bgu.spl.mics.application.passiveObjects;


import java.util.*;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Map<Integer,Ewok> EwokLIst; // Map that stores ewoks - used map to find ewok by his serial number faster


    /**
     * Thread safe singleton implementation
     */
    private static class SingletonHolder{
        private static Ewoks instance = new Ewoks();
    }

    /**
     * A private inner class that represents a waiting microservice that provided his lock and the list of required ewoks for his attack
     */

    private Ewoks()
    {
        EwokLIst = new HashMap<>();
    }
    public static Ewoks GetEwoksInstance() {
        return SingletonHolder.instance;
    }

    public boolean AssignEwoks(List<Integer> serials)  {
        Collections.sort(serials); // sort so every thread will go through the same order and that way we avoid deadlocks
        for(Integer integer : serials)
        {
            Ewok temp = EwokLIst.get(integer);
            synchronized (temp){ // lock every time on different ewok to be able to wait when some ewok is not available
                while (!temp.isAvailable())
                {
                    try {
                        temp.wait(); // if the ewok is not available we will wait for him to be released
                    } catch (InterruptedException e) {

                    }
                }
                try {
                    temp.acquire(); // after we waited we acquire the ewok
                } catch (Exception e) {

                }
            }
        }
        return true;
    }

    public void ReleaseEwoks(List<Integer> serials){
        Collections.sort(serials); // sort so every thread will go through the same order and that way we avoid deadlocks
        for(Integer integer : serials) // release all the ewoks
        {
            Ewok temp = EwokLIst.get(integer);
            synchronized (temp){ // we lock every time on a specific ewok to awake the waiting threads(notify must be inside synchronized block)
                temp.release(); // release the ewok
                temp.notifyAll(); // notify the waiting threads that this ewok is not free
            }

        }
    }
    public void addEwoks(int ewoks) // method that adds the ewoks to the list
    {
        for(int i=1;i<=ewoks;i++)
        {
            EwokLIst.putIfAbsent(i,new Ewok(i));
        }
    }

}
