package game.combat;

public class CombatResult {
    private String mensaje;
    private boolean combateTerminado;
    private boolean jugadorGano;

    public CombatResult(String mensaje, boolean combateTerminado, boolean jugadorGano) {
        this.mensaje = mensaje;
        this.combateTerminado = combateTerminado;
        this.jugadorGano = jugadorGano;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isCombateTerminado() {
        return combateTerminado;
    }

    public boolean isJugadorGano() {
        return jugadorGano;
    }
}