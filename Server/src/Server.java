import java.awt.*;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;

import org.w3c.dom.events.MouseEvent;

public class Server {
	static ArrayList<MyFile> myFiles = new ArrayList<>();
	
	public static void main(String[] args) {
		
		
		int fileId = 0;
		
		JFrame frame = new JFrame("Server");
		frame.setSize(450, 450);
		frame.setLocation(500, 200);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		
		JScrollPane jScrollPane = new JScrollPane(jPanel);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel titleJLabel = new JLabel("Files receiver");
		titleJLabel.setFont(new FontUIResource("Arial", Font.BOLD, 30));
		titleJLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
		
		frame.add(titleJLabel);
		frame.add(jScrollPane);
		frame.setVisible(true);
		
		try {
			
		
			ServerSocket serverSocket = new ServerSocket(1234);
			
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					int fileNameLength = dataInputStream.readInt();
					if (fileNameLength > 0) {
						byte[] fileNameBytes = new byte[fileNameLength];
						dataInputStream.readFully(fileNameBytes, 0, fileNameLength);
						String fileName = new String(fileNameBytes);
						
						int fileContentLength = dataInputStream.readInt();
						
						if (fileContentLength > 0) {
							byte[] fileContentBytes = new byte[fileContentLength];
							dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
							String fileContent = new String(fileContentBytes);
							
							JPanel fileRowJPanel = new JPanel();
							fileRowJPanel.setLayout(new BoxLayout(fileRowJPanel, BoxLayout.Y_AXIS));
							
							JLabel filenameLabel = new JLabel(fileName);
							filenameLabel.setFont(new FontUIResource("Arial", Font.BOLD, 20));
							
							
							fileRowJPanel.setName(String.valueOf(fileId));
							fileRowJPanel.addMouseListener(getMouseListener());
							fileRowJPanel.add(filenameLabel);
							jPanel.add(fileRowJPanel);
							frame.validate();

							
							myFiles.add(new MyFile(fileId++, fileName, fileContentBytes, getFileExtension(fileName)));
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
	static MouseListener getMouseListener() {
		
		return new MouseListener() {
			
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				JPanel jPanel = (JPanel) e.getSource();
				
				int fileId = Integer.parseInt(jPanel.getName());
				
				for (MyFile file: myFiles) {
					if (file.getId() == fileId) {
						JFrame previewFrame = createFrame(file);
						previewFrame.setVisible(true);
					}
 				}
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				
			}
		};
	}
	
	public static JFrame createFrame(MyFile myFile) {
		JFrame frame = new JFrame("File downloader");
		frame.setSize(400, 400);
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		
		JLabel titleJLabel = new JLabel("File downloader");
		
		titleJLabel.setFont(new FontUIResource("Arial", Font.BOLD, 30));
		titleJLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
		
		JLabel promLabel = new JLabel("Are you sure want to download " + myFile.getName());
		
		JButton yesButton = new JButton("Yes");
		yesButton.setPreferredSize(new DimensionUIResource(150, 75));
		
		JButton noButton = new JButton("No");
		noButton.setPreferredSize(new DimensionUIResource(150, 75));
		
		JLabel fileContentLabel = new JLabel();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		
		if (myFile.getFileExtension().equals("txt")) {
			fileContentLabel.setText("<html> Content: " + new String(myFile.getData()) + "</html>");
		} else if (myFile.getFileExtension().equals("png") || myFile.getFileExtension().equals("jpg")) {
			ImageIcon icon = new ImageIcon(myFile.getData());
			Image image = icon.getImage();
			Image newImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			fileContentLabel.setIcon(new ImageIcon(newImage));
		}
		
		yesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File fileToDownload = new File(myFile.getName());
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
					fileOutputStream.write(myFile.getData());
					fileOutputStream.close();
					frame.dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		noButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		
		jPanel.add(titleJLabel);
		jPanel.add(promLabel);
		jPanel.add(fileContentLabel);
		jPanel.add(buttonPanel);
		
		frame.add(jPanel);
		
		return frame;
	}
}
