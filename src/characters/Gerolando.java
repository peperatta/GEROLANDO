package characters;

import items.Arma;
import items.Armadura;
import items.Item;

public class Gerolando {
//ATRIBUTOS
    public int vidaMax, vidaActual;
    public int nivel;
    public int fuerza;
    public int velocidad;
    private int xp;

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
        this.xp = 0;
        this.vidaActual = vidaMax;
        this.nivel = 1;
        this.fuerza = 5;
        this.velocidad = 20;

        this.manaMax = 3;
        this.manaActual = manaMax;

        this.inventario = new Inventario();
    }

    //Equipar
    public void equiparArma(Arma arma){
        this.armaEquipada = arma;
        System.out.println("Equipaste el arma: " + arma.getNombre());
    }
    public void equiparArmadura(Armadura armadura){
        this.armaduraEquipada = armadura;
        System.out.println("Equipaste la armadura: " + armadura.getNombre());
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
    public void atacar(Enemigo enemigo) {
        int danoBase = this.getAtaque();

        boolean critico = Math.random() < 0.2;
        if (critico) {
            danoBase *= 2;
            System.out.println("¡Golpe crítico!");
        }

        System.out.println("Gerolando ataque de tipo " + getTipoAtaque()+" con "+ this.armaEquipada.getNombre());

        enemigo.recibirAtaque(danoBase, this.getTipoAtaque());
    }
    public int getAtaque(){
        int base = fuerza;
        int arma = (armaEquipada != null) ? armaEquipada.getAtaque() : 0;
        return base + arma;
    }
    public String getTipoAtaque(){
        return armaEquipada.tipo;
    }
    public int getDefensa(){
        return (armaduraEquipada != null) ? armaduraEquipada.getDefensa() : 0;
    }
    public void recibirAtaque(int dano) {
        int reducido = Math.max(0, dano - getDefensa());
        vidaActual -= reducido;

        System.out.println("Gerolando recibió " + reducido + " de daño. Vida actual: " + vidaActual);

        if (vidaActual < 0) {
            vidaActual = 0;
        }
    }
    public void imprimirEstado() {
        System.out.println("Vida: " + vidaActual + "/" + vidaMax);
        System.out.println("Nivel: " + nivel);
        System.out.println("Fuerza: " + fuerza);
        System.out.println("Mana: " + manaActual);
        System.out.println("Velocidad: " + getVelocidad());
        System.out.println("Mana: " + manaActual + "/" + manaMax);
        System.out.println("XP: " + xp);
        System.out.println("Ataque: " + getAtaque());
        if (armaEquipada != null) {
            System.out.println("Arma equipada: " + armaEquipada.getNombre());
        } else {
            System.out.println("No hay arma equipada.");
        }
        if (armaduraEquipada != null) {
            System.out.println("Armadura equipada: " + armaduraEquipada.getNombre());
        } else {
            System.out.println("No hay armadura equipada.");
        }
    }



}
