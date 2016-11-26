package juego;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cliente.Cliente;
import mensaje.MensajeInteraccion;
import personaje.Personaje;

@SuppressWarnings("serial")
public class Opciones extends JFrame {

	public Opciones(Personaje pj, Cliente cliente) {
		setVisible(false);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		setType(Type.UTILITY);
		setTitle(pj.getNombre());
		setAutoRequestFocus(false);
		setBounds(100, 100, 450, 234);
		setResizable(false);
		getContentPane().setLayout(null);

		JLabel lblIntelecto = new JLabel(""+pj.getIntelecto());
		lblIntelecto.setBounds(120, 33, 46, 14);
		getContentPane().add(lblIntelecto);

		JLabel lblVitalidad = new JLabel(""+pj.getVitalidad());
		lblVitalidad.setBounds(120, 67, 46, 14);
		getContentPane().add(lblVitalidad);

		JLabel lblDestreza = new JLabel(""+pj.getDestreza());
		lblDestreza.setBounds(120, 101, 46, 14);
		getContentPane().add(lblDestreza);

		JLabel lblFuerza = new JLabel(""+pj.getFuerza());
		lblFuerza.setBounds(120, 135, 46, 14);
		getContentPane().add(lblFuerza);

		JLabel lblVelocidad = new JLabel(""+pj.getVelocidad());
		lblVelocidad.setBounds(120, 166, 46, 14);
		getContentPane().add(lblVelocidad);

		JLabel lblPuntos = new JLabel("Puntos: "+pj.getPuntosDeEstados());
		lblPuntos.setBounds(10, 11, 150, 14);
		getContentPane().add(lblPuntos);

		JButton btnIntelecto = new JButton("Intelecto");
		btnIntelecto.setBounds(10, 29, 100, 23);	
		btnIntelecto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( pj.subirIntelecto() ){
					lblIntelecto.setText(""+pj.getIntelecto());
					lblPuntos.setText("Puntos: "+pj.getPuntosDeEstados());
					enviarMensaje(cliente,"intelecto");
				}
			}

		});
		getContentPane().add(btnIntelecto);

		JButton btnVitalidad = new JButton("Vitalidad");
		btnVitalidad.setBounds(10, 63, 100, 23);
		btnVitalidad.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent e) {
				if( pj.subirVitalidad() ){
					lblVitalidad.setText(""+pj.getVitalidad());
					lblPuntos.setText("Puntos: "+pj.getPuntosDeEstados());
					enviarMensaje(cliente,"vitalidad");
				}
			}


		});
		getContentPane().add(btnVitalidad);

		JButton btnDestreza = new JButton("Destreza");
		btnDestreza.setBounds(10, 97, 100, 23);
		btnDestreza.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent e) {
				if( pj.subirDestreza() ){
					lblDestreza.setText(""+pj.getDestreza());
					lblPuntos.setText("Puntos: "+pj.getPuntosDeEstados());
					enviarMensaje(cliente,"destreza");
				}
			}
		});
		getContentPane().add(btnDestreza);

		JButton btnFuerza = new JButton("Fuerza");
		btnFuerza.setBounds(10, 131, 100, 23);
		btnFuerza.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent e) {
				if( pj.subirFuerza() ){
					lblFuerza.setText(""+pj.getFuerza());
					lblPuntos.setText("Puntos: "+pj.getPuntosDeEstados());
					enviarMensaje(cliente,"fuerza");
				}
			}
		});
		getContentPane().add(btnFuerza);

		JButton btnVelocidad = new JButton("Velocidad");
		btnVelocidad.setBounds(10, 162, 100, 23);
		btnVelocidad.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent e) {
				if( pj.subirVelocidad() ){
					lblVelocidad.setText(""+pj.getVelocidad());
					lblPuntos.setText("Puntos: "+pj.getPuntosDeEstados());
					enviarMensaje(cliente,"velocidad");
				}
			}
		});
		getContentPane().add(btnVelocidad);


		JPanel panel = new JPanel();
		panel.setBounds(157, 29, 109, 156);
		getContentPane().add(panel);

		JLabel lblEquipo = new JLabel("Mochila");
		lblEquipo.setBounds(157, 11, 46, 14);
		getContentPane().add(lblEquipo);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(331, 29, 93, 156);
		getContentPane().add(panel_1);

		JLabel lblEquipo_1 = new JLabel("Equipo");
		lblEquipo_1.setBounds(331, 11, 46, 14);
		getContentPane().add(lblEquipo_1);

		JButton btnEquipar = new JButton("->");
		btnEquipar.setBounds(276, 63, 45, 57);
		getContentPane().add(btnEquipar);
	}
	private static void enviarMensaje(Cliente cliente,String estado) {
		try {
			cliente.enviarInteraccion(new MensajeInteraccion(estado, MensajeInteraccion.ESTADO));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error en actualizar datos.\nDetalles: "+e.toString());
		}
	}
}
