package panaderias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Empleado extends DBTable {
	
	private int id_empleado;
	private String n_ss;
	private String nombre;
	private String apellido1;
	private String apellido2;
	
	public Empleado(int id_empleado, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_empleado = id_empleado;	// solo tenemos id_empleado como parametro
		n_ss = DBConnection.NULL_SENTINEL_VARCHAR;	// el resto a valor centinela
		nombre = DBConnection.NULL_SENTINEL_VARCHAR;
		apellido1 = DBConnection.NULL_SENTINEL_VARCHAR;
		apellido2 = DBConnection.NULL_SENTINEL_VARCHAR;

		if(DBSync) {
			createTable();	// creamos la tabla
			if(!insertEntry()) {	// si la insercion falla ponemos tambien el id_empleado a valor centinela y la Sync falsa
				this.id_empleado = DBConnection.NULL_SENTINEL_INT;
				DBSync = false;
			}
		}
	}
	
	public Empleado(int id_empleado, String n_ss, String nombre, String apellido1, String apellido2, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_empleado = id_empleado;
		this.n_ss = n_ss;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;	// nos dan todos los atributos como parametros

		if(DBSync) {
			createTable();	// creamos la tabla
			if(!insertEntry()) {	// igual que antes, si falla la insercion ponemos todo en valor centinela
				this.id_empleado = DBConnection.NULL_SENTINEL_INT;
				this.n_ss = DBConnection.NULL_SENTINEL_VARCHAR;
				this.nombre = DBConnection.NULL_SENTINEL_VARCHAR;
				this.apellido1 = DBConnection.NULL_SENTINEL_VARCHAR;
				this.apellido2 = DBConnection.NULL_SENTINEL_VARCHAR;
				DBSync = false;
			}
		}
	}
	
	public int getId_empleado() {
		if(DBSync) getEntryChanges();	// en los get si la sync está activada debemos actualizar los valores de los atributos primero
		return id_empleado;
	}
	
	public String getN_ss() {
		if(DBSync) getEntryChanges();
		return n_ss;
	}

	public void setN_ss(String n_ss) {	// en los set si la sync está activada debemos actualizar los valores de los atributos primero
		this.n_ss = n_ss;
		if(DBSync) updateEntry();	// ademas despues de modificar el valor debemos actualizar el obejeto de la base de datos
	}

	public String getNombre() {
		if(DBSync) getEntryChanges();
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
		if(DBSync) updateEntry();
	}

	public String getApellido1() {
		if(DBSync) getEntryChanges();
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
		if(DBSync) updateEntry();
	}

	public String getApellido2() {
		if(DBSync) getEntryChanges();
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
		if(DBSync) updateEntry();
	}
	
	public void destroy() {
		if(DBSync && deleteEntry()) {	// si la sync está activa y se borra el elemento se ponen todos los atributos a valor centinela
			this.id_empleado = DBConnection.NULL_SENTINEL_INT;
			this.n_ss = DBConnection.NULL_SENTINEL_VARCHAR;
			this.nombre = DBConnection.NULL_SENTINEL_VARCHAR;
			this.apellido1 = DBConnection.NULL_SENTINEL_VARCHAR;
			this.apellido2 = DBConnection.NULL_SENTINEL_VARCHAR;
			DBSync = false;	// desactiva la sync
		}
	}
	

	
	boolean createTable() {
		boolean res = false;
		if(conn.connect() && !conn.tableExists("empleado")) {	// si la tabla no existe y estamos conectados
			String sql = "create table empleado("
							+ "id_empleado int not null, "
							+ "n_ss VARCHAR(100), "
							+ "nombre VARCHAR(100), "
							+ "apellido1 VARCHAR(100), "
							+ "apellido2 VARCHAR(100), "
							+ "primary key(id_empleado));";	// escribimos la consulta como la hariamos en sql
			res = conn.update(sql) == 0;	// si hemos afectado 0 filas, hemos creado la tabla correctamente 
		}
		
		return res;
	}
	
	boolean insertEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();	//creamos un array a para pasarlo como parámetro
		a.add(id_empleado);	// metemos los valores en el orden
		a.add(n_ss);
		a.add(nombre);
		a.add(apellido1);
		a.add(apellido2);
		String sql = "insert into empleado values (?,?,?,?,?);"; // hacemos un INSERT con parametros
		
		res = conn.update(sql, a) == 1;	//si hemos afectado 1 fila -> insert correcto
		
		return res;
	}
	
	boolean updateEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(n_ss);
		a.add(nombre);
		a.add(apellido1);
		a.add(apellido2);
		a.add(id_empleado); // metemos id_empleado el ultimo
		// hacemos un UPDATE con parametros en el orden designado
		String sql = "update empleado set n_ss = ?, nombre = ?, apellido1 = ?, apellido2 = ? where id_empleado = ?;";
		
		res = conn.update(sql, a) == 1;
		
		return res;
	}
	
	boolean deleteEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_empleado);	// filtramos por los valores de la clave primaria
		String sql = "delete from empleado where id_empleado = ?;"; // hacemos un DELETE
		
		res = conn.update(sql, a) == 1;	//modificamos la base de datos (conn.update)
		return res;
	}
	
	void getEntryChanges() {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_empleado);
		// hacemos un SELECT sobre id_empleado
		String sql = "select n_ss, nombre, apellido1, apellido2 from empleado where id_empleado = ?;";
		ResultSet rst = conn.query(sql,a); // no modifica la base de datos
		
		try {
			if(rst!=null && rst.next()) { // actualizamos los valores de los atributos con los set
				setN_ss(rst.getString(1));
				setNombre(rst.getString(2));
				setApellido1(rst.getString(3));
				setApellido2(rst.getString(4));
			}
		} catch (SQLException e) {
			
		}finally { // nos aseguramos de cerrar el ResultSet
			try {
				if(rst != null) rst.close();
			} catch (SQLException e) {}
		}
		
	}
	
	public String toString() {
		return "Empleado=id: " + id_empleado + ", nss: " + n_ss +", nombre: " + nombre + " " + apellido1 + " " + apellido2 + " --- ";
	}

}
