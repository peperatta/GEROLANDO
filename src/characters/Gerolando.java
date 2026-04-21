package characters;

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
    private Arma armaEquipada;
    private Armadura armaduraEquipada;
    public Inventario inventario;

    // Sistema de progresión
    private ProgressionSystem progressionSystem;

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
        this.armaEquipada = arma;
        System.out.println("Equipaste el arma: " + arma.getNombre());
    }

    public void equiparArmadura(Armadura armadura) {
        this.armaduraEquipada = armadura;
        System.out.println("Equipaste la armadura: " + armadura.getNombre());
    }

    public void usarItem(Item item) {
        if (item == null) return;

        if (item instanceof Arma) {
            if (armaEquipada == null || !armaEquipada.equals(item)) {
                equiparArma((Arma) item);
            } else {
                item.usar();
            }
            return;
        }

        if (item instanceof Armadura) {
            if (armaduraEquipada == null || !armaduraEquipada.equals(item)) {
                equiparArmadura((Armadura) item);
            } else {
                item.usar();
            }
        }
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public Armadura getArmaduraEquipada() {
        return armaduraEquipada;
    }

    // =========================
    // COMBATE
    // =========================
    public int getVelocidad() {
        int base = velocidad;
        int pesoArma = (armaEquipada != null) ? armaEquipada.getPeso() : 0;
        int pesoArmadura = (armaduraEquipada != null) ? armaduraEquipada.getPeso() : 0;
        return base - (pesoArma + pesoArmadura);
    }

    public void atacar(Enemigo enemigo) {
        int danoBase = this.getAtaque();

        boolean critico = Math.random() < 0.2;
        if (critico) {
            danoBase *= 2;
            System.out.println("¡Golpe crítico!");
        }

        String tipoAtaque = getTipoAtaque();
        String nombreArma = (armaEquipada != null) ? armaEquipada.getNombre() : "puños";

        System.out.println("Gerolando ataca de tipo " + tipoAtaque + " con " + nombreArma);

        enemigo.recibirAtaque(danoBase, tipoAtaque);
    }

    public int getAtaque() {
        int base = fuerza;
        int ataqueArma = (armaEquipada != null) ? armaEquipada.getAtaque() : 0;
        return base + ataqueArma;
    }

    public String getTipoAtaque() {
        if (armaEquipada != null) {
            return armaEquipada.tipo;
        }
        return "melee";
    }

    public int getDefensa() {
        return (armaduraEquipada != null) ? armaduraEquipada.getDefensa() : 0;
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