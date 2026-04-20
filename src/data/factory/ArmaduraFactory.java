package data.factory;

import data.model.DatosArmadura;
import items.Armadura;

import java.util.Map;

public class ArmaduraFactory {

    private Map<String, DatosArmadura> catalogo;

    public ArmaduraFactory(Map<String, DatosArmadura> catalogo) {
        this.catalogo = catalogo;
    }

    public Armadura crear(String id) {

        DatosArmadura d = catalogo.get(id);

        if (d == null) {
            throw new RuntimeException("Armadura no encontrada: " + id);
        }

        return new Armadura(d.nombre, d.defensa);
    }
}

