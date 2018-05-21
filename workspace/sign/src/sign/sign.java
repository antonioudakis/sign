package sign;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class sign extends JFrame implements ActionListener{
	private JButton sign;
	private JButton exit;
	private JPasswordField PIN;
	//private JTextArea message;
	private static final char symbol[] = {
			'~','!','@','#','$','%','^','&','*','(',')','`','1','2','3','4','5','6','7','8','9','0'
	};
	static int key[] = {
			192,49,50,51,52,53,54,55,56,57,48,192,49,50,51,52,53,54,55,56,57,48
	}; 
	
	
	private static final long serialVersionUID = 1L;

	public sign() {
		initUI();
	}
	
	private void initUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel label = new JLabel("USB Token PIN");
		PIN = new JPasswordField(10);
		sign = new JButton();
		sign.setText("Sign");
		exit = new JButton();
		exit.setText("Exit");
		//message = new JTextArea();
		//message.setBounds(10,30, 200,200);  
		panel.add(label);
		panel.add(PIN);
		panel.add(sign);
		panel.add(exit);
		//panel.add(message);
		sign.addActionListener(this);
		exit.addActionListener(this);
		this.add(panel);
        setTitle("Digital Sign - Διεύθυνση Πληροφορικής Ε.Μ.Π.");
        setSize(440, 80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sign){
			try {
				signPDFs();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if(e.getSource() == exit){
			System.exit(0);
		} 
		
	}
	
	static int getPositionInSymbol(Character ch){
		for (int i=0;i<symbol.length;i++){
			if (ch==symbol[i]){
				return i;
			}
		}
		return -1;
	}
	
	public void parsePIN(String str) throws Exception {
    	Robot robot = new Robot();
        Character ch;
        int chCode;
        for (int i=0;i<str.length();i++) {
        	ch=str.charAt(i);
        	chCode=getPositionInSymbol(ch);
        	if (chCode!=-1) {
        		if (chCode<symbol.length/2){
        			robot.keyPress(KeyEvent.VK_SHIFT);
        			robot.keyPress(key[chCode]);
        			robot.keyRelease(KeyEvent.VK_SHIFT);
        		} else {
        			robot.keyPress(key[chCode]);
        		}
        	} else if (Character.isUpperCase(ch)) {
        		robot.keyPress(KeyEvent.VK_SHIFT);
        		robot.keyPress(ch.hashCode());
        		robot.keyRelease(KeyEvent.VK_SHIFT);
        	} else {
        		robot.keyPress(ch.hashCode()-32);
        	} 
        }
        robot.keyPress(KeyEvent.VK_ENTER);   	
    }
	
	@SuppressWarnings("deprecation")
	public int signSinglePDF(String file) throws Exception {
		String command = "java -jar \"C:\\Program Files (x86)\\JSignPdf\\JSignPdf.jar\" -kst WINDOWS-MY  c:\\DigitalSign\\unsigned\\"+file+" -out-suffix \"\" --out-directory c:\\DigitalSign\\signed  --tsa-server-url http://timestamp.ermis.gov.gr/TSS/HttpTspServer --tsa-policy-oid 1.3.6.1.4.1.601.10.3.1";
		Process proc = Runtime.getRuntime().exec(command);
		try  
		{
	        Thread.sleep(3000);
	    } 
	    catch(InterruptedException ex) 
	    {
	        Thread.currentThread().interrupt();
	    }
		parsePIN(PIN.getText());
		int exitCode = proc.waitFor();
		return exitCode;
		//System.out.println("exit code :"+exitCode);
	    //InputStream in = proc.getInputStream();

	    //byte b[]=new byte[in.available()];
	    //in.read(b,0,b.length);
	    //System.out.println(new String(b));
	}
	
	
	
	public void signPDFs() throws Exception{
		ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \"C:\\DigitalSign\\unsigned\" && dir *.pdf");
	    builder.redirectErrorStream(true);
	    Process p = builder.start();
	    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line;
	    int exitCode;
	    while (true) {    	
	    	line = r.readLine();
	    	if (line == null) { break; }
	    	if (line.lastIndexOf(' ')>=0) {
	    		line = line.substring(line.lastIndexOf(' ')+1,line.length());
	    		if (line.lastIndexOf('.')>=0) {
	    			if (line.substring(line.lastIndexOf('.'),line.length()).equals(".pdf")) {
	    				//message.setText("Signing ...");
	    				exitCode = signSinglePDF(line); 
	    				if (exitCode != 0) {
	    					System.exit(0);
	    				}
	    			} 
	    		}	            	
	    	}           
	    }
	    System.exit(0);
	}
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(() -> {
            sign ui = new sign();
            ui.setVisible(true);
        });
	}
}