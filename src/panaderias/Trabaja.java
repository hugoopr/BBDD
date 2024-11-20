package panaderias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Trabaja extends DBTable {
	// mismos comentarios que empleado
	
	private int id_empleado;
	private int id_local;
	private java.sql.Date fecha_inicio;
	private java.sql.Date fecha_fin;

	
	public Trabaja(int id_empleado, int id_local, java.sql.Date fecha_inicio, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_local = id_local;
		this.id_empleado = id_empleado;
		this.fecha_inicio = fecha_inicio;
		fecha_fin = DBConnection.NULL_SENTINEL_DATE;

		if(DBSync) {
			createTable();
			if(!insertEntry()) {
				this.id_local = DBConnection.NULL_SENTINEL_INT;
				this.id_empleado = DBConnection.NULL_SENTINEL_INT;	
				this.fecha_inicio = DBConnection.NULL_SENTINEL_DATE;
				DBSync = false;
			}
		}
	}
	
	public Trabaja(int id_empleado, int id_local, java.sql.Date fecha_inicio, java.sql.Date fecha_fin, DBConnection conn, boolean DBSync) {
		super(conn, DBSync);
		this.id_local = id_local;
		this.id_empleado = id_empleado;
		this.fecha_inicio = fecha_inicio;
		this.fecha_fin = fecha_fin; 

		if(DBSync) {
			createTable();
			if(!insertEntry()) {
				this.id_local = DBConnection.NULL_SENTINEL_INT;
				this.id_empleado = DBConnection.NULL_SENTINEL_INT;
				this.fecha_inicio = DBConnection.NULL_SENTINEL_DATE;
				this.fecha_fin = DBConnection.NULL_SENTINEL_DATE;
				DBSync = false;
			}
		}
	}

	public int getId_empleado() {
		if(DBSync) getEntryChanges();
		return id_empleado;
	}

	public int getId_local() {
		if(DBSync) getEntryChanges();
		return id_local;
	}

	public java.sql.Date getFecha_inicio() {
		if(DBSync) getEntryChanges();
		return fecha_inicio;
	}

	public java.sql.Date getFecha_fin() {
		if(DBSync) getEntryChanges();
		return fecha_fin;
	}

	public void setFecha_fin(java.sql.Date fecha_fin) {
		this.fecha_fin = fecha_fin;
		if(DBSync) updateEntry();
	}
	
	public void destroy() {
		if(DBSync && deleteEntry()) {
			this.id_local = DBConnection.NULL_SENTINEL_INT;
			this.id_empleado = DBConnection.NULL_SENTINEL_INT;  	
			this.fecha_inicio = DBConnection.NULL_SENTINEL_DATE;
			this.fecha_fin = DBConnection.NULL_SENTINEL_DATE;
			DBSync = false;
			DBSync = false;
		}
	}
	
	
	boolean createTable() {
		boolean res = false;
		if(conn.connect() && !conn.tableExists("trabaja")) {
			String sql = "create table trabaja("
							+ "id_local int not null, "
							+ "id_empleado int not null, "
							+ "fecha_inicio date not null, "
							+ "fecha_fin date, "
							+ "primary key(id_local, id_empleado, fecha_inicio), "
							+ "foreign key(id_local) references local(id_local) on delete cascade on update cascade, " //metemos las foreign keys
							+ "foreign key(id_empleado) references empleado(id_empleado) on delete cascade on update cascade);";
			res = conn.update(sql) == 0;
		}
		return res;
	}
	
	boolean insertEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		a.add(id_empleado);
		a.add(fecha_inicio);
		a.add(fecha_fin);
		String sql = "insert into trabaja values (?,?,?,?);";
		
		res = conn.update(sql, a) == 1;
		
		return res;
	}
	
	boolean updateEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(fecha_fin);
		a.add(id_local);
		a.add(id_empleado);
		a.add(fecha_inicio);
		String sql = "update trabaja set fecha_fin = ? where id_local = ? and id_empleado=? and fecha_inicio = ?;";
		
		res = conn.update(sql, a) == 1;
		
		return res;
	}
	
	boolean deleteEntry() {
		boolean res = false;
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		a.add(id_empleado);
		a.add(fecha_inicio);
		String sql = "delete from trabaja where id_local = ? and id_empleado = ? and fecha_inicio = ?;";
		
		res = conn.update(sql, a) == 1;
		return res;
	}
	
	void getEntryChanges() {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(id_local);
		a.add(id_empleado);
		a.add(fecha_inicio);
		String sql = "select fecha_fin from trabaja where id_local = ? and id_empleado = ? and fecha_inicio = ?;";
		ResultSet rst = conn.query(sql,a);
		
		try {
			if(rst!=null && rst.next()) setFecha_fin(rst.getDate(1));
		} catch (SQLException e) {
		} finally {
		 	try {
				if(rst != null) rst.close();
			} catch (SQLException e) {
			}
		}
	}

}
