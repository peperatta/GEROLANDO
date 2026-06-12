package world.map;

import game.world.Biome;

import java.awt.Color;

public class TileDefinition {
    private final int id;
    private final String nombre;
    private final boolean caminable;
    private final Biome biome;
    private final String texturePath;
    private final String battleBackgroundPath;
    private final Color fallbackColor;

    public TileDefinition(int id,
                          String nombre,
                          boolean caminable,
                          Biome biome,
                          String texturePath,
                          String battleBackgroundPath,
                          Color fallbackColor) {
        this.id = id;
        this.nombre = nombre;
        this.caminable = caminable;
        this.biome = biome;
        this.texturePath = texturePath;
        this.battleBackgroundPath = battleBackgroundPath;
        this.fallbackColor = fallbackColor;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isCaminable() {
        return caminable;
    }

    public Biome getBiome() {
        return biome;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getBattleBackgroundPath() {
        return battleBackgroundPath;
    }

    public Color getFallbackColor() {
        return fallbackColor;
    }
}
