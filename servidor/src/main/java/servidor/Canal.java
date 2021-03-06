package servidor;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;

import batalla.EquipoSimple;
import batalla.PersonajeSimple;
import database.SQLiteJDBC;
import mapa.Mapa;
import mapa.Punto;
import mensaje.*;
import personaje.Personaje;


public class Canal {

	private Map<String,SocketCliente> canal;
	private String nombre;
	private Mapa map;
	private List<CanalCombate> combates;
	private JTextArea textArea;
	SQLiteJDBC sqcon;
	
	
	
	public void addCombate(CanalCombate combate) {
		this.combates.add(combate);
	}

	public Canal(String nombre, int alto, int ancho,JTextArea textArea, SQLiteJDBC sqcon) {
		this.textArea = textArea;
		this.map = new Mapa(nombre, alto, ancho);
		this.canal = new HashMap<String, SocketCliente>();
		this.nombre = nombre;
		combates = new ArrayList<CanalCombate>();
		this.sqcon = sqcon;

	}

	public List<CanalCombate> getCombates() {
		return combates;
	}

	public boolean esMiNombre(String nombre){
		return nombre.equals(this.nombre);
	}

	public void agregarCliente(SocketCliente cliente, Personaje per) throws IOException{
		canal.put(cliente.getUsuario(),cliente);
		map.agregarPersonaje(per, cliente.getUsuario());
		new ThreadEnviarInteraccion(this, new MensajeMovimiento(per.getUbicacion(), per.getNombre(), nombre, per.getSprite())).start();
		//cliente.enviarMensajePosicion(per.getUbicacion(), cliente.getUsuario(), "mapa4");

	}

	public String toString(){
		return nombre;
	}

	public void quitarCliente(SocketCliente cliente){
		
		canal.remove(cliente.getUsuario());
		map.quitarPersonaje(cliente.getUsuario());
		if(!sqcon.guardarPersonaje(cliente.getPer())){
			textArea.append("No se pudo guardar el personaje " + cliente.getUsuario());
		}
		try {
			cliente.cerrar();
		} catch (IOException e1) {
			textArea.append("No se pudo cerrar al cliente");
		}
		new ThreadEnviarInteraccion(this, new MensajeInteraccion(cliente.getUsuario(), MensajeInteraccion.DESCONECTADO)).start();
		// ACA MANDAR MENSAJE QUE ESTE CLIENTE ESTA DESCONECTADO A TODOS LOS USUARIOS CONECTADOS:

		textArea.append("Cliente Desconectado.");
	}

	public void enviarMensaje(Object men){

		for(SocketCliente cliente : canal.values())
		{

			try {
				cliente.enviarMensaje(men);
			} catch (IOException e) {
				//textArea.append("Error al enviar mensaje\nDetalle: "+e.toString()+"\n");

				this.quitarCliente(cliente);
			}

		}
	}

	public void enviarPosicionesACliente(SocketCliente cliente){
		Map<String,Personaje> personajes = map.obtenerPersonajes();
		for(String key : personajes.keySet())
		{

			try {
				cliente.enviarMensaje(new MensajeMovimiento(personajes.get(key).getUbicacion(), key, nombre, personajes.get(key).getSprite()));
			} catch (IOException e) {
				//textArea.append("Error al enviar mensaje\nDetalle: "+e.toString()+"\n");		
				this.quitarCliente(cliente);		 // Se trata el error.
				
			}



		}
	}

	public void enviarPosicion(MensajeMovimiento men){

		for(SocketCliente cliente : canal.values())
		{

			try {
				cliente.enviarMensaje(men);
			} catch (IOException e) {
				//textArea.append("Error al enviar mensaje\nDetalle: "+e.toString()+"\n");							
				this.quitarCliente(cliente);
			}	
		}
	}

	public void moverPersonaje(Personaje per, Punto point){
		map.moverPersonaje(per, point);
	}

	public void detenerPersonaje(Personaje per){
		map.detenerPersonaje(per);		
	}	
	public void subirEstado(Personaje per, MensajeInteraccion mens) {
		if(mens.getEmisor().equals("intelecto"))
			per.subirIntelecto();
		if(mens.getEmisor().equals("vitalidad"))
			per.subirVitalidad();
		if(mens.getEmisor().equals("destreza"))
			per.subirDestreza();
		if(mens.getEmisor().equals("fuerza"))
			per.subirFuerza();
		if(mens.getEmisor().equals("velocidad"))
			per.subirVelocidad();
	}

	public CanalCombate empezarCombate(String desafiador, String desafiado) {
		
		if(map.quitarPersonaje(desafiado) == null)
			return null; // roto
		if(map.quitarPersonaje(desafiador)== null){
			//la cague y lat engo que arreglar en algun momneto
		}

		EquipoSimple eq1 = new EquipoSimple(canal.get(desafiador).getPer().obtenerEquipoSimple());
		EquipoSimple eq2 = new EquipoSimple(canal.get(desafiado).getPer().obtenerEquipoSimple());
		CanalCombate can = new CanalCombate(this);
		for (PersonajeSimple pj : eq1.getPersonajes()) {
			can.agregarEquipo1(canal.get(pj.getNombre()));
		}
		for (PersonajeSimple pj : eq2.getPersonajes()) {
			can.agregarEquipo2(canal.get(pj.getNombre()));
		}

		MensajeInicioCombate men = new MensajeInicioCombate(desafiador, eq1, eq2);

		this.enviarMensajeInicioCombate(men, can);
		
		return can;
		//aca tengo que armar los equipos mandarlos a los clientes, y arrancar el combate
	}

	private void enviarMensajeInicioCombate(MensajeInicioCombate men, CanalCombate can) {
		for(SocketCliente cliente : can.getEq1())
		{
			try {
				cliente.enviarMensaje(men);
			} catch (IOException e) {
				//textArea.append("Error al enviar mensaje\nDetalle: "+e.toString()+"\n");							
				this.quitarCliente(cliente);
				
			}
		}

		for(SocketCliente cliente : can.getEq2())
		{
			try {
				cliente.enviarMensaje(men);
			} catch (IOException e) {
				//textArea.append("Error al enviar mensaje\nDetalle: "+e.toString()+"\n");							
				this.quitarCliente(cliente);
				
			}
		}
	}

	public boolean terminarCombate(String nombre2, String emisor) {
		// TODO Auto-generated method stub
		// aca tengo que volver a poner a los personajes en el mapa, y el que murio supongo que lo revivo en el spawn 
		return false;
	}

	public void reagregarCliente(SocketCliente cliente) {
		map.agregarPersonaje(cliente.getPer(), cliente.getUsuario());
		
	}

	public void removerCombate(CanalCombate canalCombate) {
		combates.remove(canalCombate);
		
	}

	public boolean estaCliente(SocketCliente cliente) {
		return canal.containsValue(cliente);
	}



}
