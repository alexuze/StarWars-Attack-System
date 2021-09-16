package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	private Integer serialNumber;
	private boolean available;
	
    public Ewok(int serialNumber)
    {
        this.serialNumber = serialNumber;
        available = true;
    }
    /**
     * Acquires an Ewok
     */
    public void acquire() throws Exception {
		if(!isAvailable())
		    throw new Exception("Ewok number "+serialNumber+" is not available");
		available = false;
    }

    /**
     * release an Ewok
     */
    public void release() {
    	available = true;
    }
    public boolean isAvailable()
    {
        return available;
    }
    public int getSerialNumber(){return serialNumber;}

}
