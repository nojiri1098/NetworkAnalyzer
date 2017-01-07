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

public class Function extends JFrame implements ActionListener{
	JFrame ns_window;
	JRadioButton[] radio;
	JButton ns_button;

	public void search(){
		JFrame search_window = new JFrame("パケットの検索");
		search_window.setSize(500,100);
		search_window.setLocationRelativeTo(null);
		search_window.setVisible(true);
	
		JLabel label = new JLabel("検索ワード");
		label.setPreferredSize(new Dimension(100,30));
		JTextField serch_word = new JTextField();
		serch_word.setPreferredSize(new Dimension(300,30));
	
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT,10,0));		//左詰め&横方向だけすき間
		p.add(label);
		p.add(serch_word);
	
		Container cPane = search_window.getContentPane();
		cPane.add(p);
	}//searchメソッド

	public void filter(){
		JFrame filter_window = new JFrame("表示フィルタ");
		filter_window.setSize(500,500);
		filter_window.setLocationRelativeTo(null);
		filter_window.setVisible(true);
	}//filterメソッド

	public void captureproperty(){
		JFrame cp_window = new JFrame("キャプチャプロパティ");
		cp_window.setSize(750,730);
		cp_window.setLocationRelativeTo(null);
		cp_window.setVisible(true);
	}//capturepropertyメソッド

	public void termination(){
		JFrame termination_window = new JFrame("終端");
		termination_window.setSize(500,500);
		termination_window.setLocationRelativeTo(null);
		termination_window.setVisible(true);
	}//terminationメソッド

	public void dialogue(){
		JFrame dialogue_window = new JFrame("対話");
		dialogue_window.setSize(500,500);
		dialogue_window.setLocationRelativeTo(null);
		dialogue_window.setVisible(true);
	}//dialogueメソッド

	public void NetworkSwitching(){
		/*デバイスリスト格納(static)*/
		JNI.GetDeviceList();

		ns_window = new JFrame("インターフェースの選択");
		ns_window.setSize(500,500);
		ns_window.setLocationRelativeTo(null);
		ns_window.setVisible(true);
		
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
		ns_button = new JButton("キャプチャスタート");
		ns_button.addActionListener(this);		//thisは自分のクラス内のactionPerfomedのみ有効
		
		/*パネルにラジオボタンとボタンをセット*/		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(radio.length+1,1));
		for(int i=0;i<radio.length;i++){
			p.add(radio[i]);
		}		
		p.add(ns_button);

		/*コンテナにパネルをセット*/
		Container cPane = ns_window.getContentPane();
		cPane.add(p);
	}//NetworkSwitchingメソッド

	public void FilterList(){
		JFrame fl_window = new JFrame("フィルタ構文の一覧");
		fl_window.setSize(500,500);
		fl_window.setLocationRelativeTo(null);
		fl_window.setVisible(true);
	}//FilterListメソッド

	public void Protcolhierarchy(){
		JFrame ph_window = new JFrame("プロトコル階層統計");
		ph_window.setSize(500,500);
		ph_window.setLocationRelativeTo(null);
		ph_window.setVisible(true);
	}//Protcolhierarchyメソッド

	public void Endnote(){
		JFrame e_window = new JFrame("エンドノート");
		e_window.setSize(500,500);
		e_window.setLocationRelativeTo(null);
		e_window.setVisible(true);
	}//Endnoteメソッド

	public void ipv4(){
		JFrame ip4_window = new JFrame("IPv4スタティスティクス");
		ip4_window.setSize(500,500);
		ip4_window.setLocationRelativeTo(null);
		ip4_window.setVisible(true);
	}//ipv4メソッド

	public void ipv6(){
		JFrame ip6_window = new JFrame("IPv6スタティスティクス");
		ip6_window.setSize(500,500);
		ip6_window.setLocationRelativeTo(null);
		ip6_window.setVisible(true);
	}//ipv6メソッド

	public void toc(){
		JFrame toc_window = new JFrame("目次");
		toc_window.setSize(500,500);
		toc_window.setLocationRelativeTo(null);
		toc_window.setVisible(true);
	}//tocメソッド

	public void graph(){
		JFrame graph_window = new JFrame("グラフ");
		graph_window.setSize(500,500);
		graph_window.setLocationRelativeTo(null);
		graph_window.setVisible(true);
	}//graphメソッド

	public void manual(){
		JFrame m_window = new JFrame("マニュアルページ");
		m_window.setSize(500,500);
		m_window.setLocationRelativeTo(null);
		m_window.setVisible(true);
	}//Manualメソッド

	public void about(){
		JFrame about_window = new JFrame("ソフトウェアについて");
		about_window.setSize(500,500);
		about_window.setLocationRelativeTo(null);
		about_window.setVisible(true);
	}//aboutメソッド

	/*Functionクラスのイベント*/
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		JNI jni = new JNI();

		/*デバイスの切り替え*/
		if(object == ns_button){
			/*選択されているラジオボタンからデバイス名を取得*/		
			for(int i=0;i<radio.length;i++){
				if(radio[i].isSelected()){
					Window.device = radio[i].getText();	
				}
			}
			Window.footer[0].setText("デバイス : "+Window.device);	//フッタのデバイス名を更新
			ns_window.dispose();					//デバイスの選択ウィンドウ削除
			jni.stopThread();					//パケットキャプチャ停止
			JNI.count=0;						//パケットのカウントを初期化
			Window.footer[1].setText(" || パケット数 ： "+JNI.count);	//フッタのパケット数を更新
			Window.footer[2].setText(" || 表示 ： "+JNI.count);	//表示を更新
			Window.tablemodel.setRowCount(0);			//JTableの行を削除
		}
	}//actionPerformed
}//Functionクラス
