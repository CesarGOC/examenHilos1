
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Scanner;


//Hilo Directorio que recibe una lista de archivos que ya tienen un patron
class Directorio extends Thread{
	// Atributos
	File[] file;
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_RESET = "\u001B[0m";
	//Constructor
	Directorio(File [] directorios){
		file = new File[directorios.length];
		for(int i=0; i<directorios.length;i++){
			file[i]=directorios[i];
		}
	}
	//Metodo
	public void run(){
		//imprime solo la lista de directorios que cumple el patron
		for(int i=0; i<file.length; i++) {
				if(file[i].isDirectory()) {
				System.out.println(ANSI_PURPLE+getName()+" (Directorio) ----> "+file[i].getName()+" | [tama\u00f1o: "+Math.round(Math.ceil(file[i].length()/1024.0))+" KB ]"+ANSI_PURPLE);
				}
		}
		System.out.println(ANSI_PURPLE+getName()+" (Directorio) TERMINO"+ANSI_RESET);
	}
}

//Hilo Archivo que recibe una lista de archivos que ya tienen un patron
class Archivo extends Thread{
		// Atributos
	File[] file;
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_RESET = "\u001B[0m";
	//Constructor
	Archivo(File [] archivos){
		file = new File[archivos.length];
		for(int i=0; i<archivos.length;i++){
			file[i]=archivos[i];
		}
	}
	//Metodo
	public void run(){
		//imprime solo la lista de archivos que cumple el patron
		for(int i=0; i<file.length; i++) {
			if(file[i].isDirectory()) {
			}else{
			System.out.println(ANSI_BLUE+getName()+" (Archivo) ----> "+file[i].getName()+" |[tama\u00f1o: "+Math.round(Math.ceil(file[i].length()/1024.0))+" KB ]"+ANSI_RESET);
			}
		}
		System.out.println(ANSI_BLUE+getName()+" (Archivo) TERMINO"+ANSI_RESET);
	}
}
//Hilo Patron que recibe una ruta y el patron de busqueda
class Patron extends Thread{
	// Atributos
	String ruta;
	String patron;
	File carpeta;
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	//Constructor
	Patron(String ruta, String patron){
		this.ruta = ruta;
		this.patron = patron;
	}
	//Metodo
	public void run(){
		File carpeta = new File(this.ruta); //obtenemos la ruta
		// sobreescribimos el metodo Filefilter con la condicion de matches
		FileFilter filtro = new FileFilter(){ 
			public boolean accept(File file) {
			return file.getName().matches(patron);
			}
		};
		if(carpeta.isDirectory()) {
			File[] archivos;
			archivos = carpeta.listFiles(filtro); //hacemos un filtro de los archivos
			if(archivos.length == 0){
				System.out.println(ANSI_GREEN+getName()+" (Patron) ----> No se encontraron archivos o directorios con el patron: "+this.patron+ANSI_RESET);
			}else{
			for(int i=0; i<archivos.length; i++) {
			//imprime lista de archivos y directorios que cumple el patron
			System.out.println(ANSI_GREEN+getName()+" (Patron) ----> "+ archivos[i].getName()+" | [tama\u00f1o: "+Math.round(Math.ceil(archivos[i].length()/1024.0))+" KB ]"+ANSI_RESET); 
            }
			// crea dos bisnietos
			Archivo hiloBisnieto1 = new Archivo(archivos);
			hiloBisnieto1.start();
			Directorio hiloBisnieto2 = new Directorio(archivos);
			hiloBisnieto2.start();
			}
		}
		System.out.println(ANSI_GREEN+getName()+" (Patron) TERMINO"+ANSI_RESET);
	}
	
}


//Hilo Listar que recibe una ruta y el patron de busqueda
class Listar extends Thread{
	// Atributos
	String ruta;
	String patron;
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	//Constructor
	Listar(String ruta, String patron){
		this.ruta = ruta;
		this.patron = patron;
	}
	//Metodo
	
	public void run(){
		//Corrida del hilo listar
		System.out.println(ANSI_RED+"Listado"+ANSI_RESET);
		File carpeta = new File(this.ruta); // archivo donde se guarda la ruta
			if(carpeta.exists()){ // comprueba si existe
				if(carpeta.isDirectory()) {
					File[] archivos;
					archivos = carpeta.listFiles(); // guarda la lista de archivos de la ruta  
					for(int i=0; i<archivos.length; i++) {
					System.out.println(ANSI_RED+getName()+" (Listado) ----> "+carpeta.getName()+" "+archivos[i].getName()+ANSI_RESET); //imprime lista de archivos 
					}
				}else{
					System.out.println(ANSI_RED+getName()+" No se encontraron archivos"+ANSI_RESET);
				}
			}else{
			System.out.println(ANSI_RED+getName()+" No se encontro ruta"+ANSI_RESET);
			}
			Patron hiloNieto = new Patron(this.ruta, this.patron); // crea un nuevo hilo nieto que ejecuta el patron
			hiloNieto.start(); // corre el hilo
			System.out.println(ANSI_RED+getName()+" TERMINO"+ANSI_RESET);
	}
}

class ExamenHilos1{
	public static void main (String[] args){
		//intruduccion de datos
		Scanner sc = new Scanner(System.in);
		System.out.println("Escribe la ruta: ");
		String ruta = sc.nextLine();
		System.out.println("Escribe el patron: ");
		String patron = sc.nextLine();
		
		Listar hilo = new Listar(ruta,patron);
		hilo.start();
		System.out.println("Hilo principal esperando "+Thread.activeCount()+" hilos");
		System.out.println("1) "+ Thread.currentThread());
		System.out.println("2) "+ hilo.currentThread());
		try{
			hilo.join();
			}catch(InterruptedException ex){}
		
		
	}
}