package servidor;

public class ThreadEnviarPosicionesIniciales extends Thread{
	
	private Canal clientes;
	SocketCliente client;

	
	public ThreadEnviarPosicionesIniciales(Canal clientes, SocketCliente client){
		this.clientes = clientes;		
		this.client = client;
	}
	
	@Override
	public void run(){
			clientes.enviarPosicionesACliente(client);
	}

}
