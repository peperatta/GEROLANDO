package characters;

import items.Arma;
import items.Armadura;
import items.Item;

public class Gerolando {
//ATRIBUTOS
    public int vidaMax, vidaActual;
    public int nivel;
    public int fuerza;
    public int magia;
    public int velocidad;

    //Porcentajes
    public int manaMax, manaActual;

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
        this.velocidad = 20;

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

        if (item instanceof Arma ) {
            if (armaEquipada == null || !armaEquipada.equals(item)) {
                equiparArma((Arma) item);
                System.out.println("Equipaste el arma: " + item.getNombre());
            }
            else{item.usar();}
        }

        if (item instanceof Armadura) {
            if (armaduraEquipada == null || !armaduraEquipada.equals(item)) {
                equiparArmadura((Armadura) item);
                System.out.println("Equipaste la armadura: " + item.getNombre());
            }
            else{item.usar();}
        }
    }

    //Combate
    public int getVelocidad(){
        int base = velocidad;
        int arma = (armaEquipada != null) ? armaEquipada.getPeso() : 0;
        int armadura = (armaduraEquipada != null) ? armaduraEquipada.getPeso() : 0;
        return base - (arma + armadura);
    }
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
