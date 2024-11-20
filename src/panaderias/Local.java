package panaderias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Local extends DBTable {
	
	private int id_local;
	private boolean tiene_cafeteria;
	private String direccion;
	private String descripcion;
	
	public Local(int id_local, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_local = id_local;
		tiene_cafeteria = false;  	
		direccion = DBConnection.NULL_SENTINEL_VARCHAR;
		descripcion = DBConnection.NULL_SENTINEL_VARCHAR;

		if(DBSync) {
			createTable();
			if(!insertEntry()) {
				this.id_local = DBConnection.NULL_SENTINEL_INT;
				DBSync = false;
			}
		}
	}
	
	public Local(int id_local, boolean tiene_cafeteria, String direccion, String descripcion, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_local = id_local;
		this.tiene_cafeteria = tiene_cafeteria;  	
		this.direccion = direccion;	
		this.descripcion = descripcion;	

		if(DBSync) {
			createTable();
			if(!insertEntry()) {
				this.id_local = DBConnection.NULL_SENTINEL_INT;
				this.tiene_cafeteria = false;  	
				this.direccion = DBConnection.NULL_SENTINEL_VARCHAR;
				this.descripcion = DBConnection.NULL_SENTINEL_VARCHAR;
				DBSync = false;
			}
		}
	}
	
	public int getId_local() {
		if(DBSync) getEntryChanges();
		return id_local;
	}
	
	public boolean getTiene_cafeteria() {
		if(DBSync) getEntryChanges();
		return tiene_cafeteria;
	}

	public void setTiene_cafeteria(boolean tiene_cafeteria) {
		this.tiene_cafeteria = tiene_cafeteria;
		if(DBSync) updateEntry();
	}

	public String getDireccion() {
		if(DBSync) getEntryChanges();
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
		if(DBSync) updateEntry();
	}

	public String getDescripcion() {
		if(DBSync) getEntryChanges();
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
		if(DBSync) updateEntry();
	}
	
	public void destroy() {
		if(DBSync && deleteEntry()) {
			this.id_local = DBConnection.NULL_SENTINEL_INT;
			this.tiene_cafeteria = false;  	
			this.direccion = DBConnection.NULL_SENTINEL_VARCHAR;
			this.descripcion = DBConnection.NULL_SENTINEL_VARCHAR;
			DBSync = false;
		}
	}
	
	
	
	boolean createTable() {
		boolean res = false;
		if(conn.connect() && !conn.tableExists("local")) {
			String sql = "create table local("
							+ "id_local int not null, "
							+ "tiene_cafeteria int, "
							+ "direccion VARCHAR(100), "
							+ "descripcion VARCHAR(100), "
							+ "primary key(id_local));";
			res = conn.update(sql) == 0;
		}
		return res;
	}
	
	boolean insertEntry() {
		boolean res = false;
		int cafe=1;	//en sql no hay boolean, es un 1 o 0 por lo que usamos estas variables locales
		int notcafe=0;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		if(tiene_cafeteria)a.add(cafe); 
		else a.add(notcafe);
		a.add(direccion);
		a.add(descripcion);
		String sql = "insert into local values (?,?,?,?);";
		
		res = conn.update(sql, a) == 1;
		
		return res;
	}
	
	boolean updateEntry() {
		boolean res = false;
		int cafe=1;
		int notcafe=0;
		ArrayList<Object> a = new ArrayList<Object>();
		if(tiene_cafeteria)a.add(cafe);
		else a.add(notcafe);
		a.add(direccion);
		a.add(descripcion);
		a.add(id_local);
		String sql = "update local set tiene_cafeteria = ?, direccion = ?, descripcion = ? where id_local = ?;";
		
		res = conn.update(sql, a) == 1;
		
		return res;
	}
	
	boolean deleteEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		String sql = "delete from local where id_local = ?;";
		
		res = conn.update(sql, a) == 0;
		return res;
	}
	
	void getEntryChanges() {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		String sql = "select tiene_cafeteria, direccion, descripcion from local where id_local = ?;";
		ResultSet rst = conn.query(sql,a);
		
		try {
			if(rst!=null && rst.next()) {
				setTiene_cafeteria((rst.getInt(1)==1)?true:false);
				setDireccion(rst.getString(2));
				setDescripcion(rst.getString(3));
			}
		} catch (SQLException e) {	
		} finally {
			try {
				if(rst != null) rst.close();
			} catch (SQLException e) {
			}
		}
		
	}
	public String toString() {
		return "Local=" + id_local + ", tiene cafeteria: " + (tiene_cafeteria) + ", dirección: " + direccion + ", descripción: " + descripcion + " --- ";
	}

}
