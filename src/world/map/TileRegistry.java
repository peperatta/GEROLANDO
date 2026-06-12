package world.map;

import game.world.Biome;
import game.world.Biomes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class TileRegistry {
    private static final String DEFAULT_TEXTURE = "/assets/tiles/grass.png";
    private static final String PLAYA_BACKGROUND = "/assets/tiles/backgrounds/playa.png";
    private static final String BOSQUE_BACKGROUND = "/assets/tiles/backgrounds/bosque.png";
    private static final String CUEVA_BACKGROUND = "/assets/tiles/backgrounds/cueva.png";
    private static final Color DEFAULT_COLOR = new Color(50, 150, 70);

    private static final Map<Integer, TileDefinition> TILES = new HashMap<>();

    static {
        registrar(0, "Arena", true, Biomes.PLAYA, "/assets/tiles/sand.png", PLAYA_BACKGROUND, DEFAULT_COLOR);
        registrar(1, "Pared", false, Biomes.PLAYA, "/assets/tiles/wall.png", PLAYA_BACKGROUND, Color.DARK_GRAY);
        registrar(2, "Agua", false, Biomes.PLAYA, "/assets/tiles/water.png", PLAYA_BACKGROUND, Color.BLUE);
        registrar(3, "Piedra de arena", false, Biomes.PLAYA, "/assets/tiles/sand_stone.png", PLAYA_BACKGROUND, Color.GREEN);
        registrar(4, "Concha", false, Biomes.PLAYA, "/assets/tiles/sand_shell.png", PLAYA_BACKGROUND, Color.GRAY);

        registrarRango(5, 9, "Tile playa", true, Biomes.PLAYA, DEFAULT_TEXTURE, PLAYA_BACKGROUND, DEFAULT_COLOR);

        registrar(10, "Pasto", true, Biomes.BOSQUE, "/assets/tiles/grass.png", BOSQUE_BACKGROUND, DEFAULT_COLOR);
        registrarRango(11, 19, "Tile bosque", true, Biomes.BOSQUE, DEFAULT_TEXTURE, BOSQUE_BACKGROUND, DEFAULT_COLOR);

        registrar(20, "Roca", true, Biomes.CUEVA, "/assets/tiles/stone.png", CUEVA_BACKGROUND, DEFAULT_COLOR);
        registrarRango(21, 29, "Tile cueva", true, Biomes.CUEVA, DEFAULT_TEXTURE, CUEVA_BACKGROUND, DEFAULT_COLOR);
    }

    private TileRegistry() {
    }

    public static TileDefinition obtener(int tileId) {
        return TILES.getOrDefault(tileId, crearFallback(tileId));
    }

    public static boolean esCaminable(int tileId) {
        return obtener(tileId).isCaminable();
    }

    public static Biome obtenerBiome(int tileId) {
        return obtener(tileId).getBiome();
    }

    private static void registrar(int id,
                                  String nombre,
                                  boolean caminable,
                                  Biome biome,
                                  String texturePath,
                                  String battleBackgroundPath,
                                  Color fallbackColor) {
        TILES.put(id, new TileDefinition(id, nombre, caminable, biome, texturePath, battleBackgroundPath, fallbackColor));
    }

    private static void registrarRango(int inicio,
                                       int fin,
                                       String nombreBase,
                                       boolean caminable,
                                       Biome biome,
                                       String texturePath,
                                       String battleBackgroundPath,
                                       Color fallbackColor) {
        for (int id = inicio; id <= fin; id++) {
            registrar(id, nombreBase + " " + id, caminable, biome, texturePath, battleBackgroundPath, fallbackColor);
        }
    }

    private static TileDefinition crearFallback(int tileId) {
        return new TileDefinition(
                tileId,
                "Tile desconocido",
                true,
                null,
                DEFAULT_TEXTURE,
                null,
                DEFAULT_COLOR
        );
    }
}
