package game;

import characters.Enemigo;

public class ExplorationResult {
    private String mensaje;
    private boolean hayEncuentro;
    private Enemigo enemigo;

    private ExplorationResult(String mensaje, boolean hayEncuentro, Enemigo enemigo) {
        this.mensaje = mensaje;
        this.hayEncuentro = hayEncuentro;
        this.enemigo = enemigo;
    }

    public static ExplorationResult sinEncuentro(String mensaje) {
        return new ExplorationResult(mensaje, false, null);
    }

    public static ExplorationResult conEncuentro(String mensaje, Enemigo enemigo) {
        return new ExplorationResult(mensaje, true, enemigo);
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean hayEncuentro() {
        return hayEncuentro;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }
}