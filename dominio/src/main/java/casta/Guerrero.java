package casta;
import java.util.HashMap;

import habilidad.Habilidad;
import habilidad.HabilidadFisica;
import personaje.Personaje;
public class Guerrero extends Casta{
		
		public Guerrero(){
			habilidades = new HashMap<String, Habilidad>();
			habilidades.put("atacar", new HabilidadFisica("atacar", "fisica", "te pego", 0, 0, 30, 5));
		}
		
		public int getEstado(Personaje pj){
			int fuerza = pj.getFuerza(); 
			return fuerza;
		}
		@Override
		public String toString() {
		return "Guerrero";
		}
}
