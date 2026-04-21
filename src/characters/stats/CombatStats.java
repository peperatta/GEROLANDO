package characters.stats;

public class CombatStats {
    private int vidaMax;
    private int vidaActual;
    private int fuerza;
    private int velocidad;
    private int manaMax;
    private int manaActual;

    public CombatStats(int vidaMax, int fuerza, int velocidad, int manaMax) {
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.fuerza = fuerza;
        this.velocidad = velocidad;
        this.manaMax = manaMax;
        this.manaActual = manaMax;
    }

    public int getVidaMax() {
        return vidaMax;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getFuerza() {
        return fuerza;
    }

    public int getVelocidadBase() {
        return velocidad;
    }

    public int getManaMax() {
        return manaMax;
    }

    public int getManaActual() {
        return manaActual;
    }

    public void setVidaActual(int vidaActual) {
        if (vidaActual < 0) {
            this.vidaActual = 0;
        } else if (vidaActual > vidaMax) {
            this.vidaActual = vidaMax;
        } else {
            this.vidaActual = vidaActual;
        }
    }

    public void setManaActual(int manaActual) {
        if (manaActual < 0) {
            this.manaActual = 0;
        } else if (manaActual > manaMax) {
            this.manaActual = manaMax;
        } else {
            this.manaActual = manaActual;
        }
    }

    public void aumentarVidaMax(int cantidad) {
        this.vidaMax += cantidad;
    }

    public void aumentarFuerza(int cantidad) {
        this.fuerza += cantidad;
    }

    public void aumentarVelocidadBase(int cantidad) {
        this.velocidad += cantidad;
    }

    public void aumentarManaMax(int cantidad) {
        this.manaMax += cantidad;
    }

    public void restaurarVidaYMana() {
        this.vidaActual = this.vidaMax;
        this.manaActual = this.manaMax;
    }

    public void recibirDanio(int danoReducido) {
        setVidaActual(this.vidaActual - danoReducido);
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }
}