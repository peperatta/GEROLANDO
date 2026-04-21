package game.world;

public class Biomes {
    public static final Biome PLAYA = new Biome(
            "playa",
            "Playa",
            "Una costa tranquila con arena caliente y brisa marina.",
            new String[]{"finko"},
            30
    );

    public static final Biome BOSQUE = new Biome(
            "bosque",
            "Bosque",
            "Árboles densos, sombras largas y sonidos extraños.",
            new String[]{"calabazo"},
            35
    );

    public static final Biome CUEVA = new Biome(
            "cueva",
            "Cueva",
            "Oscura, húmeda y peligrosa. Algo se mueve entre las rocas.",
            new String[]{"diablo"},
            40
    );
}