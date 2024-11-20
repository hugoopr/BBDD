package panaderias;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main (String[] args) {
		// Hacer pruebas aquí para comprobar la funcionalidad
		Scanner sc = new Scanner(System.in);
		int entrada=1;
		int sel;
		DBConnection conn = new DBConnection("localhost",3306,"panaderia_user","panaderia_pass","panaderias");
		Empleado empleado = new Empleado(0,conn,false);
		Local local = new Local(0,conn,false);
		Trabaja trabaja = new Trabaja(0,0,null,conn,false);
		ArrayList<Empleado> empleados = null;
		ArrayList<Local> locales = null;
		
		
			

			do {
				System.out.println("¿QUÉ DESEA HACER?");
				System.out.println("  1) Crear las tablas");
				System.out.println("  2) Cargar datos de Empleado.csv");
				System.out.println("  3) Cargar datos de Local.csv");
				System.out.println("  4) Mostrar los empleados");
				System.out.println("  5) Mostrar los locales");
				System.out.println("  6) Modificar empleado");
				System.out.println("  7) Modificar local");
				System.out.println("  8) Eliminar empleado");
				System.out.println("  9) Eliminar local");
				System.out.println("  0) Salir de la aplicación");
				entrada = sc.nextInt();
			switch (entrada) {
			case 1:
				if(empleado.createTable())
					System.out.println("Tabla Empleado creada con éxito");
				else
					System.out.println("La tabla Empleado ya existía");
				if(local.createTable())
					System.out.println("Tabla Local creada con éxito");
				else
					System.out.println("La tabla Local ya existía");
				if(trabaja.createTable())
					System.out.println("Tabla Trabaja creada con éxito");
				else
					System.out.println("La tabla Trabaja ya existía");
				break;
			case 2:
				empleados = DataManager.getEmpleadosFromCSV("empleados.csv", conn, true);
				System.out.println("CARGADOS LOS DATOS A LA TABLA EMPLEADO");
				break;
			case 3:
				locales = DataManager.getLocalesFromCSV("locales.csv", conn, true);
				System.out.println("CARGADOS LOS DATOS A LA TABLA LOCAL");
				break;
			case 4:
				System.out.println("Empleados:");
				System.out.println((empleados = DataManager.getEmpleadosFromDB(conn,true)));
				break;
			case 5:
				System.out.println("Locales:");
				System.out.println((locales = DataManager.getLocalesFromDB(conn,true)));
				break;
			case 6:
				System.out.println("Elige el nº del empleado:");
				sel = sc.nextInt();
				empleado = empleados.get(sel);
				System.out.println("Se va a modificar el empleado " + empleado);
				empleado.setNombre("Laura");
				empleado.setApellido1("Maza");
				empleado.setN_ss("77465040");
				System.out.println("DATOS DEL EMPLEADO MODIFICADOS");
				break;
			case 7:
				System.out.println("Elige el nº del local:");
				sel = sc.nextInt();
				local = locales.get(sel);
				System.out.println("Se va a modificar el local " + local);
				local.setTiene_cafeteria(true);
				local.setDescripcion("Cafetería francesa");
				local.setDireccion("Virgen de Loreto 12");
				System.out.println("DATOS DEL LOCAL MODIFICADOS");
				break;
			case 8:
				System.out.println("Elige el nº del empleado:");
				sel = sc.nextInt();
				empleado = empleados.get(sel);
				System.out.println("Se van a eliminar los datos del empleado " + empleado);
				empleado.destroy();
				System.out.println("DATOS DEL EMPLEADO ELIMINADOS");
				break;
			case 9:
				System.out.println("Elige el nº del local:");
				sel = sc.nextInt();
				local = locales.get(sel);
				System.out.println("Se van a eliminar los datos del local " + local);
				local.destroy();
				System.out.println("DATOS DEL LOCAL ELIMINADOS");
				break;
			case 0:
				System.out.println("FIN DEL PROGRAMA DE PRUEBAS");
			}
		} while (entrada!='0');

		sc.close();
		
		if(conn.close())
			System.out.println("Conexión cerrada con éxito");
		
	}

}
