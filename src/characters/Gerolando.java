package characters;

import characters.equipment.EquipmentSystem;
import game.progression.ProgressionSystem;
import items.Arma;
import items.Armadura;
import items.Item;

public class Gerolando {
    // ATRIBUTOS
    public int vidaMax, vidaActual;
    public int fuerza;
    public int velocidad;

    // Mana
    public int manaMax, manaActual;

    // Inventario
    public Inventario inventario;

    // Sistemas
    private ProgressionSystem progressionSystem;
    private EquipmentSystem equipmentSystem;

    // CONSTRUCTOR
    public Gerolando() {
        this.vidaMax = 100;
        this.vidaActual = vidaMax;

        this.fuerza = 5;
        this.velocidad = 20;

        this.manaMax = 3;
        this.manaActual = manaMax;

        this.inventario = new Inventario();

        this.progressionSystem = new ProgressionSystem();
        this.equipmentSystem = new EquipmentSystem();
    }

    // =========================
    // PROGRESIÓN
    // =========================
    public int getNivel() {
        return progressionSystem.getNivel();
    }

    public int getXP() {
        return progressionSystem.getXP();
    }

    public void ganarXP(int cantidad) {
        progressionSystem.ganarXP(cantidad, this);
    }

    public void aumentarVidaMax(int cantidad) {
        this.vidaMax += cantidad;
    }

    public void aumentarFuerza(int cantidad) {
        this.fuerza += cantidad;
    }

    public void aumentarManaMax(int cantidad) {
        this.manaMax += cantidad;
    }

    public void aumentarVelocidadBase(int cantidad) {
        this.velocidad += cantidad;
    }

    public void restaurarVidaYMana() {
        this.vidaActual = this.vidaMax;
        this.manaActual = this.manaMax;
    }

    // =========================
    // EQUIPO
    // =========================
    public void equiparArma(Arma arma) {
        equipmentSystem.equiparArma(arma);
    }

    public void equiparArmadura(Armadura armadura) {
        equipmentSystem.equiparArmadura(armadura);
    }

    public void usarItem(Item item) {
        equipmentSystem.usarItem(item);
    }

    public Arma getArmaEquipada() {
        return equipmentSystem.getArmaEquipada();
    }

    public Armadura getArmaduraEquipada() {
        return equipmentSystem.getArmaduraEquipada();
    }

    public EquipmentSystem getEquipmentSystem() {
        return equipmentSystem;
    }

    // =========================
    // COMBATE
    // =========================
    public int getVelocidad() {
        return velocidad - equipmentSystem.getPenalizacionPeso();
    }

    public int getAtaque() {
        return fuerza + equipmentSystem.getAtaqueBonus();
    }

    public int getDefensa() {
        return equipmentSystem.getDefensaBonus();
    }

    public String getTipoAtaque() {
        return equipmentSystem.getTipoAtaque();
    }

    public void atacar(Enemigo enemigo) {
        int danoBase = this.getAtaque();

        boolean critico = Math.random() < 0.2;
        if (critico) {
            danoBase *= 2;
            System.out.println("¡Golpe crítico!");
        }

        Arma armaEquipada = getArmaEquipada();
        String nombreArma = (armaEquipada != null) ? armaEquipada.getNombre() : "puños";

        System.out.println("Gerolando ataca de tipo " + getTipoAtaque() + " con " + nombreArma);

        enemigo.recibirAtaque(danoBase, this.getTipoAtaque());
    }

    public void recibirAtaque(int dano) {
        int reducido = Math.max(0, dano - getDefensa());
        vidaActual -= reducido;

        if (vidaActual < 0) {
            vidaActual = 0;
        }

        System.out.println("Gerolando recibió " + reducido + " de daño. Vida actual: " + vidaActual);
    }

    public void imprimirEstado() {
        System.out.println("Vida: " + vidaActual + "/" + vidaMax);
        System.out.println("Nivel: " + getNivel());
        System.out.println("Fuerza: " + fuerza);
        System.out.println("Velocidad: " + getVelocidad());
        System.out.println("Mana: " + manaActual + "/" + manaMax);
        System.out.println("XP: " + getXP());
        System.out.println("Ataque: " + getAtaque());
        System.out.println("Defensa: " + getDefensa());

        if (getArmaEquipada() != null) {
            System.out.println("Arma equipada: " + getArmaEquipada().getNombre());
        } else {
            System.out.println("No hay arma equipada.");
        }

        if (getArmaduraEquipada() != null) {
            System.out.println("Armadura equipada: " + getArmaduraEquipada().getNombre());
        } else {
            System.out.println("No hay armadura equipada.");
        }
    }
}