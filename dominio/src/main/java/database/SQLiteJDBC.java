package database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import casta.Casta;
import gson.CastaInstanceCreator;
import gson.EquipoInstanceCreator;
import gson.PersonajeInstanceCreator;
import habilidad.*;
import interfaces.Equipo;
import item.FactoriaItemLanzable;
import item.ItemEquipo;
import item.ItemLanzable;
import mensaje.Mensaje;
import personaje.FactoriaPersonaje;
import personaje.Personaje;
import raza.Humano;

public class SQLiteJDBC
{
	private static SQLiteJDBC instance = null;
	private static Connection c = null;
	
	protected SQLiteJDBC() throws ClassNotFoundException, SQLException {
		// Exists only to defeat instantiation.
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:juego.db");
		c.setAutoCommit(false);
	}

	public  boolean autenticarUsuario(String username, String password){
		
		Statement stmt = null;
		username = username.toLowerCase();
		boolean resultado = false;
		try {

			stmt = c.createStatement();

			String consulta = "SELECT id_player FROM jugadores WHERE username = '"+username+"' AND password = '"+password+"' LIMIT 1;";

			ResultSet rs = stmt.executeQuery(consulta);
			resultado = rs.next();
			//boolean resultado = true;
			/*while ( rs.next() ) {
	         int id = rs.getInt("id_player");
	         System.out.println(id);
	      }*/

			rs.close();
			stmt.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}

		return resultado;
	}
	

	public static Map<String,Habilidad> obtenerHabilidades(){

		Statement stmt = null;
		Map<String,Habilidad> habilidades = new HashMap<String,Habilidad>();
		try {

			stmt = c.createStatement();

			String consulta = "SELECT * FROM habilidad;";

			ResultSet rs = stmt.executeQuery(consulta);

			while ( rs.next() ) {
				Habilidad aux = FactoriaHabilidades.getHabilidad(
						rs.getString("nombre"), rs.getString("efecto"), rs.getString("descripcion"),
						rs.getInt("costo"), rs.getInt("nivel"), rs.getInt("cantEfecto"), rs.getInt("velocidad"));

				habilidades.put(rs.getString("key"), aux);
			}

			rs.close();
			stmt.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}

		return habilidades;
	}
	
	public static Map<String,ItemLanzable> obtenerLanzables(){

		Statement stmt = null;
		Map<String,ItemLanzable> lanzables = new HashMap<String,ItemLanzable>();
		try {

			stmt = c.createStatement();

			String consulta = "SELECT * FROM itemLanzable;";

			ResultSet rs = stmt.executeQuery(consulta);

			while ( rs.next() ) {
				ItemLanzable aux = FactoriaItemLanzable.getItemLanzable(
						rs.getString("key"), rs.getInt("nivel"),rs.getString("nombre"), rs.getString("descripcion"),
						rs.getInt("cantEfecto"), rs.getString("efecto"),1, rs.getInt("velocidad"));

				lanzables.put(rs.getString("key"), aux);
			}

			rs.close();
			stmt.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}

		return lanzables;
	}
	
	public static Map<String,ItemEquipo> obtenerEquipables(){

		Statement stmt = null;
		Map<String,ItemEquipo> equipables = new HashMap<String,ItemEquipo>();
		try {

			stmt = c.createStatement();

			String consulta = "SELECT * FROM itemEquipable;";

			ResultSet rs = stmt.executeQuery(consulta);

			while ( rs.next() ) {
				ItemEquipo aux = new ItemEquipo(
						rs.getString("key"), rs.getInt("nivel"), rs.getString("nombre"),
						rs.getString("descripcion"),rs.getInt("fuerza"), rs.getInt("intelecto"), 
						rs.getInt("destreza"),rs.getInt("vitalidad"), rs.getInt("ataqueFisico"), 
						rs.getInt("ataqueMagico"),rs.getInt("defensaFisica"), rs.getInt("defensaMagica"),rs.getString("tipo"));	
						
				equipables.put(rs.getString("key"), aux);
			}

			rs.close();
			stmt.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}

		return equipables;
	}

	public  boolean crearUsuario(String username, String password){

		Statement stmt = null;
		username = username.toLowerCase();
		try {			
			stmt = c.createStatement();
			String sql = "INSERT INTO jugadores (username,password) " +
					"VALUES ('"+username+"', '"+password+"');"; 
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
			return true;
	}
	
	public boolean guardarPersonaje(Personaje per){
		Statement stmt = null;

		final Gson gson = new Gson();		
		
		try {			
			stmt = c.createStatement();
			String sql = "UPDATE jugadores SET json = '" +
					gson.toJson(per) +
					"' WHERE username = '"+per.getNombre()+"';"; 
			
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
			return true;
		
	}
	
	public void cerrar(){
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static SQLiteJDBC getInstance() throws ClassNotFoundException, SQLException {
	      if(instance == null) {
	         instance = new SQLiteJDBC();
	      }
	      return instance;
	   }

	public Personaje getPersonaje(String username) {
		Statement stmt = null;
		username = username.toLowerCase();
		Personaje resultado = null;
		try {

			stmt = c.createStatement();

			String consulta = "SELECT json FROM jugadores WHERE username = '"+username+"' LIMIT 1;";

			ResultSet rs = stmt.executeQuery(consulta);
			if(rs.next()){
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Casta.class, new CastaInstanceCreator()); 
				gsonBuilder.registerTypeAdapter(Personaje.class, new PersonajeInstanceCreator()); 
				gsonBuilder.registerTypeAdapter(Equipo.class, new EquipoInstanceCreator()); 
				Gson gson = gsonBuilder.create();
				String stringRes = rs.getString("json");
				System.out.println(stringRes);
				resultado = gson.fromJson(stringRes, Personaje.class);
				resultado = FactoriaPersonaje.reconstruirPersonaje(resultado);
				
				final Gson gson2 = new Gson();
				System.out.println(gson.toJson(resultado));
			}
			rs.close();
			stmt.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}

		return resultado;
		
	}
}