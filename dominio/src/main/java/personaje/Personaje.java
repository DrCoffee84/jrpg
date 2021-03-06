package personaje;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acciones.Accion;
import acciones.FactoriaAcciones;
import batalla.EquipoJugadores;
import batalla.PersonajeSimple;
import casta.Casta;
import casta.Guerrero;
import casta.LoroMaster;
import casta.Mago;
import interfaces.Atacable;
import interfaces.Equipo;
import item.ItemEquipo;
import item.ItemLanzable;
import mapa.Punto;
import mensaje.MensajeBatalla;
import habilidad.Habilidad;

public abstract class Personaje implements Atacable {

	private static final int PUNTOS_POR_NIVEL = 2;
	//Salud y Energia: Estos dependeran de la raza, tendra su salud base diferente.
	protected String nombre;
	protected int saludBase; 
	protected int energiaBase;
	// Estos son calculado
	protected int saludActual;
	protected int energiaActual; 

	protected String tipoCasta;
	protected String tipoRaza;

	
	//Aca esta todo el manejo de habilidades, depende la casta tendra un libro distinto.
	protected Casta casta;
	//Ubicacion y sprite:
	protected Punto ubicacion;
	//protected Sprite sprite;
	protected boolean puedoMoverme;
	protected String sprite;
	//Mochila for now.
	
	protected Map<String, ItemLanzable> mochilaItemLanzable;
	protected Map<String, ItemEquipo> mochilaEquipo;
	
	protected final int ESPACIO_MOCHILA = 10;

	protected Equipo equipo;
	// Atributos: depende de items, (cada raza empieza con basicos pero a la larga se amortigua.
	protected int ataqueFisico;  
	protected int ataqueMagico;  
	protected int defensaFisica;
	protected int defensaMagica;
	protected boolean estaMUERTO;
	//Estados, inicialmente todo personaje nuevo tiene 0  y se van escalando a "gusto".
	protected int fuerza = 0;  
	protected int intelecto = 0;
	protected int destreza = 0;
	protected int vitalidad = 0;
	protected int velocidad = 0;

	boolean enDefensa = false;

	protected Map<String, ItemEquipo> itemEquipado;

	//Progreso del personaje.
	protected int nivel = 1;
	protected final int NIVELMAX = 100; // El algoritmo no tiene limite.xD.
	protected int experiencia = 0;
	protected int cantOro = 100;
	protected int puntosDeEstados = 0;
	private List<PersonajeSimple> equipoSimple;

	//Constructor:
	public Personaje(String nombre) {
		this.nombre = nombre;
		mochilaItemLanzable = new HashMap<String, ItemLanzable>();
		mochilaEquipo = new HashMap<String,ItemEquipo>();
		itemEquipado = new HashMap<String, ItemEquipo>();
		itemEquipado.put("anillo", new ItemEquipo());
		itemEquipado.put("armaDer", new ItemEquipo());
		itemEquipado.put("armaIzq", new ItemEquipo());
		itemEquipado.put("armadura", new ItemEquipo());
		itemEquipado.put("casco", new ItemEquipo());
		ubicacion = new Punto(6,6); // esto tiene que ser seteado por el servidor, posiblemente en el costructor. 
		//equipo = new EquipoJugadores();
		equipoSimple = new ArrayList<>();
	}
	
	public Personaje(Personaje pj) {
		this.nombre = pj.nombre;
		this.saludBase = pj.saludBase;
		this.energiaBase = pj.energiaBase;
		this.saludActual = pj.saludActual;
		this.energiaActual = pj.energiaActual;
		this.tipoCasta = pj.tipoCasta;
		this.tipoRaza = pj.tipoRaza;
		this.casta = pj.casta;
		this.ubicacion = pj.ubicacion;
		this.puedoMoverme = pj.puedoMoverme;
		this.sprite = pj.sprite;
		this.mochilaItemLanzable = pj.mochilaItemLanzable;
		this.mochilaEquipo = pj.mochilaEquipo;
		//this.equipo = pj.equipo;
		this.equipoSimple = pj.equipoSimple;
		this.ataqueFisico = pj.ataqueFisico;
		this.ataqueMagico = pj.ataqueMagico;
		this.defensaFisica = pj.defensaFisica;
		this.defensaMagica = pj.defensaMagica;
		this.fuerza = pj.fuerza;
		this.intelecto = pj.intelecto;
		this.destreza = pj.destreza;
		this.vitalidad = pj.vitalidad;
		this.velocidad = pj.velocidad;
		this.enDefensa = pj.enDefensa;
		this.itemEquipado = pj.itemEquipado;
		this.nivel = pj.nivel;
		this.experiencia = pj.experiencia;
		this.puntosDeEstados = pj.puntosDeEstados;
	}
	
	public Personaje() {
		mochilaItemLanzable = new HashMap<String, ItemLanzable>();
		itemEquipado = new HashMap<String, ItemEquipo>();
		mochilaEquipo = new HashMap<String, ItemEquipo>();
/*
		mochilaItemLanzable = new HashMap<String, ItemLanzable>();
		itemEquipado = new HashMap<String, ItemEquipo>();
		
		itemEquipado.put("anillo", new ItemEquipo());
		itemEquipado.put("armaDer", new ItemEquipo());
		itemEquipado.put("armaIzq", new ItemEquipo());
		itemEquipado.put("armadura", new ItemEquipo());
		itemEquipado.put("casco", new ItemEquipo());
		
		ubicacion = new Punto(0,0);
		this.equipo = new EquipoJugadores(nombre);*/
	}
	/**
	 * La salud total depende de la raza.
	 * @return
	 */
	public abstract int calcularSaludTotal();
	/**
	 * La energia total depende de la raza.
	 * @return
	 */
	public abstract int calcularEnergiaTotal();

	//Experiencia	
	/**
	 * Sube la experencia y se fija si subiste de nivel.
	 * Si subiste lo hace automaticamente, y al subir de nivel 
	 * se le da los puntos.
	 * @param exp
	 */
	public void subirExperencia(int exp) {
		this.experiencia += exp;
		caluclarNivel();
	}
	/**
	 * Devuelve el nivel con respecto a tu experiencia.
	 * y tambien aumenta los puntos si subi el nivel
	 */
	private void caluclarNivel() {
		int nivelAntiguo = this.nivel;
		int nivelNuevo = 0;
		for (int i = 1; i <= NIVELMAX; i++) {
			if( this.experiencia >= calcularExpPorNivel(i))
				nivelNuevo = i;
		}
		this.nivel = nivelNuevo;

		int nivelesQueSubi = nivelNuevo-nivelAntiguo;

		if(nivelesQueSubi>0)
			puntosDeEstados += PUNTOS_POR_NIVEL*nivelesQueSubi; // 2 puntos por nivel.
	}
	/**
	 * Calculo de la experiencia que necesito en algun nivel.
	 * @param nivel
	 * @return
	 */
	private int calcularExpPorNivel(int nivel){
		int expNecesaria=50;
		for(int i=1;i<=nivel;i++){
			expNecesaria += i*50;
		}
		return expNecesaria;
	}

	public int getExperiencia() {
		return this.experiencia;
	}

	public int getNivel() {
		return this.nivel;
	}

	public int getPuntosDeEstados(){
		return this.puntosDeEstados;
	}

	//Esto depende del equipo:
	public int obtenerPuntosDeAtaqueFisico(){
		int ataqueFisicoTotal=ataqueFisico;

		for ( ItemEquipo item : itemEquipado.values() ) 
			ataqueFisicoTotal += item.getAtaqueFisico();
		ataqueFisicoTotal += getFuerza()*2;
		return  ataqueFisicoTotal;
	}
	public int obtenerPuntosDeAtaqueMagico(){
		int ataqueMagicoTotal=ataqueMagico;

		for ( ItemEquipo item : itemEquipado.values() ) 
			ataqueMagicoTotal += item.getAtaqueMagico();
		ataqueMagicoTotal += getIntelecto()*2;
		return  ataqueMagicoTotal;
	}
	public int obtenerPuntosDeDefensaFisica(){
		int defensaFiscaTotal=defensaFisica;

		for ( ItemEquipo item : itemEquipado.values() ) 
			defensaFiscaTotal += item.getDefensaFisica();

		if(enDefensa)
			defensaFiscaTotal*= 4;

		return  defensaFiscaTotal;

	}
	public int obtenerPuntosDeDefensaMagica(){
		int defensaMagicaTotal=defensaMagica;

		for ( ItemEquipo item : itemEquipado.values() ) 
			defensaMagicaTotal += item.getDefensaMagica();

		if(enDefensa)
			defensaMagicaTotal*= 4;

		return  defensaMagicaTotal;
	}
	public int getIntelecto() {
		int intelectoTotal=intelecto;

		for ( ItemEquipo item : itemEquipado.values() ) 
			intelectoTotal += item.getIntelecto();

		return  intelectoTotal;
	}
	public int getVitalidad() {
		int vitalidadTotal=vitalidad;

		for ( ItemEquipo item : itemEquipado.values() ) 
			vitalidadTotal += item.getVitalidad();

		return  vitalidadTotal;
	}
	public int getDestreza() {
		int destrezaTotal=destreza;

		for ( ItemEquipo item : itemEquipado.values() ) 
			destrezaTotal += item.getDestreza();

		return  destrezaTotal;
	}
	public int getFuerza() {
		int fuerzaTotal=fuerza; //fuerza actual 

		for ( ItemEquipo item : itemEquipado.values() ) 
			fuerzaTotal += item.getFuerza();

		return  fuerzaTotal;
	}

	/**
	 * Velocidad actual del personaje.
	 * @return
	 */
	public int getVelocidad() {
		return this.velocidad;
	}

	/**
	 * Energia actual del personaje.
	 * @return
	 */
	public int getEnergia() {
		return this.energiaActual;
	}

	/**
	 *
	 * Devuelve un boolean si se puede consumir o no.
	 * Si se puede consumir, lo hace.
	 * Se pasa como parametro el Costo de energia y la que dispongo.
	 * @param costo
	 * @param energia 
	 */
	public boolean consumirEnergia(int costo) {
		if(costo > getEnergia())
			return false;
		this.energiaActual -= costo;
		return true;
	}

	//	#######################################################
	// Casta:
	public Casta getCasta() {
		return casta;
	}

	//Por ahora seria asi: ( y creo que va a quedar asi).
	public void setCastaMago() {
		this.tipoCasta = "mago";
		this.casta = new Mago();
	}
	public void setCastaLoroMaster() {
		this.tipoCasta = "loromaster";
		this.casta = new LoroMaster();
	}
	public void setCastaGuerrero(){
		this.tipoCasta = "guerrero";
		this.casta = new Guerrero();
	}
	
	/**
	 * Esto posiblemente vuele, que ya todos los personajes tenga la habilidad cargadas
	 * y lo unico que haga es preguntar si tengo el nivel para lanzarla.
	 * @param conjuro
	 * @param habilidad
	 */
	public void agregarHabilidad(String conjuro, Habilidad habilidad) {
		casta.agregarHabilidad(conjuro, habilidad);
	}

	public String verHabilidades() {
		String habilidades= "Habilidades:\n";
		for ( String key : casta.listaHabilidades() ) {
			habilidades += key + "\n";
		}
		return habilidades;
	}
	
	public Map<String,Habilidad> getHabilidades() {

		return casta.getHabilidades();
	}




	/**
	 * 	Lanzar habilidad[algun conjuro del libro, ya sea una "superPatada" o "piroExplosion" o "pitulin"].
	 *  Si no lo encuentra no lo lanza.
	 *  Distingue del tipo de ataque ataque normal, magico y fisico.
	 * @param conjuro
	 * @param personaje
	 * @return
	 */
	public  boolean lanzarHabilidad(String conjuro, Personaje personaje){
		Habilidad h = getCasta().getHabilidad(conjuro);
		if( h != null && consumirEnergia( h.getCosto() )  ){
			h.afectar(personaje, getCasta().getEstado(this), tipoDanio(h.getEfecto()));
			return true;
		}
		return false;
	}


	/**
	 * 	Obtiene la velocidad al lanzar una habilidad[algun conjuro del libro,
	 *  ya sea una "superPatada" o "piroExplosion" o "pitulin"].
	 *  Si no lo encuentra devuelve 0.
	 *  
	 * @param conjuro
	 * 
	 * @return velocidad
	 */
	public int getVelLanzarHabilidad(String conjuro){
		Habilidad h = getCasta().getHabilidad(conjuro);

		if( h != null )
			return h.getVelocidad() + this.getVelocidad();
	
		return 0;
	}


	/**
	 * 	Obtiene la velocidad al lanzar un objeto.
	 *  Si no lo encuentra devuelve 0.
	 *  
	 * @param item
	 * 
	 * @return velocidad
	 */
	public int getVelLanzarItem(String item){

		ItemLanzable  i = mochilaItemLanzable.get(item);

		if( i != null ){
			return i.getVelocidad() + this.getVelocidad();
		}
		return 0;
	}


	/**
	 * 	Obtiene la velocidad al defenderse.

	 * 
	 * @return velocidad
	 */
	public int getVelDefenderse(){

		return this.getVelocidad()*100;
	}

	/**
	 * 	Obtiene la velocidad al huir.
	 * 
	 * @return velocidad
	 */
	public int getVelHuir(){
		return this.getVelocidad()*100;
	}

	/**
	 * Dependiendo el tipo de habilidad, entonces le envio el ataque correspondiente.
	 * @param tipo
	 * @return
	 */
	private int tipoDanio(String tipo) {
		if(tipo.equals("magica"))
			return ataqueMagico;
		if(tipo.equals("curativa"))
			return ataqueMagico;
		if(tipo.equals("fisica"))
			return ataqueFisico;
		if(tipo.equals("truedamage"))
			return ataqueFisico+ataqueMagico;
		return 0;
	}

	public  boolean lanzarItem(String item, Personaje personaje){
		ItemLanzable  i = mochilaItemLanzable.get(item);
		if(i != null ){
			i.usar(personaje);
			if(i.getCantidad() == 0)
				this.mochilaItemLanzable.remove(item);
			return true;
		}

		return false;
	}

	/**
	 * 1% del daño es reducido por cada punto de defensa Fisica.
	 * @param danio
	 */
	public void serAtacadoFisico(int danio) {
		this.saludActual -=  (int) danio - (danio*obtenerPuntosDeDefensaFisica())/100;
		if(enDefensa){
			sacarDefenderse();
		}
		verSiSeMurio();
	}


	/**
	 *  1% del daño es reducido por cada punto de defensa Magica.
	 * @param danio
	 */
	public void serAtacadoMagico(int danio){
		this.saludActual -=  (int) danio - (danio*obtenerPuntosDeDefensaMagica())/100;
		if(enDefensa){
			sacarDefenderse();
		}
		verSiSeMurio();
	}

	/**
	 *  Todo el daño es aplicado.
	 * @param danio
	 */
	public void serAtacadoDanioPuro(int danio){
		this.saludActual -=  (int) danio;
		if(enDefensa){
			sacarDefenderse();
		}
		verSiSeMurio();
	}

	private void verSiSeMurio() {
		if ( this.saludActual <= 0 ){
			estaMUERTO = true;
			saludActual = 0;			
		}
	}
	/** 
	 * Cura toda la vida.
	 */
	public void serCurado() {
		this.saludActual = calcularSaludTotal();
		
	}

	/**
	 * Cura una cierta cantidad de vida.
	 * @param vida
	 */
	public void serCurado(int vida) {
		int aux = saludActual + vida;
		if(aux <= calcularSaludTotal() )
			this.saludActual = aux;
		else
			saludActual = calcularSaludTotal();
	}
	/**
	 * Energiza al maximo
	 */
	public void serEnergizado() {
		this.energiaActual = calcularEnergiaTotal();
	}

	/**
	 * Energiza en tantos puntos.  
	 */
	public void serEnergizado(int e) {
		int aux = this.energiaActual + e;
		if(aux <= calcularEnergiaTotal() )
			this.energiaActual = aux;
		else
			energiaActual = calcularEnergiaTotal();
	}

	// No implementado todavia, posiblemente no este mas.
	public void morir() { 
	}

	/**
	 * Dice si el personaje esta muerto.
	 */
	public boolean estaMuerto() {
		return estaMUERTO;
	}
	
	public void revivir(){
		estaMUERTO = false;
		this.saludActual = calcularSaludTotal();
		ubicacion = new Punto(6, 6);
	}
	
	public int getSaludActual(){
		return this.saludActual;
	}

	//Subir habilidad:
	//Sube 1 punto, (supongo esto va estar en un botoncito y hago click) 
	public boolean subirVitalidad() {
		if( this.puntosDeEstados==0 )
			return false;
		this.puntosDeEstados--;
		this.vitalidad ++;
		this.saludActual = calcularSaludTotal();
		return true;
	}
	public boolean subirVelocidad() {
		if( this.puntosDeEstados==0 )
			return false;
		this.puntosDeEstados--;
		this.velocidad ++;
		return true;
	}
	public boolean subirDestreza() {
		if( this.puntosDeEstados==0 )
			return false;
		this.puntosDeEstados--;
		this.destreza ++;
		this.energiaActual = calcularEnergiaTotal();
		return true;
	}
	public boolean subirIntelecto() {
		if( this.puntosDeEstados==0 )
			return false;
		this.puntosDeEstados--;
		this.intelecto++;
		return true;
	}
	public boolean subirFuerza() {
		if( this.puntosDeEstados==0 )
			return false;
		this.puntosDeEstados--;
		this.fuerza ++;
		return true;
	}

	@Override
	public String toString() {
		return nombre;
	}

	//Items:
	public boolean guardarItem(String i, ItemLanzable item){
		// Si no puedo equipar no equipo.
		ItemLanzable loTengo = mochilaItemLanzable.get(i);
		if( loTengo == null){
			if(ESPACIO_MOCHILA <= cantidadItems())
				return false;
			mochilaItemLanzable.put(i,item);
		}
		else{
				loTengo.aumentarCantidad( item.getCantidad() );
		}
		return true;
	}

	public int cantidadItems(){
		return mochilaItemLanzable.size();
	}
	public String verItemsLanzablesEnMochila(){
		String Items= "Items:\n";
		for ( String key : mochilaItemLanzable.keySet() ) {
			Items += key + "\n";
		}
		return Items;
	}

	// ↓↓↓Equipar items, puede ser que todo esto se convierta en uno
	// Tambien deberia devolver el anterior, despues pruebo  pero seria algo asi
	// public ItemEquipo setSarasa(ItemEquipo){}
	// 	↓↓			↑Este seria el viejo.
	public ItemEquipo equipar(ItemEquipo item){
		String tipo = item.toString(); // Saco el tipo de Equipo que sea (anillo, casco, lo que sea)

		ItemEquipo sacoEste = itemEquipado.get(tipo); //Retorno el item que tenia antes.

		itemEquipado.put(tipo, item); // Pongo el nuevo item. 

		return sacoEste; // lesto.
	}
	/**
	 * Me quedo sin equipo en la ranura que paso por parametro
	 * ya sea: casco, arma, anillo, etc.
	 * y retorno lo que habia ahi. 
	 * @param key
	 * @return
	 */
	public ItemEquipo desequipar(String key) {
		ItemEquipo sacoEste = itemEquipado.get(key);
		itemEquipado.put(key, new ItemEquipo());
		return sacoEste;
	}
	/**
	 * Veo que item esta en la ranura.
	 * @param key
	 * @return
	 */
	public ItemEquipo verEquipo(String key) {
		return itemEquipado.get(key);
	}

	public Accion pedirAccionTest(Equipo elemigo,MensajeBatalla mensaje) {		
		
		
		Personaje objetivo = obtenerObjetivo( elemigo, mensaje.getObjetivo() );
				
		return FactoriaAcciones.getAccion(this, objetivo,mensaje.getAccion(),mensaje.getTipo());
	}

	private Personaje obtenerObjetivo(Equipo elemigo, String objetivo) {
		Personaje pjAliado;
		Personaje pjElemigo;
		pjElemigo = elemigo.getPersonaje(objetivo);
		if( pjElemigo != null )
			return pjElemigo;
		
		pjAliado = this.equipo.getPersonaje(objetivo);
		if(pjAliado != null)
			return pjAliado;
		
		return null;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
		equipo.agregar(this);
	}
	public void salirEquipo(){
		this.equipo = new EquipoJugadores();
		autoAgregarce();
	}
	public Equipo getEquipo(){
		return this.equipo;
	}
	public Punto getUbicacion() {
		return ubicacion;
	}

	public void desplazar(String dir) {
		if(puedoMoverme)
			ubicacion.desplazar(dir);
	}
	public boolean isPuedoMoverme() {
		return puedoMoverme;
	}
	public void setPuedoMoverme(boolean puedoMoverme) {
		this.puedoMoverme = puedoMoverme;
	}

	public void setUbicacion(int x, int y) {
		ubicacion = new Punto(x,y);
	}
	
	public void setUbicacion(Punto pun) {
		ubicacion = pun;
	}
	
	public void defenderse() {
		enDefensa = true;

	}
	public void sacarDefenderse() {
		enDefensa = false;
	}
	public void meVoy(){
		equipo.quitar(this);
	}
	public String getNombre() {
		return this.nombre;
	}
	
	public Map<String, ItemLanzable> getMochilaItemLanzable() {
		return mochilaItemLanzable;
	}
	public String getSprite() {
		return this.sprite;
	}
	public Map<String,ItemEquipo> getItemEquipables() {
		return itemEquipado;
	}
	public PersonajeSimple getSimplificado() {
		return new PersonajeSimple(this.nombre, this.calcularSaludTotal(),this.calcularEnergiaTotal() , this.saludActual, this.energiaActual, this.sprite);
	}
	public void autoAgregarce() {
		equipoSimple = new ArrayList<>(); // Por cada vez que te conectas vas a estar solito en este mundo olvidado.
		equipoSimple.add(getSimplificado()); //:)
	}
	public List<PersonajeSimple> obtenerEquipoSimple() {
		return equipoSimple;
	}

	public String getTipoCasta() {
		return tipoCasta;
	}
	public String getTipoRaza() {
		return tipoRaza;
	}

	public int quitarOroPerder() {
		int cantOro = (this.cantOro * 5)/100;
		this.cantOro -= cantOro;
		return cantOro;
	}

	public void darOro(int oro) {
		this.cantOro+=oro;
		
	}

	public int getCantOro() {
		return this.cantOro;
	}

	public void matar() {
		saludActual = 0;
		estaMUERTO=true;
		
	}
	public void agregarAMochila(String key, ItemEquipo itemE) {
		mochilaEquipo.put(key, itemE);
	}

	public String verItemsEquipablesEnMochila(){
		String Items= "Items:\n";
		for ( String key : mochilaEquipo.keySet() ) {
			Items += key + "\n";
		}
		return Items;
	}

}

