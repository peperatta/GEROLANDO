package game;

import characters.Gerolando;
import items.Arma;
import items.ArmaMelee;
import items.ArmaRango;
import items.ArmaMagica;

public class Main {
    public static void main(String[] args) {
        Gerolando gerolando = new Gerolando();
        
        // Crear 11 armas
        ArmaMelee espada1 = new ArmaMelee("Espada de Hierro", 10);
        ArmaMelee espada2 = new ArmaMelee("Espada de Acero", 15);
        ArmaMelee espada3 = new ArmaMelee("Espada Legendaria", 25);
        ArmaMelee espada4 = new ArmaMelee("Espada Maldita", 20);
        
        ArmaRango arco1 = new ArmaRango("Arco de Madera", 8);
        ArmaRango arco2 = new ArmaRango("Arco de Elfo", 12);
        ArmaRango arco3 = new ArmaRango("Ballesta", 18);
        
        ArmaMagica varita1 = new ArmaMagica("Varita de Fuego", 14);
        ArmaMagica varita2 = new ArmaMagica("Varita de Hielo", 16);
        ArmaMagica varita3 = new ArmaMagica("Varita Suprema", 30);
        ArmaMagica varita4 = new ArmaMagica("Varita Oscura", 22);
        
        // Agregar las armas al inventario
        gerolando.inventario.agregar(espada1);
        gerolando.inventario.agregar(espada2);
        gerolando.inventario.agregar(espada3);
        gerolando.inventario.agregar(espada4);
        gerolando.inventario.agregar(arco1);
        gerolando.inventario.agregar(arco2);
        gerolando.inventario.agregar(arco3);
        gerolando.inventario.agregar(varita1);
        gerolando.inventario.agregar(varita2);
        gerolando.inventario.agregar(varita3);
        gerolando.inventario.agregar(varita4); // Esta debería fallar
        gerolando.inventario.eliminar(varita3);

        gerolando.inventario.agregar(varita4); // Esta debería fallar
        
        gerolando.inventario.mostrarInventario();
    }
}
