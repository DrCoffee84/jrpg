package Equipo;

import casta.Casta;
import personaje.Personaje;
import personaje.PersonajeEquipado;

public class EscudoSvalinn extends PersonajeEquipado {
	
	protected int defensaFisica = 10;
	protected int defensaMagica = 5;
	public EscudoSvalinn(Personaje personaje) {
		super(personaje);
		personaje.setEquipoArmaIzq(false); //ya no puedo equipar nada mas en la mano izq.
	}

	@Override
	public int obtenerPuntosDeDefensaFisica(){
		return defensaFisica + super.obtenerPuntosDeDefensaFisica();
	}
	@Override
	public int obtenerPuntosDeDefensaMagica() {
		return defensaMagica + super.obtenerPuntosDeDefensaMagica();
	}
	// No modificados:
	@Override
	public int obtenerPuntosDeAtaque() {
		return super.obtenerPuntosDeAtaque();
	}
	
	//Gets:
	@Override
	public int getIntelecto(){
		return super.getIntelecto();
	}
	public int getEnergia() {
		return super.getEnergia();
	}
	public int getVitalidad() {
		return super.getVitalidad();
	}
	public int getDestreza() {
		return super.getDestreza();
	}
	public int getFuerza() {
		return super.getFuerza();
	}	
	@Override
	public Casta getCasta(){
		return super.getCasta();
	}
	@Override
	public String toString() {
		return "Escudo Svalinn";
	}
	public String verEquipo() {
		return super.verEquipo() + "\n" + this.toString();
	}
	@Override
	public void quitarArmIqz(Personaje na) {
		super.quitarArmIqz(this);
	}

}
