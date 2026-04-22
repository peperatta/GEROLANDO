package characters;

import java.util.List;

public class Enemigo {
    public String nombre;
    public int vida;
    public int vidaActual;
    public int ataque;
    public int defensa;
    public int velocidad;
    public String debilidad;
    public String spritePath;

    private List<String> drops;
    private int dropChance;

    public Enemigo(String nombre, int vida, int ataque, int defensa, int velocidad,
                   String debilidad, String spritePath, List<String> drops, int dropChance) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaActual = vida;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.debilidad = debilidad;
        this.spritePath = spritePath;
        this.drops = drops;
        this.dropChance = dropChance;
    }

    public int getVida() {
        return vida;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public List<String> getDrops() {
        return drops;
    }

    public int getDropChance() {
        return dropChance;
    }

    public void recibirAtaque(int dano, String tipoAtaque) {
        int danoFinal = Math.max(0, dano - defensa);

        if (debilidad != null && debilidad.equalsIgnoreCase(tipoAtaque)) {
            danoFinal *= 2;
            System.out.println("¡Es súper efectivo! " + nombre + " es débil a " + tipoAtaque);
        }

        vidaActual -= danoFinal;

        if (vidaActual < 0) {
            vidaActual = 0;
        }

        System.out.println(nombre + " recibió " + danoFinal + " de daño. Vida actual: " + vidaActual);
    }

    public void atacar(Gerolando jugador) {
        System.out.println(nombre + " ataca.");
        jugador.recibirAtaque(ataque);
    }
}