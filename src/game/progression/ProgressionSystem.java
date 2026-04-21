package game.progression;

import characters.Gerolando;

public class ProgressionSystem {
    private int nivel;
    private int xp;

    public ProgressionSystem() {
        this.nivel = 1;
        this.xp = 0;
    }

    public int getNivel() {
        return nivel;
    }

    public int getXP() {
        return xp;
    }

    public void ganarXP(int cantidad, Gerolando jugador) {
        if (cantidad <= 0) return;

        xp += cantidad;
        System.out.println("Ganaste " + cantidad + " XP.");

        checkNivelUp(jugador);
    }

    private void checkNivelUp(Gerolando jugador) {
        int xpNecesario = nivel * 10;

        while (xp >= xpNecesario) {
            xp -= xpNecesario;
            subirNivel(jugador);
            xpNecesario = nivel * 10;
        }
    }

    private void subirNivel(Gerolando jugador) {
        nivel++;

        jugador.aumentarVidaMax(5);
        jugador.aumentarFuerza(1);
        jugador.aumentarManaMax(1);
        jugador.aumentarVelocidadBase(1);
        jugador.restaurarVidaYMana();

        System.out.println("¡Subiste de nivel! Ahora eres nivel " + nivel);
    }
}