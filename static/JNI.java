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

/*
 * Runnableインターフェースを使用してパケットキャプチャを別スレッドとして動作させる
 */
public class JNI implements Runnable{
	/*ライブラリのロードとネイティブメソッドの宣言*/
	static {System.loadLibrary("finddevice");}
	static {System.loadLibrary("packet");}
	//static {System.loadLibrary("capture");}
	native static void finddevice(String[] device_list);
	native static float packet(int i_packet[],String[] s_packet);
	//native static float capture(String str,int i_packet[],String[] s_packet);

	static String[] device_list = new String[10];		//デバイスリスト格納用
	static int count=0;					//パケット一覧 No
	private static boolean isActive;			//スレッド停止用

	/*
	 * デバイスの一覧を取得して、配列device_listに格納
	 */	
	public static void GetDeviceList(){
		finddevice(device_list);
	}

	/*
	 * パケットをキャプチャして配列に格納
	 * ある程度たまったらデータベースに格納する予定
	 * 別スレッドとして動作させる
	 * メソッド名はインターフェースで定義されているので仕方ない
	 */	
	public void run(){
		/*パケット情報格納用*/	
		float time=0.0f;		
		int i_packet[] = new int[6];/*取得時間？*/
		String[] s_packet = new String[6];
	
		/*初期化と格納する情報の確認*/
		time=0.0f;		//取得時間
		i_packet[0] = 0;	//ネットワーク層     prot2
		i_packet[1] = 0;	//トランスポート層   prot3
		i_packet[2] = 0;	//アプリケーション層 prot4
		i_packet[3] = 0;	//送信元ポート番号 sPort
		i_packet[4] = 0;	//宛先ポート番号   dPort
		i_packet[5] = 0;	//パケット長       length
		s_packet[0]="0";	//送信元IPアドレスsrc
		s_packet[1]="0";	//宛先IPアドレスdist
		s_packet[2]="0";	//送信元MACアドレスsMac
		s_packet[3]="0";	//宛先MACアドレスdMac
		s_packet[4]="0";	//パケットの概要info	
		s_packet[5]="0";	//ペイロードのアスキー表示ascii
		
		/*isActive初期化*/
		isActive = true;
		
		/*停止ボタンが押されるまで*/
		while(isActive){
			try{
				time = packet(i_packet,s_packet);
				count++;
				Window.AddPacket(time,i_packet,s_packet,count);
				Thread.sleep(1000);	//1秒待つ(スレッドをスリープしてしまうので全体がストップしてしまう）
			}
			catch(InterruptedException e){
				System.out.println("sleepのエラー");
			}
		}
	}

	/*スレッドPacketCaptureの停止*/
	public void stopThread(){
		isActive = false;
	}

}
