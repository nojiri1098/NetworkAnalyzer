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

//mainメソッド 全体を指揮するクラス
public class NetworkAnalyzer{
	
	/*
	 * main関数 すべてを統括する？
	 */
	public static void main(String[] args){
		Window frame = new Window();			
	}
}

class Window extends JFrame implements ActionListener,WindowListener{
	/*フィールドではメソッド間で共用するものを宣言する*/
	static String device;
	int c=1;

	/*デバイスの選択画面で使うコンポーネント*/
	static JRadioButton[] radio;
	static JButton start_button;
	JFrame SelectDevice;
	/*メインウィンドウで使うコンポーネント*/
	JFrame MainWindow;
	static JMenuBar JBar = new JMenuBar();			//メニューバー
	static JMenu[] cJMenu = new JMenu[3];			//メニューバーに並べる項目
	static JMenuItem[] menuitem = new JMenuItem[11];	//各項目の中身
	static JButton[] btn = new JButton[5];			//アイコン
	static JTextField filter = new JTextField("");		//表示フィルタ
	static JFileChooser filechooser = new JFileChooser();	//ファイルを開くとき
	static JTextArea Packet = new JTextArea();		//パケット一覧
	static JTextArea Packet_details = new JTextArea();	//パケット詳細
	static JTextArea Packet_bytes = new JTextArea();	//パケットデータ
	static JLabel[] footer=new JLabel[5];          		//フッタ
	static String[][] packet ={{"","","","","","",""}};		//表の中身を格納
	static String[] column = {"No","Time","Source","Distination","Protocol","Length","Information"};//カラム名
	static DefaultTableModel tablemodel = new DefaultTableModel(column, 0 ) ;//テーブルモデルを作成
	static JTable Packet_list = new JTable(tablemodel);	//テーブルを作成
	static int disp =0;					//内表示数
	static double percent=0.0;				//表示割合
	static String profile ="Default";			//プロファイル

	/*インスタンス生成*/	
	Function fc = new Function();
	Database db = new Database();
	

	/*
	　* コンストラクタ
	　* デバイス一覧の取得→ウィンドウの生成まで
	 */
	Window(){
		/*デバイスリスト格納(static)*/
		JNI.GetDeviceList();

		SelectDevice = new JFrame("インターフェースの選択");
		SelectDevice.setSize(500,500);
		SelectDevice.setLocationRelativeTo(null);
		SelectDevice.setVisible(true);
		SelectDevice.addWindowListener(this);		//WindowListenerに登録
		
		/*ラジオボタンにデバイス名をセット*/
		/*nullじゃないデバイス名の個数だけラジオボタンを作成*/
		int count=0;
		while(JNI.device_list[count] != null){
			count++;
		}
		radio = new JRadioButton[count];
		for(int i=0;i<radio.length;i++){
			radio[i] = new JRadioButton(JNI.device_list[i]);
		}
	
		/*初期状態ではradio[0]を選択した状態*/
		radio[0].setSelected(true);
		
		/*1つしか選択できないようにグループ化する*/
		ButtonGroup group = new ButtonGroup();
		for(int i=0;i<radio.length;i++){
			group.add(radio[i]);
		}

		/*デバイスの確定ボタン（キャプチャスタート）*/
		start_button = new JButton("キャプチャスタート");
		start_button.addActionListener(this);		//thisは自分のクラス内のactionPerfomedのみ有効
		
		/*パネルにラジオボタンとボタンをセット*/		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(radio.length+1,1));
		for(int i=0;i<radio.length;i++){
			p.add(radio[i]);
		}		
		p.add(start_button);

		/*コンテナにパネルをセット*/
		Container cPane = SelectDevice.getContentPane();
		cPane.add(p);	
	}//コンストラクタおわり


	/*
	 * メインウィンドウを表示する
	 */
	public void mainWindow(){
		/*ディスプレイのサイズを取得して、ウィンドウのサイズを決める*/
		Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w=ScreenSize.width;
		int h=ScreenSize.height;
		MainWindow = new JFrame("ネットワークアナル");
	   	MainWindow.setSize(w,h-30);				//ウィンドウサイズ(width,height)
		MainWindow.setLocationRelativeTo(null);
		MainWindow.setVisible(true);
		MainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	//✕ボタンを押されてもなにもしない（WindowListenerに移動）
		MainWindow.addWindowListener(this);		//WindowListenerに登録
		
		/*メニューバー------------------------------------------------------------------------*/
		/*ファイル*/
		cJMenu[0] = new JMenu("ログの閲覧");
		menuitem[10] = new JMenuItem("開く");			menuitem[10].addActionListener(this);
		cJMenu[0].add(menuitem[10]);
		/*編集*/
		cJMenu[1]   = new JMenu("キャプチャオプション");
		menuitem[0] = new JMenuItem("ネットワークの切り替え");	menuitem[0].addActionListener(this);
		menuitem[1] = new JMenuItem("キャプチャフィルタ");		menuitem[1].addActionListener(this);
		menuitem[2] = new JMenuItem("フィルタ構文の一覧");	menuitem[2].addActionListener(this);
		cJMenu[1].add(menuitem[0]);
		cJMenu[1].add(menuitem[1]);
		cJMenu[1].add(menuitem[2]);
		//表示
		cJMenu[2]   = new JMenu("統計");
		menuitem[3] = new JMenuItem("エンドノート");		menuitem[3].addActionListener(this);
		menuitem[4] = new JMenuItem("キャプチャプロパティ");	menuitem[4].addActionListener(this);
		menuitem[5] = new JMenuItem("対話");			menuitem[5].addActionListener(this);
		menuitem[6] = new JMenuItem("IPv4スタティクティクス");	menuitem[6].addActionListener(this);
		menuitem[7] = new JMenuItem("IPv6スタティスティクス");	menuitem[7].addActionListener(this);
		menuitem[8] = new JMenuItem("入出力グラフ");		menuitem[8].addActionListener(this);
		menuitem[9] = new JMenuItem("プロトコル階層統計");	menuitem[9].addActionListener(this);
		cJMenu[2].add(menuitem[3]);
		cJMenu[2].add(menuitem[4]);
		cJMenu[2].add(menuitem[5]);
		cJMenu[2].add(menuitem[6]);
		cJMenu[2].add(menuitem[7]);
		cJMenu[2].add(menuitem[8]);
		cJMenu[2].add(menuitem[9]);
		
		/*メニューをメニュバーに追加*/
		for(int i=0; i<cJMenu.length;i++){
			JBar.add(cJMenu[i]);
		}
		/*メニューバーを表示(パネルではないのでここで表示）*/
		MainWindow.setJMenuBar(JBar);

		/*パネル0(アイコン)---------------------------------------------------------------------*/
		/*開始*/
		ImageIcon icon0 = new ImageIcon("./image/start.png");
		btn[0]=new JButton(icon0);	btn[0].addActionListener(this);
		/*停止*/
		ImageIcon icon1 = new ImageIcon("./image/stop.png");
		btn[1]=new JButton(icon1);	btn[1].addActionListener(this);
		/*リセット*/
		ImageIcon icon2 = new ImageIcon("./image/restart.png");
		btn[2]=new JButton(icon2);	btn[2].addActionListener(this);
		/*検索*/
		ImageIcon icon3 = new ImageIcon("./image/search.png");
		btn[3]=new JButton(icon3);	btn[3].addActionListener(this);
		/*チェックボックス*/
		ImageIcon icon4 = new ImageIcon("./image/check2.png");
		btn[4]=new JButton(icon4);

		/*パネル1(表示フィルタ)------------------------------------------------------------------*/
		JTextField filter = new JTextField("表示フィルタを適用します");
		filter.setPreferredSize(new Dimension(w-25,30));	//Dimension(width,height)

		/*パネル2(パケット一覧)------------------------------------------------------------------*/
		Packet_list.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);	//自動リサイズOFF
		/*カラム幅*/
		Packet_list.getColumn("No").setPreferredWidth(w/15);
		Packet_list.getColumn("Time").setPreferredWidth(w/15);
		Packet_list.getColumn("Source").setPreferredWidth(w/9);
		Packet_list.getColumn("Distination").setPreferredWidth(w/9);
		Packet_list.getColumn("Protocol").setPreferredWidth(w/15);
		Packet_list.getColumn("Length").setPreferredWidth(w/15);
		Packet_list.getColumn("Information").setPreferredWidth(w/45*23-27);
		JScrollPane scroll_list = new JScrollPane(Packet_list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll_list.setPreferredSize(new Dimension(w-25,(h-205)*19/25));
		
		/*パネル3(パケット詳細)------------------------------------------------------------------*/
		Packet_details = new JTextArea("Packet_details"+w+"\n"+h,(h-205)/65,(w-38)/11);
		JScrollPane scroll_details = new JScrollPane(Packet_details,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        	JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//改行入ってるよ
		
		/*パネル5(フッタ)-----------------------------------------------------------------------*/
		footer[0] = new JLabel("デバイス : "+device);
		footer[1] = new JLabel(" || パケット数 ： 0");
		footer[2] = new JLabel("・表示 ： "+disp);
		footer[3] = new JLabel("("+percent+"%)");
		footer[4] = new JLabel(" || プロファイル ： "+profile+"  ");
		
		/*パネル作成*/
		JPanel cJPanel[] = new JPanel[5];
		/*パネル0-----------------------------------------------------------------------------*/
		cJPanel[0] = new JPanel();					//FlowLayout(配置の仕方,横方向すき間,縦方向すき間)
		cJPanel[0].setLayout(new FlowLayout(FlowLayout.LEFT,10,0));	//左詰め&横方向だけすき間
		for(int i=0;i<btn.length;i++){
			cJPanel[0].add(btn[i]);
		}
		/*パネル1(表示フィルタ)-----------------------------------------------------------------*/
		cJPanel[1] = new JPanel();
		cJPanel[1].setLayout(new FlowLayout(FlowLayout.LEFT,10,0));	//左詰め&横方向だけすき間
		cJPanel[1].add(filter);
		/*パネル2(パケット一覧)-----------------------------------------------------------------*/
		cJPanel[2] = new JPanel();
		cJPanel[2].setLayout(new FlowLayout(FlowLayout.LEFT,10,0));	//左詰め&横方向だけすき間
		cJPanel[2].add(scroll_list);
		/*パネル3(パケット詳細)-----------------------------------------------------------------*/
		cJPanel[3] = new JPanel();
		cJPanel[3].setLayout(new FlowLayout(FlowLayout.LEFT,10,0));	//左詰め&横方向だけすき間
		cJPanel[3].add(scroll_details);
		/*パネル5(フッタ)-----------------------------------------------------------------------*/
		cJPanel[4]=new JPanel();
		cJPanel[4].setLayout(new FlowLayout(FlowLayout.RIGHT,10,0));	//左詰め&横方向だけすき間
		for(int i=0;i<5;i++){
			cJPanel[4].add(footer[i]);
		}

		/*パネル配置（MainWindow.getContentPane()となるのに注意）*/
		Container cPane = MainWindow.getContentPane();
		cPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		for(int i=0;i<=4;i++){
			cPane.add(cJPanel[i]);
		}	
	}
		
	/*
	 * キャプチャしたパケットの情報を一覧に表示
	 */
	public static void AddPacket(float time,int i_packet[],String[] s_packet,int count){
		Database db2 = new Database();
		//System.out.println("AddPacket呼ばれた");
		String[] packet_add = {String.valueOf(count),String.valueOf(time),s_packet[0],s_packet[1],String.valueOf(i_packet[3]),String.valueOf(i_packet[4]),s_packet[4]};
		tablemodel.addRow(packet_add);
		/*データベースに格納*/
		db2.insertDB(time,i_packet,s_packet);
		/*フッタの更新*/
		footer[1].setText(" || パケット数 ： "+count);
		footer[2].setText("・表示 : "+(++disp));
		footer[3].setText(" ("+String.format("%1$.1f",((disp-1)*1.0f/count*1.0f)*100)+"%) ");
	}

	/*
	 * データベースに入っている情報を一覧に表示
	 */
	static void openPacket(String No,String Time,String sPort,String dPort,String Src,String Dist,String Length,String Info){
		String[] packet_add = {No,Time,Src,Dist,sPort,dPort,Info};
		tablemodel.addRow(packet_add);
	}		
	
	/*
	 * Windowクラスでのみ有効なイベントクラス
	 */
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();

		/*
		 * スレッドは実行したあと一度止めてまた実行みたいなことは
		 * できなくて　毎回インスタンスを作りなおす必要があるらしい
		 */
		JNI jni = new JNI();
		/*スレッド生成*/
		Thread PacketCapture = new Thread(jni);
	
		/*
		　* 最初のインターフェースの選択
		 * ラジオボタンで指定されているデバイス名を取得
		 */
		if(object == start_button){
			for(int i=0;i<radio.length;i++){
				if(radio[i].isSelected()){
					device = radio[i].getText();
				}
			}
			mainWindow();
			SelectDevice.dispose();
		}

		/*開始  アイコン*/
		else if(object==btn[0]){
			/*PacketCaptureのスレッドをたてる*/
			PacketCapture.start();
			
		}
		/*停止  アイコン*/
		else if(object==btn[1]){
			jni.stopThread();
		}
		/*リセット　アイコン*/
		else if(object==btn[2]){
			jni.stopThread();
			tablemodel.setRowCount(0);
			/* 
			 * 毎回インスタンスを作ってるから
			 * 同じ名前でも違うものとして扱われるから
　			 * 2つ目のスレッドとして動いてしまう
			　*/
			PacketCapture.start();
		}
		/*パケットの検索　アイコン*/
		else if(object==btn[3]){
			fc.search();
		}
		/*チェックボックス　アイコン*/
		else if(object==btn[4]){
			
		}
		/*デバイスの切り替え*/
		else if(object==menuitem[0]){
			 fc.NetworkSwitching();
		}
		/*キャプチャフィルタ*/
		else if(object==menuitem[1]){
			fc.filter();
		}
		/*フィルタリスト*/
		else if(object==menuitem[2]){
			fc.FilterList();
		}
		/*エンドノート*/
		else if(object==menuitem[3]){
			fc.Endnote();
		}
		
		//キャプチャプロパティ
		else if(object==menuitem[4]){
			fc.captureproperty();
		}
		//対話
		else if(object==menuitem[5]){
			fc.dialogue();
		}
		//IPv4スタティスティクス
		else if(object==menuitem[6]){
			fc.ipv4();
		}
		/*IPv6スタティスティクス*/
		else if(object==menuitem[7]){
			fc.ipv6();
		}
		/*入出力グラフ*/
		else if(object==menuitem[8]){
			fc.graph();
		}
		/*プロトコル階層ごとの統計*/
		else if(object==menuitem[9]){
			fc.Protcolhierarchy();
		}
		/*ログの閲覧　開く*/
		else if(object == menuitem[10]){
			db.openDB();
		}
		/*キャプチャスタートボタン*/
		else if(object==Window.start_button){
			while(true){
				System.out.printf("%d",c);
				if(device != "0") break;
				//c++;
			}
			mainWindow();
		}
	
		
	}//antionPerformedおわり


	/*✕ボタンが押されたとき*/
	public void windowClosing(WindowEvent e){
		Object object = e.getSource();
		
		if(object == MainWindow){
			//System.out.println("MainWindow　✕ボタン");
			int ans = JOptionPane.showConfirmDialog(MainWindow, "終了しますか？","最終確認", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(ans == JOptionPane.YES_OPTION) {
	            		System.exit(0);		//プログラムを終了
			}
			else{
				
			}
		}
		else if(object == SelectDevice){
			//System.out.println("SelectDevice　✕ボタン");
			System.exit(0);
		}
	}

	/*dispose()が使われたとき(SelectDeviceで使用）*/
	public void windowClosed(WindowEvent e){
		Object object = e.getSource();
		
		if(object == MainWindow){

		}
		else if(object == SelectDevice){
			System.out.println(device);
		}
	}

	public void windowOpened(WindowEvent e){
	
	}

	public void windowIconified(WindowEvent e){
	
	}

	public void windowDeiconified(WindowEvent e){
	
	}

	public void windowActivated(WindowEvent e){
	
	}
	public void windowDeactivated(WindowEvent e){
	
	}

}//Windowクラスおわり
