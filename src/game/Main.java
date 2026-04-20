package game;

import characters.Gerolando;
import data.factory.ArmaFactory;
import items.Arma;
import items.ArmaMelee;
import items.ArmaRango;
import items.ArmaMagica;

import data.loader.ArmaLoader;
import data.model.DatosArma;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // 1. Cargar catálogo desde JSON
        Map<String, DatosArma> catalogo =
                ArmaLoader.cargar("src/assets/data/armas.json");

        // 2. Obtener un arma por ID
        DatosArma arma = catalogo.get("espada_hierro");
        ArmaFactory factory= new ArmaFactory(catalogo);
        Arma espadaHierro = factory.crear("espada_hierro");

        Gerolando gerolando = new Gerolando();
        gerolando.inventario.agregar(espadaHierro);
        gerolando.usarItem(espadaHierro);
        gerolando.usarItem(espadaHierro);

    }
}