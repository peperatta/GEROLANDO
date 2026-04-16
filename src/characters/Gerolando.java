package characters;

import items.Arma;
import items.Armadura;
import items.Item;

public class Gerolando {
//ATRIBUTOS
    int vidaMax, vidaActual;
    int nivel;
    int fuerza;
    int magia;

    //Porcentajes
    int manaMax, manaActual;

    //Inventario
    private Arma armaEquipada;
    private Armadura armaduraEquipada;
    public Inventario inventario;

//FUNCIONES
    //Constructor
    public Gerolando(){
        this.vidaMax = 100;
        this.vidaActual = vidaMax;
        this.nivel = 1;
        this.fuerza = 5;
        this.magia = 5;

        this.manaMax = 3;
        this.manaActual = manaMax;

        this.inventario = new Inventario();
    }

    //Equipar
    public void equiparArma(Arma arma){
        this.armaEquipada = arma;
    }
    public void equiparArmadura(Armadura armadura){
        this.armaduraEquipada = armadura;
    }

    public void usarItem(Item item) {

        if (item == null) return;

        item.usar();

        if (item instanceof Arma) {
            equiparArma((Arma) item);
            System.out.println("Equipaste el arma: " + item.getNombre());
        }

        if (item instanceof Armadura) {
            equiparArmadura((Armadura) item);
            System.out.println("Equipaste la armadura: " + item.getNombre());
        }
    }

    //Combate
    public int getAtaque(){
        int base = fuerza;
        int arma = (armaEquipada != null) ? armaEquipada.getAtaque() : 0;
        return base + arma;
    }
    public int getDefensa(){
        return (armaduraEquipada != null) ? armaduraEquipada.getDefensa() : 0;
    }
    public void recibirDano(int dano) {
        int reducido = Math.max(0, dano - getDefensa());
        vidaActual -= reducido;

        if (vidaActual < 0) {
            vidaActual = 0;
        }
    }



}
