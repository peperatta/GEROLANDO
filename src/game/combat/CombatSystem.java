package game.combat;

import characters.Enemigo;
import characters.Gerolando;
import items.Item;

public class CombatSystem {
    private Gerolando jugador;
    private Enemigo enemigo;
    private boolean turnoJugador;

    public CombatSystem(Gerolando jugador, Enemigo enemigo) {
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.turnoJugador = jugador.getVelocidad() >= enemigo.getVelocidad();
    }

    public String iniciarCombate() {
        StringBuilder mensaje = new StringBuilder();

        mensaje.append("¡Comienza el combate contra ")
                .append(enemigo.nombre)
                .append("!\n");

        if (turnoJugador) {
            mensaje.append("Gerolando es más rápido y ataca primero.");
        } else {
            mensaje.append(enemigo.nombre).append(" es más rápido y ataca primero.");
        }

        return mensaje.toString();
    }

    public boolean esTurnoJugador() {
        return turnoJugador;
    }

    public CombatResult atacar() {
        if (!turnoJugador) {
            return new CombatResult("No es turno de Gerolando.", false, false);
        }

        jugador.atacar(enemigo);

        if (enemigo.vidaActual <= 0) {
            return new CombatResult("¡Ganaste el combate!", true, true);
        }

        turnoJugador = false;
        return turnoEnemigo();
    }

    public CombatResult usarItem(int itemIndex) {
        if (!turnoJugador) {
            return new CombatResult("No es turno de Gerolando.", false, false);
        }

        if (jugador.inventario.size() == 0) {
            return new CombatResult("El inventario está vacío.", false, false);
        }

        if (itemIndex < 0 || itemIndex >= jugador.inventario.size()) {
            return new CombatResult("Índice de item no válido.", false, false);
        }

        Item item = jugador.inventario.getItems().get(itemIndex);
        jugador.usarItem(item);

        if (enemigo.vidaActual <= 0) {
            return new CombatResult("¡Ganaste el combate!", true, true);
        }

        turnoJugador = false;
        return turnoEnemigo();
    }

    public CombatResult turnoEnemigo() {
        if (turnoJugador) {
            return new CombatResult("Todavía es turno de Gerolando.", false, false);
        }

        enemigo.atacar(jugador);

        if (!jugador.estaVivo()) {
            return new CombatResult("Has sido derrotado...", true, false);
        }

        turnoJugador = true;
        return new CombatResult("", false, false);
    }

    public Gerolando getJugador() {
        return jugador;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }
}