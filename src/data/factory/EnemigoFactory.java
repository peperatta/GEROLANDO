package data.factory;

import characters.Enemigo;
import data.model.DatosEnemigo;

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

        return new Enemigo(d.nombre, d.vida, d.ataque, d.defensa, d.spritePath, d.velocidad, d.debilidad);
    }
}

