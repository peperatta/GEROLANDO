package data.factory;

import data.model.DatosEnemigo;
import characters.Enemigo;

import java.util.Map;

public class EnemigoFactory {

    private Map<String, DatosEnemigo> catalogo;

    public EnemigoFactory(Map<String, DatosEnemigo> catalogo) {
        this.catalogo = catalogo;
    }

    public Enemigo crear(String id) {
        DatosEnemigo d = catalogo.get(id);

        if (d == null) {
            throw new RuntimeException("Enemigo no encontrado: " + id);
        }

        return new Enemigo(
                d.nombre,
                d.vida,
                d.ataque,
                d.defensa,
                d.velocidad,
                d.debilidad,
                d.spritePath,
                d.drops,
                d.dropChance
        );
    }
}