package game;

import characters.Enemigo;
import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.factory.EnemigoFactory;
import data.loader.ArmaduraLoader;
import data.loader.EnemigoLoader;
import data.model.DatosArmadura;
import data.model.DatosEnemigo;
import items.*;

import data.loader.ArmaLoader;
import data.model.DatosArma;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Cargar catálogos desde JSON
        Map<String, DatosArma> catalogoArma =
                ArmaLoader.cargar("src/assets/data/armas.json");
        Map<String, DatosArmadura> catalogoArmadura =
                ArmaduraLoader.cargar("src/assets/data/armaduras.json");
        Map<String, DatosEnemigo> catalogoEnemigo =
                EnemigoLoader.cargar("src/assets/data/enemigos.json");

        // Crear factories
        ArmaFactory factory = new ArmaFactory(catalogoArma);
        ArmaduraFactory armaduraFactory = new ArmaduraFactory(catalogoArmadura);
        EnemigoFactory enemigoFactory = new EnemigoFactory(catalogoEnemigo);

        // Crear items
        Arma espadaHierro = factory.crear("espada_hierro");
        Arma espadaMadera = factory.crear("espada_madera");
        Armadura RopaVieja = armaduraFactory.crear("ropa_vieja");
        Enemigo finko = enemigoFactory.crear("finko");


        Gerolando gerolando = new Gerolando();
        gerolando.inventario.agregar(espadaHierro);
        gerolando.inventario.agregar(espadaMadera);
        gerolando.inventario.agregar(RopaVieja);
        gerolando.equiparArmadura(RopaVieja);
        gerolando.equiparArma(espadaHierro);
        Combate.iniciarCombate(gerolando, finko);
        System.out.println(gerolando.getXP());

    }
}