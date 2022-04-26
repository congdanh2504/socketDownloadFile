import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;import javax.swing.plaf.FontUIResource;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;

public class Client  {

	public static void main(String[] args) {
		final File[] fileToSend = new File[1];
		JFrame frame = new JFrame("Client");
		frame.setSize(450, 450);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel title = new JLabel("File sender");
		title.setBorder(new EmptyBorder(20, 0, 10, 0));
		title.setFont(new FontUIResource("Arial", Font.BOLD, 30));
	
		JLabel fileNameLabel = new JLabel("Choose a file to send");
		fileNameLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
		fileNameLabel.setFont(new FontUIResource("Arial", Font.BOLD, 20));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(75, 0, 10, 0));
		
		JButton sendFileButton = new JButton("Send file");
		sendFileButton.setPreferredSize(new DimensionUIResource(150, 75));
		
		JButton chooseFileButton = new JButton("Choose file");
		chooseFileButton.setPreferredSize(new DimensionUIResource(150, 75));
		
		buttonPanel.add(sendFileButton);
		buttonPanel.add(chooseFileButton);
		
		chooseFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle("Choose a file to send");
				
				if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend[0] = jFileChooser.getSelectedFile();
					fileNameLabel.setText(fileToSend[0].getName());
				}
			}
		});
		
		sendFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToSend[0] == null) {
					fileNameLabel.setText("Please choose a file");
				} else {
					try {
						FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
						Socket socket = new Socket("localhost", 1234);
						
						DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
						
						byte[] fileNameBytes = fileToSend[0].getName().getBytes();
						byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
						fileInputStream.read(fileContentBytes);
						
						dataOutputStream.writeInt(fileNameBytes.length);
						dataOutputStream.write(fileNameBytes);
						dataOutputStream.writeInt(fileContentBytes.length);
						dataOutputStream.write(fileContentBytes);
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		frame.add(title);
		frame.add(fileNameLabel);
		frame.add(buttonPanel);
		frame.setVisible(true);
	}

}
