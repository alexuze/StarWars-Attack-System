package bgu.spl.mics.application.passiveObjects;

/**
 * A class that represents the input from the json file
 */
public class ProgramParameters {
    private Attack[] attacks;
    private int R2D2;
    private int Lando;
    private int Ewoks;
    public ProgramParameters(Attack[] attacks,int r2D2,int lando,int ewoks)
    {
        this.attacks = attacks;
        this.R2D2 = r2D2;
        this.Lando =  lando;
        this.Ewoks = ewoks;
    }

    public int getEwoks() {
        return Ewoks;
    }
    public void setEwoks(int ewoks) {
        Ewoks = ewoks;
    }
    public int getLando() {
        return Lando;
    }
    public void setLando(int lando) {
        Lando = lando;
    }
    public int getR2D2() {
        return R2D2;
    }
    public void setR2D2(int r2d2) {
        R2D2 = r2d2;
    }
    public Attack[] getAttacks() {
        return attacks;
    }
    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }
}
