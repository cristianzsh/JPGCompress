import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
* A simple program to compress JPG files
* @author Cristian Henrique (cristianmsbr@gmail.com)
*/

public class JPGCompress extends JFrame implements ActionListener {
	private JLabel source, result;
	private ImageIcon s, r;
	private JButton load, compress, save, exit;
	private JFileChooser jfc;
	private File img = null;

	public JPGCompress() {
		buildGUI();
	}

	private void buildGUI() {
		this.setTitle("JPG-Compress");

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {  }

		addImagePanels();
		addOptions();

		this.setSize(900, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void addImagePanels() {
		s = new ImageIcon(getClass().getResource("imgIcon.png"));
		r = new ImageIcon(getClass().getResource("imgIcon.png"));
		source = new JLabel(s);
		result = new JLabel(r);

		JScrollPane panelSource = new JScrollPane(source);
		JScrollPane panelResult = new JScrollPane(result);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelSource, panelResult);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(375);

		this.getContentPane().add(BorderLayout.CENTER, splitPane);
	}

	private void addOptions() {
		load = new JButton("Load image");
		compress = new JButton("Compress");
		save = new JButton("Save");
		exit = new JButton("Exit");

		load.addActionListener(this);
		compress.addActionListener(this);
		exit.addActionListener(this);

		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(5));
		box.add(load);
		box.add(Box.createVerticalStrut(10));
		box.add(compress);
		box.add(Box.createVerticalStrut(10));
		box.add(save);
		box.add(Box.createVerticalStrut(10));
		box.add(exit);
		box.setBorder(BorderFactory.createTitledBorder("Options"));

		this.getContentPane().add(BorderLayout.EAST, box);
	}

	private void loadImage() {
		jfc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG files", "jpg", "jpg");
		jfc.setFileFilter(filter);
		if (jfc.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		img = jfc.getSelectedFile();
		try {
			source.setIcon(new ImageIcon(ImageIO.read(img)));
			result.setIcon(new ImageIcon(ImageIO.read(img)));
			this.repaint();
		} catch (Exception ex) {  }
	}

	private void compressImage() {
		if (img == null) {
			return;
		}

		String quality = JOptionPane.showInputDialog(this, "Quality (ex: 0.5)");

		try {
			BufferedImage bi = ImageIO.read(img);

			File compressedImage = new File(new Date().toString().replaceAll(":", "") + ".jpg");
			OutputStream os = new FileOutputStream(compressedImage);

			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			ImageWriter writer = (ImageWriter) writers.next();

			ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			writer.setOutput(ios);

			ImageWriteParam par = writer.getDefaultWriteParam();

			par.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			par.setCompressionQuality(Float.parseFloat(quality));
			writer.write(null, new IIOImage(bi, null, null), par);

			os.close();
			ios.close();
			writer.dispose();

			result.setIcon(new ImageIcon(ImageIO.read(compressedImage)));
			this.repaint();
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == load) {
			loadImage();
		} else if (ev.getSource() == compress) {
			compressImage();
		} else if (ev.getSource() == exit) {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new JPGCompress();
	}
}