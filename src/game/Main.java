package game;

import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.loader.ArmaduraLoader;
import data.model.DatosArmadura;
import items.*;

import data.loader.ArmaLoader;
import data.model.DatosArma;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // 1. Cargar catálogo desde JSON
        Map<String, DatosArma> catalogoArma =
                ArmaLoader.cargar("src/assets/data/armas.json");
        Map<String, DatosArmadura> catalogoArmadura =
                ArmaduraLoader.cargar("src/assets/data/armaduras.json");

        // 2. Obtener un arma por ID
        DatosArma arma = catalogoArma.get("espada_hierro");
        ArmaFactory factory= new ArmaFactory(catalogoArma);
        Arma espadaHierro = factory.crear("espada_hierro");

        // Obtener armadura por ID
        DatosArmadura armadura = catalogoArmadura.get("ropa_vieja");
        ArmaduraFactory armaduraFactory = new ArmaduraFactory(catalogoArmadura);
        Armadura RopaVieja = armaduraFactory.crear("ropa_vieja");

        Gerolando gerolando = new Gerolando();
        gerolando.inventario.agregar(espadaHierro);
        gerolando.inventario.agregar(RopaVieja);
        gerolando.usarItem(RopaVieja);
        gerolando.usarItem(espadaHierro);
        gerolando.usarItem(espadaHierro);

    }
}