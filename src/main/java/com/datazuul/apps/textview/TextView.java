package com.datazuul.apps.textview;

//---------------------------------------------------------------
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

// File: TextView.java
//
// A simple Swing application that lets the user view text files.
//---------------------------------------------------------------
/**
 * The TextView application class
 */
public class TextView extends JPanel {

    // The width and height of the main frame
    private static final int width = 600;
    private static final int height = 500;
    public static Dimension screenSize;
    private static JFrame frame;
    private JDialog aboutBox;
    private final JScrollPane textScroller;
    private final JPanel textPanel;
    private JTextArea textArea = null;
    public final static Border loweredBorder = new SoftBevelBorder(BevelBorder.LOWERED);
    private String text; // String to store file content
    // Rabio button menu items for different look-and-feel
    private JRadioButtonMenuItem metalMenuItem;
    private JRadioButtonMenuItem motifMenuItem;
    private JRadioButtonMenuItem windowsMenuItem;

    /**
     * Creates an instance of the application
     */
    public TextView() {
        super(true); // enable double buffer
        setLayout(new BorderLayout());
        // Add the menu bar
        add(createMenuBar(), BorderLayout.NORTH);
        // Add the scrolling text display area
        textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(loweredBorder);
        textArea = new JTextArea();
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);
        textScroller = new JScrollPane();
        textScroller.getViewport().add(textArea);
        textPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
        textPanel.add(textScroller, BorderLayout.CENTER);
        // Add the text panel to the main panel
        add(textPanel);
    }

    /**
     * Creates the application's menu bar
     */
    JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem mi;
        //---------------
        // File Menu
        JMenu file = (JMenu) menuBar.add(new JMenu("File"));
        file.setMnemonic('F');
        // Menu item - "About"
        mi = (JMenuItem) file.add(new JMenuItem("About"));
        mi.setMnemonic('b');
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aboutBox == null) {
                    aboutBox = new JDialog(frame,
                            "About TextView", false);
                    JPanel aboutPanel = new JPanel(
                            new BorderLayout());
                    JLabel aboutLabel = new JLabel(
                            "TextView by Naba Barkakati, January 1999");
                    aboutPanel.add(aboutLabel);
                    aboutBox.getContentPane().add(aboutPanel,
                            BorderLayout.CENTER);
                    JPanel buttonPanel = new JPanel(true);
                    aboutPanel.add(buttonPanel,
                            BorderLayout.SOUTH);
                    JButton okBtn = new JButton("OK");
                    buttonPanel.add(okBtn);
                    okBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            aboutBox.setVisible(false);
                        }
                    });  // end of okBtn.addActionListener()
                }
                Point fp = frame.getLocationOnScreen();
                Dimension fs = frame.getSize();
                aboutBox.setLocation(fp.x + fs.width / 4,
                        fp.y + fs.height / 4);
                aboutBox.pack();
                aboutBox.show();
            }
        }); // end of mi.addActionListener() 
        // Insert a separator between menu items
        file.addSeparator();
        // Menu item - "Open"
        mi = (JMenuItem) file.add(new JMenuItem("Open"));
        mi.setMnemonic('O');
        mi.setEnabled(true);
        ActionListener fileOpen = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                SimpleFileFilter filter = new SimpleFileFilter(
                        new String[]{"txt"}, "Text files");
                chooser.addChoosableFileFilter(filter);
                chooser.setFileFilter(filter);
                File currentFile = new File(".");
                chooser.setCurrentDirectory(currentFile);
                int rv = chooser.showOpenDialog(TextView.this);
                if (rv == JFileChooser.APPROVE_OPTION) {
                    File theFile = chooser.getSelectedFile();
                    if (theFile != null) {
                        text = readTextFile(
                                chooser.getSelectedFile().getAbsolutePath());
                        textArea.setText(text);
                    }
                }
            }
        };
        mi.addActionListener(fileOpen);
        // Several other sample menu items (all disabled)
        mi = (JMenuItem) file.add(new JMenuItem("Save"));
        mi.setMnemonic('S');
        mi.setEnabled(false);   // disable this item
        mi = (JMenuItem) file.add(new JMenuItem("Save As..."));
        mi.setMnemonic('A');
        mi.setEnabled(false);  // disable this item
        file.addSeparator();
        mi = (JMenuItem) file.add(new JMenuItem("Exit"));
        mi.setMnemonic('x');
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //-------------
        // Options Menu
        JMenu options = (JMenu) menuBar.add(new JMenu("Options"));
        options.setMnemonic('O');
        // Menu item - "Text Color"
        mi = (JMenuItem) options.add(new JMenuItem("Text Color"));
        mi.setMnemonic('C');
        ActionListener selectColor = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(
                        TextView.this,
                        "Select Text Color",
                        textArea.getForeground());
                textArea.setForeground(color);
                textArea.repaint();
            }
        };
        mi.addActionListener(selectColor);
        options.addSeparator();
        // Menu items that let user change the look-and-feel
        ButtonGroup uiGroup = new ButtonGroup();
        UIChangeListener uiListener = new UIChangeListener();
        // Menu item - "Java Look and Feel"
        metalMenuItem = (JRadioButtonMenuItem) options.add(
                new JRadioButtonMenuItem("Java Look and Feel"));
        metalMenuItem.setSelected(
                UIManager.getLookAndFeel().getName().equals("Metal"));
        uiGroup.add(metalMenuItem);
        metalMenuItem.addItemListener(uiListener);
        metalMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_1,
                        ActionEvent.ALT_MASK));
        // Menu item - "Motif Look and Feel"
        motifMenuItem = (JRadioButtonMenuItem) options.add(
                new JRadioButtonMenuItem("Motif Look and Feel"));
        motifMenuItem.setSelected(
                UIManager.getLookAndFeel().getName().equals("CDE/Motif"));
        uiGroup.add(motifMenuItem);
        motifMenuItem.addItemListener(uiListener);
        motifMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_2,
                        ActionEvent.ALT_MASK));
        // Menu item - "Windows Look and Feel"
        windowsMenuItem = (JRadioButtonMenuItem) options.add(
                new JRadioButtonMenuItem("Windows Look and Feel"));
        windowsMenuItem.setSelected(
                UIManager.getLookAndFeel().getName().equals("Windows"));
        uiGroup.add(windowsMenuItem);
        windowsMenuItem.addItemListener(uiListener);
        windowsMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_3,
                        ActionEvent.ALT_MASK));
        return menuBar;
    }

    /**
     * Changes the user interface look-and-feel
     */
    class UIChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
            try {
                if (rb.isSelected() && rb == windowsMenuItem) {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                } else if (rb.isSelected() && rb == motifMenuItem) {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                } else if (rb.isSelected() && rb == metalMenuItem) {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            } catch (UnsupportedLookAndFeelException ex) {// Unsupported look and feel
                rb.setEnabled(false);
                System.err.println("Unsupported LookAndFeel: " + rb.getText());
                // Reset look and feel to Java look-and-feel 
                try {
                    metalMenuItem.setSelected(true);
                    UIManager.setLookAndFeel(
                            UIManager.getCrossPlatformLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.err.println("Could not load LookAndFeel: " + e1);
                    e1.printStackTrace();
                }
            } catch (Exception e2) {
                rb.setEnabled(false);
                e2.printStackTrace();
                System.err.println("Could not load LookAndFeel: " + rb.getText());
                e2.printStackTrace();
            }
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

    }

    /**
     * Sets up the user interface and starts the application
     *
     * @param args (not used)
     */
    public static void main(String[] args) {
        String version = System.getProperty("java.version");
        if (version.compareTo("1.1.2") < 0) {
            System.out.println("WARNING: Swing components require Java version 1.1.2 or higher");
        }
        WindowListener l = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        TextView tv = new TextView();
        frame = new JFrame("TextView");
        frame.addWindowListener(l);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tv, BorderLayout.CENTER);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2);
        frame.setSize(width, height);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        frame.validate();
        frame.show();
        tv.requestDefaultFocus();
    }

    /**
     * Returns contents of a file in a String object
     *
     * @param filename file
     * @return content of file
     */
    public static String readTextFile(String filename) {
        String s = new String();
        File infile;
        char[] buffer = new char[50000];
        InputStream is;
        InputStreamReader isr;
        // Open and read the file's content
        try {
            infile = new File(filename);
            isr = new FileReader(infile);
            int n;
            while ((n = isr.read(buffer, 0, buffer.length)) != -1) { // Append buffer to String
                s = s + new String(buffer, 0, n);
            }
        } catch (java.io.IOException e) {
            s = "Error loading file: " + filename;
        }
        return s;
    }
}
//---------------------------------------------------------------
// A simple file filter used by JFileChooser to display files 
// with a specified extension (such as .txt).
//---------------------------------------------------------------

class SimpleFileFilter extends javax.swing.filechooser.FileFilter {

    private Hashtable extensions = null;
    private String description = null;
    private String fullDescription = null;

    /**
     * Creates a file filter from a string array of file extensions and a description. For example, to create a text
     * fiile filter, create an instance as follows: new SimpleFileFilter(String {"txt"}, "Text files"); Note that you
     * don't need the "." before the file extension.
     */
    public SimpleFileFilter(String[] extensions,
            String description) {
        this.extensions = new Hashtable(extensions.length);
        for (int i = 0; i < extensions.length; i++) {
            // Add the extensions one by one
            addExtension(extensions[i]);
        }
        this.description = description;
        fullDescription = null;
    }

    /**
     * Returns true is file is a directory or the file extension matches one of the allowed extensions.
     */
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && extensions.get(getExtension(f)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the extension portion of a file's name
     */
    public String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Returns a description of acceptable file extensions
     */
    @Override
    public String getDescription() {
        if (fullDescription == null) {
            if (description != null) {
                fullDescription = description;
            }
            // Add a comma-separated list fo extensions 
            // within parentheses
            fullDescription += " (";  // opening parenthesis
            Enumeration extlist = extensions.keys();
            if (extlist != null) {
                fullDescription += "." + (String) extlist.nextElement();
                while (extlist.hasMoreElements()) {
                    fullDescription += ", " + (String) extlist.nextElement();
                }
            }
            fullDescription += ")";  // closing parenthesis
        }
        return fullDescription;
    }

    /**
     * Adds a file extension to the table of extensions.
     */
    public void addExtension(String extension) {
        if (extensions == null) {
            extensions = new Hashtable(8);
        }
        extensions.put(extension.toLowerCase(), this);
        fullDescription = null;
    }
}
