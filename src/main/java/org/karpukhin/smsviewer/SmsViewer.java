package org.karpukhin.smsviewer;

import org.karpukhin.smsviewer.model.Message;
import org.karpukhin.smsviewer.utils.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pavel Karpukhin
 */
public class SmsViewer {

    private static Logger logger = Logger.getLogger(SmsViewer.class.getName());

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private MessageSource messageSource;

    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JSplitPane splitPane;
    private JTree tree;
    private JPanel panel;
    private JFileChooser fileChooser;

    private DefaultTreeModel treeModel;
    private List<Message> messages;

    public SmsViewer(MessageSource messageSource) {
        this.messageSource = messageSource;
        mainFrame = new JFrame("SMS Viewer");
        menuBar = new JMenuBar();
        splitPane = new JSplitPane();
        tree = new JTree();
        panel = new JPanel();
        fileChooser = new JFileChooser(/*System.getProperty("user.home")*/);
    }

    /**
     * Initializes view
     */
    public void init() {
        initMainFrame();
        initMenu();
        initTree();

        JScrollPane treeScrollPane = new JScrollPane(tree);
        JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        splitPane.setLeftComponent(treeScrollPane);
        splitPane.setRightComponent(scrollPane);

        //fileChooser.setCurrentDirectory(fileChooser.getFileSystemView().getDefaultDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        createLayout();
    }

    /**
     * Initializes main frame
     */
    public void initMainFrame() {
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WIDTH, HEIGHT);
        ViewUtils.setWindowPositionInCenterOfScreen(mainFrame);
    }

    /**
     * Initialize menu
     */
    public void initMenu() {
        JMenu menu = new JMenu(messageSource.getMessage("label.file"));
        JMenuItem menuItem = new JMenuItem(messageSource.getMessage("label.open"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFolderChooserDialog();
            }
        });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem(messageSource.getMessage("label.exit"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
    }

    /**
     * Initializes tree with phone numbers
     */
    public void initTree() {
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(messageSource.getMessage("label.phones")));
        tree.setModel(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                if (node != null && !node.isRoot() && node.isLeaf() && node.getUserObject() instanceof String) {
                    showMessages(MessageUtils.getMessagesForNumber(messages, (String)node.getUserObject()));
                }
            }
        });
    }

    /**
     * Fills the tree with phone numbers
     */
    public void fillTree() {
        List<String> phones = MessageUtils.extractNumbers(messages);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(messageSource.getMessage("label.phones"));
        for (String phone : phones) {
            root.add(new DefaultMutableTreeNode(phone));
        }
        treeModel.setRoot(root);
    }

    public List<String> listFiles(String path) throws ApplicationException {
        File file = new File(path);
        if (!file.exists()) {
            throw new ApplicationException(String.format(messageSource.getMessage("error.path.not.exist"), path));
        }
        if (!file.isDirectory()) {
            throw new ApplicationException(String.format(messageSource.getMessage("error.path.not.directory"), path));
        }
        return listFiles(file);
    }

    public List<String> listFiles(File file) {
        File[] files = file.listFiles();
        List<String> result = new ArrayList<String>();
        for (File f : files) {
            //logger.debug(f.getName());
            if (f.isDirectory()) {
                result.addAll(listFiles(f));
            } else if (f.getName().endsWith(".vmg")) {
                result.add(f.getAbsolutePath());
            }
        }
        return result;
    }

    /**
     * Creates layout
     */
    public void createLayout() {
        GroupLayout layout = new GroupLayout(mainFrame.getContentPane());

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane)
                .addContainerGap()
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane)
                .addContainerGap()
        );

        mainFrame.getContentPane().setLayout(layout);
    }

    /**
     * Shows view
     */
    public void show() {
        mainFrame.setVisible(true);
    }

    /**
     * Shows messages
     * @param messages list of message
     */
    public void showMessages(List<Message> messages) {
        panel.removeAll();
        GroupLayout layout = new GroupLayout(panel);

        GroupLayout.ParallelGroup horGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verGroup = layout.createSequentialGroup();

        for (Message message : messages) {
            JTextArea label = new JTextArea(DateUtils.formatDate(message.getDate()) + ": " + message.getText());
            label.setLineWrap(true);
            label.setWrapStyleWord(true);
            if (message.getInbox()) {
                label.setBackground(Color.green);
                horGroup.addGroup(layout.createSequentialGroup()
                        .addComponent(label, 20, 20, Integer.MAX_VALUE)
                        .addGap(0, 0, 50)
                );
            } else {
                horGroup.addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, 50)
                        .addComponent(label, 20, 20, Integer.MAX_VALUE)
                );
            }
            verGroup.addComponent(label);
            verGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        }
        verGroup.addGap(0, 0, Integer.MAX_VALUE);
        layout.setHorizontalGroup(horGroup);
        layout.setVerticalGroup(verGroup);
        panel.setLayout(layout);
    }

    public void showFolderChooserDialog() {
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            List<String> files = new ArrayList<String>();
            try {
                files = listFiles(fileChooser.getSelectedFile().getAbsolutePath());
            } catch (ApplicationException e) {
                JOptionPane.showMessageDialog(mainFrame, e.getMessage(), messageSource.getMessage("label.error"), JOptionPane.ERROR_MESSAGE);
            }
            messages = MessageUtils.parseMessages(files);
            fillTree();
        }
    }

    public static void main(String[] args) {
        final MessageSource messageSource = new ResourceBundleMessageSource("messages");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SmsViewer viewer = new SmsViewer(messageSource);
                viewer.init();
                viewer.show();
            }
        });
    }

    public static class MessageUtils {

        private static Logger logger = Logger.getLogger(MessageUtils.class.getName());

        public static List<Message> parseMessages(List<String> files) {
            List<Message> messages = new ArrayList<Message>();
            for (String file : files) {
                logger.log(Level.FINE, file);
                Message message = VmessageParser.parse(file);
                if (message != null) {
                    messages.add(message);
                }
            }
            return messages;
        }

        public static List<String> extractNumbers(List<Message> messages) {
            Set<String> phones = new TreeSet<String>();
            for (Message message : messages) {
                phones.add(message.getNumber());
            }
            return new LinkedList<String>(phones);
        }

        public static List<Message> getMessagesForNumber(List<Message> messages, String number) {
            List<Message> result = new LinkedList<Message>();
            for (Message m : messages) {
                if (number.equals(m.getNumber())) {
                    result.add(m);
                }
            }
            Collections.sort(result, new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    return m1.getDate().compareTo(m2.getDate());
                }
            });
            return result;
        }
    }
}
