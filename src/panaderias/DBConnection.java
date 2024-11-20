package panaderias;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	
	final static String NULL_SENTINEL_VARCHAR = "NULL";
	final static int NULL_SENTINEL_INT = Integer.MIN_VALUE;
	final static java.sql.Date NULL_SENTINEL_DATE = java.sql.Date.valueOf("1900-01-01");
	
	private Connection conn = null;
	private String user;
	private String pass;
	private String url;
	
	public DBConnection(String server, int port, String user, String pass, String database){ // constructor con las variables dadas
		this.user = user;
		this.pass = pass;
		url = "jdbc:mysql://" + server + ":" + port + "/" + database;
	}
	
	public boolean connect() {
		boolean res = false;
		
		try {

			if(conn == null || conn.isClosed()) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(url, user, pass);
			}
			res = true;
			
		} catch(ClassNotFoundException e) { // error con el driver
			System.out.println("NO SE PUDO CARGAR EL DRIVER");
		} catch (SQLException e) { // error con la conexion
			System.out.println("NO SE PUDO CONECTAR A LA BASE DE DATOS"); 
		}
		
		return res; // false si la conexion no esta disponible, true en caso contrario
	}
	
	
	public boolean close(){
		
		try {
			if(conn != null && !conn.isClosed()) conn.close(); // si la conexion no esta cerrada la cerramos
		} catch (SQLException e) {
			return false; // return false si ocurre una excepcion
		}

		return true; // return true si se ejecuta sin errores
	}

	public int update(String sql) {
		int res = -1;
		if(sql != null && connect()) {
			Statement stm = null;	// usamos statement pq no hay parametros libres
			try {
				stm = conn.createStatement();
				res = stm.executeUpdate(sql); // usamos update pq modifica la base de datos
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				res=-1;
			} finally { // metemos el close en un finally para asegurarnos que siempre la cierra
				try {
					if(stm!=null) stm.close();
				} catch (SQLException e1) {}
			}
		}
		return res; // devolvemos el nº de filas afectadas o -1
	}
	
	@SuppressWarnings("unused")
	public int update(String sql, ArrayList<Object> a) {
		int res = -1;
		if(sql != null && a != null && connect()) {
			PreparedStatement pstm = null; // usamos un prepared pq hay parametros libres
			try {
				pstm = conn.prepareStatement(sql);
			
				for(int i=0;i<a.size();i++) {
					Object o = a.get(i);
					if(o instanceof java.lang.String) { // si el elemento del array es String
						if(o.equals(NULL_SENTINEL_VARCHAR)) pstm.setNull(i+1, Types.VARCHAR); // usamos el setNull si el objeto es igual al valor centinela
						else pstm.setString(i+1, (String)o); // ponemos i+1 pq en sql se empieza por 1, no por 0
					}
					else if(o instanceof java.lang.Integer) { // si el elemento del array es Integer
						if((int)o == NULL_SENTINEL_INT) pstm.setNull(i+1, Types.INTEGER);
						else pstm.setInt(i+1, (int)o);
					}
					else if(o instanceof java.sql.Date) { // si el elemento del array es Date
						if(((java.sql.Date)o).equals(NULL_SENTINEL_DATE)) pstm.setNull(i+1, Types.DATE);
						else pstm.setDate(i+1, (java.sql.Date)o);
					}
				}
				
				res = pstm.executeUpdate(); //nº de filas afectadas
			
			}catch(SQLException e){
				res = -1; // res = -1 si hay excepcion
			} finally {
				try {
					if(pstm != null && !pstm.isClosed()) pstm.close(); // aseguramos el cierre
				} catch (SQLException e) {}
			}
		}
		return res; // devolvemos el nº de filas afectadas o -1
	}
	
	public ResultSet query(String sql) {
		ResultSet rst = null;
		
		if(sql != null && connect()) {
			Statement stm = null; // usamos Statement pq no hay parametros libres
		
			try {
				stm = conn.createStatement();
				rst = stm.executeQuery(sql); // usamos executeQuery pq no vamos a modificar la base de datos, devuelve un ResultSet
			} catch (SQLException e) {
				rst = null; // null si hay excepcion
			}
		}
		return rst; // al tener que devolver un ResultSet no podemos cerrar ni el Statement ni el ResultSet
	}
	
	@SuppressWarnings("unused")
	public ResultSet query(String sql, ArrayList<Object> a) {
		ResultSet rst = null;
		
		if(sql != null && a != null && connect()) {
			PreparedStatement pstm = null; // usamos un prepared pq hay parametros libres

			try {
				pstm = conn.prepareStatement(sql);

				for(int i=0; i < a.size(); i++) { // igual que el update
					Object o = a.get(i);

					if(o instanceof java.lang.String) {
						if(o.equals(NULL_SENTINEL_VARCHAR)) pstm.setNull(i+1, Types.VARCHAR); 
						else pstm.setString(i+1, (String)o);
					}

					else if(o instanceof java.lang.Integer) {
						if((int)o == (NULL_SENTINEL_INT)) pstm.setNull(i+1, Types.INTEGER );
						else pstm.setInt(i+1, (int)o);
					}

					else if(o instanceof java.sql.Date) {
						if(((java.sql.Date)o).equals(NULL_SENTINEL_DATE)) pstm.setNull(i+1, Types.DATE);
						else pstm.setDate(i+1, (java.sql.Date)o);
					}
				}
			
				rst = pstm.executeQuery();

			} catch(SQLException e) {
				rst = null;
			}
		}
		return rst;
	}
	
	public boolean tableExists(String tableName) {
		boolean res = false;
		
		if(tableName != null && connect()) {
			String sql = "SHOW TABLES;"; // consulta en sql
			ResultSet rst = query(sql); // hacemos la consulta
			try {
				while(rst.next() && !res) res = rst.getString(1).equals(tableName); // recorremos la lista de tablas comprobando los nombres a ver si ya existe
			}catch(SQLException e) {}
		}
		return res;
	}

}
