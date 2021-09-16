package bgu.spl.mics;
import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {
    private Ewok ewok;
    @BeforeEach
    public void setEwok() {ewok = new Ewok(1);}
    @Test
    public void TestAcquire() {
        assertTrue(ewok.isAvailable());
        try
        {
            ewok.acquire();
        }
        catch (Exception e){}
        assertFalse(ewok.isAvailable());
    }
    @Test
    public void TestRelease()
    {
        try
        {
            ewok.acquire();
        }
        catch (Exception e){}
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

}
