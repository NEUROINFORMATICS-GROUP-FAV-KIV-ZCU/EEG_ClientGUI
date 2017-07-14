package cz.zcu.kiv.DataUploading;

import cz.zcu.kiv.Const;
import cz.zcu.kiv.JFrameSingleton;
import cz.zcu.kiv.SettingsPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;

import static cz.zcu.kiv.Const.homeDirectory;
import static cz.zcu.kiv.Const.uriPrefix;
import static cz.zcu.kiv.DataUploading.HadoopController.deleteFile;
import static java.lang.System.out;

/***********************************************************************************************************************
 *
 * This file is part of the EEG_WorkflowGUI project

 * ==========================================
 *
 * Copyright (C) 2017 by University of West Bohemia (http://www.zcu.cz/en/)
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * ScreenAllUsers, 2017/06/28 07:15 Dorian Beganovic
 *
 **********************************************************************************************************************/
public class ScreenAllUsers extends JPanel implements ListSelectionListener,HadoopScreen {

    private static Log logger = LogFactory.getLog(ScreenAllUsers.class);

    // initialize contents of the table as empty
    private String[][] data = new String[0][5];
    // these are the predefined columns
    private final String[] columns = HadoopController.columns;
    // table model ie. the data stored in the table
    private DefaultTableModel tableModel;
    private JTable table = new JTable();
    // path which we want to analyze, in this case this is pretty fixed and mainly dependant on Constants
    private String path = "";
    private String fileTypeOption = "FOLDERS";


    public ScreenAllUsers(){
        // use the borderlayout
        super(new BorderLayout());

        // start the thread to get hadoop data
        HadoopController.getHadoopData(this,fileTypeOption);
        HadoopController.getCachedHadoopData(this,fileTypeOption);
        initializePanel();
    }


    public void initializePanel() {
        /*
        create the graphics
         */

        //Create the table and put it in a scroll pane.
        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table.setModel(tableModel);
        table.setRowHeight(30);


        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(10000, 50));

        //table.setShowHorizontalLines(true);
        //table.setShowVerticalLines(true);
        table.setGridColor(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        JScrollPane listScrollPane = new JScrollPane(table);
        // double click functionality
        listScrollPane.getViewport().getView().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    logger.info("Double clicked");
                    /*
                    JFrameSingleton.getMainScreen().setContentPane(new ScreenAllExperiments(data[table.getSelectedRow()][0]));
                    JFrameSingleton.getMainScreen().invalidate();
                    JFrameSingleton.getMainScreen().validate();
                    logger.info("Going into Hadoop folder: " +  uriPrefix+ homeDirectory +Const.hadoopSeparator +data[table.getSelectedRow()][0]);
                    */
                    /*
                    JDialog jDialog = new JDialog();
                    jDialog.add(new ScreenAllExperiments(data[table.getSelectedRow()][0]));
                    jDialog.setSize(JFrameSingleton.getMainScreen().getSize());
                    jDialog.setLocationRelativeTo(null);
                    jDialog.setVisible(true);
                    */
                    JFrame frame = new JFrame();
                    frame.add(new ScreenAllExperiments(data[table.getSelectedRow()][0]));
                    frame.setSize(800, 700);
                    frame.setResizable(true);
                    frame.setLocationByPlatform(true);
                    //frame.setLocation(JFrameSingleton.getMainScreen().getLocation().x-10,JFrameSingleton.getMainScreen().getLocation().y-10);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.setVisible(true);

                }
            }
        });


        /*
        // back button
        JButton backButton = new JButton("EXIT");
        backButton.setPreferredSize(new Dimension(120,40));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrameSingleton.getMainScreen().dispatchEvent(
                        new WindowEvent(JFrameSingleton.getMainScreen(), WindowEvent.WINDOW_CLOSING));
            }
        });
        */

        // delete button
        JButton deleteButton = new JButton("DELETE FILE");
        deleteButton.setPreferredSize(new Dimension(120,40));
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFile(
                        homeDirectory + Const.hadoopSeparator + path + Const.hadoopSeparator +  data[table.getSelectedRow()][0],
                        Const.getHadoopFileSystem() );
                HadoopController.getHadoopData(ScreenAllUsers.this,fileTypeOption);
                JFrameSingleton.getMainScreen().invalidate();
                JFrameSingleton.getMainScreen().validate();
            }
        });


        // next button
        JButton nextButton = new JButton("NEXT");
        nextButton.setPreferredSize(new Dimension(120,40));
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrameSingleton.getMainScreen().setContentPane(new ScreenAllExperiments(data[table.getSelectedRow()][0]));
                JFrameSingleton.getMainScreen().invalidate();
                JFrameSingleton.getMainScreen().validate();
                logger.info("Going into Hadoop folder: " +  uriPrefix+ homeDirectory +Const.hadoopSeparator +data[table.getSelectedRow()][0]);
            }
        });


        // ADD EXPERIMENT button
        JButton addButton = new JButton("ADD USER");
        addButton.setPreferredSize(new Dimension(120,40));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Configuration conf = new Configuration();
                FileSystem fs;
                try {
                    String filename = JOptionPane.showInputDialog(null,
                            "How should the folder be called?", "Enter the folder name",  JOptionPane.QUESTION_MESSAGE);
                    fs = FileSystem.get(URI.create(uriPrefix+ homeDirectory), conf);
                    logger.info("User wants to create a path " + uriPrefix+ homeDirectory +Const.hadoopSeparator +filename );
                    fs.mkdirs(new Path(uriPrefix+ homeDirectory +path + Const.hadoopSeparator +filename));
                    logger.info("Created a path " + uriPrefix+ homeDirectory +Const.hadoopSeparator +filename );
                    HadoopController.getHadoopData(ScreenAllUsers.this,fileTypeOption);
                }
                catch (Exception ex) {
                    out.println(ex.getMessage());
                }
            }
        });


        final JTextField jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(200,30));
        jTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        jTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                update();
            }
            public void removeUpdate(DocumentEvent e) {
                update();
            }
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                String query = jTextField.getText();

                logger.info("Set the hadoop data (String[][]) matrix onto JTable ");

                ArrayList<String[]> tempData = new ArrayList<String[]>();
                for(String[] file : data){
                    String filename = file[0];
                    if (filename.toLowerCase().contains(query.toLowerCase())) {
                        tempData.add(file);
                    }
                }
                String[][] tempArray = new String[tempData.size()][5];
                for(int i = 0; i < tempData.size(); i++){
                    tempArray[i] = tempData.get(i);
                }
                getTableModel().setDataVector(tempArray,columns);
                getTableModel().fireTableDataChanged();


            }
        });

        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new GridBagLayout());
        /*
        buttonPane.add(backButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        */
        buttonPane.add(addButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(jTextField);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(nextButton);
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(100));
        buttonPane.add(deleteButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("Settings");
        jMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {

                    JFrameSingleton.getMainScreen().setContentPane(new SettingsPanel("AllUsers",path));
                    JFrameSingleton.getMainScreen().invalidate();
                    JFrameSingleton.getMainScreen().validate();
                }
            }
        });
        JMenu jMenu2 = new JMenu("Current path: " + homeDirectory + Const.hadoopSeparator + path);
        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu2);

        add(jMenuBar,BorderLayout.PAGE_START);
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);


    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
    }

    @Override
    public String[][] getData() {
        if(data == null){
            return new String[0][5];
        }
        return data;
    }
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public JTable getJTable() {
        return this.table;
    }

    @Override
    public void setData(String[][] data) {
        this.data = data;
    }

    @Override
    public void setJTable(JTable newTable) {
        this.table = newTable;
    }

    @Override
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }
}
