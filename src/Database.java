import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingUtilities;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;

public class Database extends JFrame implements ActionListener{
	String db_name = "test";
	String tb_name = "test_table";
	String url = String.format("jdbc:mysql://localhost/%s",db_name);
	String user= "root";
	String pass= "root";
	Connection con = null;
	String insertSQL = "insert into %s (Time,Prot2,Prot3,Prot4,sPort,dPort,Src,Dist,sMac,dMac,Length,Info,Ascii) values(%f,'%d','%d','%d','%d','%d','%s','%s','%s','%s','%d','%s','%s')";
	String openSQL = String.format("select * from %s",tb_name);

	JButton open_button;
	JTextField text_database;
	JTextField text_table;
	JLabel label_database;
	JLabel label_table;
	JFrame openDB_window;
		
	String No,Time,sPort,dPort,Src,Dist,Length,Info;		
		
	public void insertDB(float time,int[] i_packet,String[] s_packet){
		try{	
			con = DriverManager.getConnection(url,user,pass);
			Statement stm = con.createStatement();
			String sSQL = String.format(insertSQL,tb_name,time,i_packet[0],i_packet[1],i_packet[2],i_packet[3],i_packet[4],
                                      s_packet[0],s_packet[1],s_packet[2],s_packet[3],i_packet[5],s_packet[4],s_packet[5]);
			stm.executeUpdate(sSQL);
			System.out.println("成功");
			
        		stm.close();
		
		}catch(SQLException error){
			System.out.println("失敗");
		}
	}//insertDB


	/*
	 * データベースに格納されている情報を取り出して一覧に表示するメソッド
	 * 情報の絞り込みは表示フィルタでするから ここではすべての情報を取り出してしまう
	　* ほんとはここでも取り出す情報を少なくしたい
	 */
	public void openDB(){

		try{
			/*JTableのすべての行を削除*/
			Window.tablemodel.setRowCount(0);

			/*
			 * ユーザがデータベース名とかテーブル名を指定できたほうが
			 * いいかもしれないから そのときは下のように書けば対応できる
			 * String url = String.format("jdbc:mysql://localhost/'%s'",name_database);
			 * String sql = String.format("select * from '%s'",name_table);
			 */

			con = DriverManager.getConnection(url,user,pass);
			Statement stm = con.createStatement(); 
            		ResultSet result= stm.executeQuery(openSQL);
			
			while(result.next()){
				No = result.getString("No");            
				Time = result.getString("Time");        
				sPort = result.getString("sPort");	
				dPort = result.getString("dPort");	
				Src = result.getString("Src");          
				Dist = result.getString("Dist");        
				Length = result.getString("Length");	
				Info = result.getString("Info");
				/*一覧に表示*/
				Window.openPacket(No,Time,sPort,dPort,Src,Dist,Length,Info);
	    		}//while

			result.close();
        		stm.close();

		}catch(SQLException error){
			System.out.println("失敗");
		}
	}//openDB

	
	public void actionPerformed(ActionEvent e){

	}//actionPerformed
}//Database
