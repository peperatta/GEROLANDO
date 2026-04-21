package game.world;

public class Biome {
    private String id;
    private String nombre;
    private String descripcion;
    private String[] enemigosDisponibles;
    private int probabilidadEncuentro;

    public Biome(String id, String nombre, String descripcion, String[] enemigosDisponibles, int probabilidadEncuentro) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.enemigosDisponibles = enemigosDisponibles;
        this.probabilidadEncuentro = probabilidadEncuentro;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String[] getEnemigosDisponibles() {
        return enemigosDisponibles;
    }

    public int getProbabilidadEncuentro() {
        return probabilidadEncuentro;
    }
}