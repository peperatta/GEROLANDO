package data.factory;

import data.model.DatosPocion;
import items.Pocion;

import java.util.Map;

public class PotionFactory {

    private Map<String, DatosPocion> catalogo;

    public PotionFactory(Map<String, DatosPocion> catalogo) {
        this.catalogo = catalogo;
    }

    public Pocion crear(String id) {

        DatosPocion d = catalogo.get(id);

        if (d == null) {
            throw new RuntimeException("Poción no encontrada: " + id);
        }

        return new Pocion(d.nombre,  d.precio, d.tipo, d.valor, d.spritePath);
    }
}

