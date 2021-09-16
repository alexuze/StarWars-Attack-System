package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
	private Attack attackDetails;
	public AttackEvent(Attack attack)
    {
        attackDetails = attack;
    }
    public int getDuration()
    {
        return attackDetails.GetDuration();
    }
    public List<Integer> getSerials()
    {
        return attackDetails.GetSerials();
    }
}
