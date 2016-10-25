package alianza;

import java.util.LinkedList;

import interfaces.Atacable;
import interfaces.Grupo;
import personaje.Personaje;
import raza.Orco;

public class BatallonOrco extends Batallon {

	public BatallonOrco(int cant) {
		super(cant);
		batallon =  new LinkedList<Personaje>();

		for (int i = 0; i < cant; i++) {
			batallon.add( new Orco("generic "+i) );
		}	

	}

	public void atacar(Grupo victimas){
		int I = 0; //  "macro", esto luego podria llegar de parametro si quiero selecionar a alguno en especial.

		for (Personaje Orco: batallon) {
			if( !victimas.isEmpty() ){
				Personaje victima = victimas.get(I); //Seleciono el primero
				Orco.atacar( victima ); //Ataco a la victima
				victimas.serAtacado( victima );		//Al atacar al batallon le envio la victima, para ver si lo descuento.
			}
		}
	}
	@Override
	public String toString() {
		return "orcos";
	}

	@Override
	public Atacable obtenerProximaVictima() {
		return null;
	}
}