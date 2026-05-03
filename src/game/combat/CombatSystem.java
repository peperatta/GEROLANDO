package game.combat;

import characters.Enemigo;
import characters.Gerolando;

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
        if (turnoJugador) {
            return "¡Apareció un " + enemigo.nombre + "!\nGerolando ataca primero.";
        }

        return "¡Apareció un " + enemigo.nombre + "!\n" + enemigo.nombre + " ataca primero.";
    }

    public boolean esTurnoJugador() {
        return turnoJugador;
    }

    public CombatResult atacar() {
        if (!turnoJugador) {
            return new CombatResult("No es turno de Gerolando.", false, false);
        }

        StringBuilder mensaje = new StringBuilder();

        int vidaAntes = enemigo.vidaActual;

        jugador.atacar(enemigo);

        int danoReal = vidaAntes - enemigo.vidaActual;

        mensaje.append("Gerolando atacó.\n");
        mensaje.append(enemigo.nombre).append(" recibió ").append(danoReal).append(" de daño.");

        if (enemigo.vidaActual <= 0) {
            mensaje.append("\n¡Ganaste el combate!");
            return new CombatResult(mensaje.toString(), true, true);
        }

        turnoJugador = false;

        CombatResult resultadoEnemigo = turnoEnemigo();
        mensaje.append("\n").append(resultadoEnemigo.getMensaje());

        return new CombatResult(
                mensaje.toString(),
                resultadoEnemigo.isCombateTerminado(),
                resultadoEnemigo.isJugadorGano()
        );
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

        var item = jugador.inventario.getItems().get(itemIndex);
        String nombreItem = item.getNombre();

        jugador.usarItem(item);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Gerolando ").append(obtenerVerboItem(item)).append(" ").append(nombreItem).append(".");

        turnoJugador = false;

        CombatResult resultadoEnemigo = turnoEnemigo();
        mensaje.append("\n").append(resultadoEnemigo.getMensaje());

        return new CombatResult(
                mensaje.toString(),
                resultadoEnemigo.isCombateTerminado(),
                resultadoEnemigo.isJugadorGano()
        );
    }
    private String obtenerVerboItem(items.Item item) {
        switch (item.getTipo()) {
            case CONSUMIBLE:
                return "tomó";

            case ARMA:
            case ARMADURA:
                return "equipó";

            default:
                return "usó";
        }
    }
    private CombatResult turnoEnemigo() {
        int vidaAntes = jugador.getVidaActual();

        enemigo.atacar(jugador);

        int danoReal = vidaAntes - jugador.getVidaActual();

        StringBuilder mensaje = new StringBuilder();
        mensaje.append(enemigo.nombre).append(" atacó.\n");
        mensaje.append("Gerolando recibió ").append(danoReal).append(" de daño.");

        if (!jugador.estaVivo()) {
            mensaje.append("\nHas sido derrotado...");
            return new CombatResult(mensaje.toString(), true, false);
        }

        turnoJugador = true;
        return new CombatResult(mensaje.toString(), false, false);
    }

    public Gerolando getJugador() {
        return jugador;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }
}