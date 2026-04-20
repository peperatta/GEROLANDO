package data.factory;

import data.model.DatosArma;
import items.Arma;
import items.ArmaMagica;
import items.ArmaMelee;
import items.ArmaRango;

import java.util.Map;

public class ArmaFactory {

    private Map<String, DatosArma> catalogo;

    public ArmaFactory(Map<String, DatosArma> catalogo) {
        this.catalogo = catalogo;
    }

    public Arma crear(String id) {

        DatosArma d = catalogo.get(id);

        switch (d.tipo) {
            case "melee":
                return new ArmaMelee(d.nombre, d.ataque, d.peso, d.spritePath);

            case "rango":
                return new ArmaRango(d.nombre, d.ataque, d.peso, d.spritePath);

            case "magica":
                return new ArmaMagica(d.nombre, d.ataque, d.peso, d.spritePath);

            default:
                throw new RuntimeException("Tipo inválido");
        }
    }
}