package characters;

import characters.equipment.EquipmentSystem;
import characters.stats.CombatStats;
import game.progression.ProgressionSystem;
import items.Arma;
import items.Armadura;
import items.Consumible;
import items.Item;

public class Gerolando {
    public Inventario inventario;
    private int oro;

    private ProgressionSystem progressionSystem;
    private EquipmentSystem equipmentSystem;
    private CombatStats combatStats;

    public Gerolando() {
        this.inventario = new Inventario();
        this.oro = 100;
        this.progressionSystem = new ProgressionSystem();
        this.equipmentSystem = new EquipmentSystem();
        this.combatStats = new CombatStats(100, 5, 20, 3);
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
        combatStats.aumentarVidaMax(cantidad);
    }

    public void aumentarFuerza(int cantidad) {
        combatStats.aumentarFuerza(cantidad);
    }

    public void aumentarManaMax(int cantidad) {
        combatStats.aumentarManaMax(cantidad);
    }

    public void aumentarVelocidadBase(int cantidad) {
        combatStats.aumentarVelocidadBase(cantidad);
    }

    public void restaurarVidaYMana() {
        combatStats.restaurarVidaYMana();
    }

    // =========================
    // EQUIPO / ITEMS
    // =========================
    public void equiparArma(Arma arma) {
        equipmentSystem.equiparArma(arma);
    }

    public void equiparArmadura(Armadura armadura) {
        equipmentSystem.equiparArmadura(armadura);
    }

    public void usarItem(Item item) {
        if (item == null) return;

        if (item instanceof Consumible) {
            ((Consumible) item).usar(this);
            inventario.eliminar(item);
            return;
        }

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
    // STATS BASE
    // =========================
    public int getVidaMax() {
        return combatStats.getVidaMax();
    }

    public int getVidaActual() {
        return combatStats.getVidaActual();
    }

    public int getFuerzaBase() {
        return combatStats.getFuerza();
    }

    public int getVelocidadBase() {
        return combatStats.getVelocidadBase();
    }

    public int getManaMax() {
        return combatStats.getManaMax();
    }

    public int getManaActual() {
        return combatStats.getManaActual();
    }

    public boolean estaVivo() {
        return combatStats.estaVivo();
    }

    public void setVida(int vidaNueva) {
        combatStats.setVidaActual(vidaNueva);
    }

    public void setMana(int manaNuevo) {
        combatStats.setManaActual(manaNuevo);
    }

    public void curarVida(int cantidad) {
        if (cantidad <= 0) return;

        int vidaNueva = getVidaActual() + cantidad;
        if (vidaNueva > getVidaMax()) {
            vidaNueva = getVidaMax();
        }

        setVida(vidaNueva);
    }

    public void curarMana(int cantidad) {
        if (cantidad <= 0) return;

        int manaNuevo = getManaActual() + cantidad;
        if (manaNuevo > getManaMax()) {
            manaNuevo = getManaMax();
        }

        setMana(manaNuevo);
    }

    // =========================
    // COMBATE
    // =========================
    public int getVelocidad() {
        return combatStats.getVelocidadBase() - equipmentSystem.getPenalizacionPeso();
    }

    public int getAtaque() {
        return combatStats.getFuerza() + equipmentSystem.getAtaqueBonus();
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
        combatStats.recibirDanio(reducido);

        System.out.println("Gerolando recibió " + reducido + " de daño. Vida actual: " + getVidaActual());
    }

    public void imprimirEstado() {
        System.out.println("Vida: " + getVidaActual() + "/" + getVidaMax());
        System.out.println("Nivel: " + getNivel());
        System.out.println("Fuerza: " + getFuerzaBase());
        System.out.println("Velocidad: " + getVelocidad());
        System.out.println("Mana: " + getManaActual() + "/" + getManaMax());
        System.out.println("XP: " + getXP());
        System.out.println("Ataque: " + getAtaque());
        System.out.println("Defensa: " + getDefensa());
        System.out.println("Oro: " + getOro());

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

    // =========================
    // ORO
    // =========================
    public int getOro() {
        return oro;
    }

    public void ganarOro(int cantidad) {
        if (cantidad <= 0) return;
        oro += cantidad;
        System.out.println("Ganaste " + cantidad + " de oro.");
    }

    public boolean gastarOro(int cantidad) {
        if (cantidad <= 0) return false;

        if (oro >= cantidad) {
            oro -= cantidad;
            return true;
        }

        System.out.println("No tienes suficiente oro.");
        return false;
    }
}