package panaderias;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataManager {
	
	public static ArrayList<Empleado> getEmpleadosFromDB(DBConnection conn, boolean sync) {
		ArrayList<Empleado> emp = new ArrayList<Empleado>();
		String sql = "SELECT * FROM empleado;";	// haremos una consulta SELECT * sobre la tabla empleado para obtener una lista de todos los empleados de la base de datos

		if(conn.connect()) {
			ResultSet rst = conn.query(sql);
			int id_empleado;
			String n_ss;
			String nombre;
			String apellido1;
			String apellido2; // var locales para usar el constructor de empleado
			try {
				while(rst.next() && rst != null) { // recorremos el ResultSet del select
					id_empleado = rst.getInt(1);	// obtenemos los datos del SELECT y los asignamos a las var locales				
					n_ss = rst.getString(2);
					nombre = rst.getString(3);
					apellido1 = rst.getString(4);
					apellido2 = rst.getString(5);
					Empleado e = new Empleado(id_empleado, n_ss, nombre, apellido1, apellido2, conn, false);	// lo creamos con sync desactivada
					e.setSync(sync);	// despues activamos la sync
					emp.add(e);	// añadimos el empleado a la array que hay que devolver con la lista de empleados
					
				}
			} catch (SQLException e) { 
				emp = null;	// si hay alguna excepcion devolvemos null
			} finally {
				try {
					if(rst != null && !rst.isClosed()) rst.close();	//nos aseguramos de cerrar el resultSet
				}catch(SQLException e) {
					emp = null;
				}
			}
		}

		return emp;	// devolvemos el array de empleados que tenemos en la base de datos
	}
	
	public static ArrayList<Empleado> getEmpleadosFromCSV(String filename, DBConnection conn, boolean sync) {
		ArrayList<Empleado> emp = new ArrayList<Empleado>();
		FileReader fr = null;
		Scanner sc = null;

		if(conn.connect()) {

			try {
				fr = new FileReader(filename); 	// abrimos el fichero
				sc = new Scanner(fr);	//lectura del fichero

				if(sc.hasNext()) sc.nextLine(); // nos saltamos la primera pq son los cabeceras de los datos
				
				String data[];	// array donde vamos a guardar cada linea
				int id_empleado;
				String n_ss;
				String nombre;
				String apellido1;
				String apellido2;	// var locales para usar el constructor de empleados

				while(sc.hasNext()) {
					data = sc.nextLine().split(";"); // dividimos los valores separados por ; y se guardan en el array data
					if(data.length == 5) { // tiene que haber 5 datos (id, nss, nombre, ap1 y ap2)
						id_empleado = Integer.parseInt(data[0]); // pasamos el string a int (el id es un nº)
						n_ss = data[1];
						nombre = data[2];
						apellido1 = data[3];
						apellido2 = data[4];	// obtenemos los datos de la linea y los guardamos en las var locales
						Empleado e = new Empleado(id_empleado, n_ss, nombre, apellido1, apellido2, conn, sync);
						emp.add(e); // creamos empleados y lo añadimos al array
					}
				}

			}  catch (FileNotFoundException e1) {
				emp = null;	// si hay excepcion return null
				System.out.println("FICHERO NO ENCONTRADO");
				
			} finally { // nos aseguramos de cerrar los recursos
				if(sc != null) sc.close();	// cerramos el scanner

				try {
					if(fr != null)	fr.close();	//cerramos el FileReader
				} catch (IOException e) {
					emp = null;
				}
			}
		}

		return emp;
	}
	
	
	public static ArrayList<Local> getLocalesFromDB(DBConnection conn, boolean sync) {	//igual q el de empleados pero con Local
		ArrayList<Local> loc = new ArrayList<Local>();
		String sql = "SELECT * FROM local;";

		if(conn.connect()) {
			ResultSet rs = conn.query(sql);
			int id;
			boolean cafeteria = false;
			String direccion;
			String descripcion;
			try {

				while(rs.next()) {
					id = rs.getInt(1);		
					if(rs.getInt(2) == 1)cafeteria = true;
					direccion = rs.getString(3);
					descripcion = rs.getString(4);
					Local l = new Local(id, cafeteria, direccion, descripcion, conn, false);
					l.setSync(sync);
					loc.add(l);
				}

			} catch (SQLException e) { 
				loc = null;
			} finally {
				try {
					if(rs != null && !rs.isClosed()) rs.close();
				}catch(SQLException e) {
					loc = null;
				}
			}
		}
		return loc;
	}
	
	public static ArrayList<Local> getLocalesFromCSV(String filename, DBConnection conn, boolean sync) {	// igual que el de empleados
		ArrayList<Local> loc = new ArrayList<Local>();
		FileReader fr = null;
		Scanner sc = null;

		if(conn.connect()) {

			try {
				fr = new FileReader(filename); 	// abrimos el fichero
				sc = new Scanner(fr);	// lectura de datos

				if(sc.hasNext()) sc.nextLine();

				String data[];
				int id;
				boolean cafeteria;			
				String direccion;
				String descripcion;

				while(sc.hasNext()) {
					data = sc.nextLine().split(";");
					if(data.length == 4) {
						id = Integer.parseInt(data[0]);
						cafeteria = Integer.parseInt(data[1]) == 1;
						direccion = data[2];
						descripcion = data[3];
						Local l = new Local(id, cafeteria, direccion, descripcion, conn, sync);
						loc.add(l);
					}
				}

			}  catch (FileNotFoundException e1) {
				loc = null;

			} finally {
				if(sc != null) sc.close();
				try {
					if(fr != null)	fr.close();
				} catch (IOException e) {
					loc = null;
				}
			}
		}

		return loc;
	}

}
