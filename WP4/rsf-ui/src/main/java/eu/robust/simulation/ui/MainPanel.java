/*
 * Copyright 2012 WeST Institute - University of Koblenz-Landau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Felix Schwagereit 
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */ 
package eu.robust.simulation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

public class MainPanel {

	private int Id;
	
	private int windowWidth = 500;

	// The tree main panels
	JPanel main = new JPanel();
	JPanel mainup = new JPanel();
	JPanel maindown = new JPanel();
	JPanel maindownleft = new JPanel();
	JPanel maindownright = new JPanel();

	// The Box-Panels (for main-Panel + subpanels)
	Box fileChooserPanel = Box.createHorizontalBox();
	Box simulationNPanel = Box.createHorizontalBox();
	Box currentTPanel = Box.createHorizontalBox();
	Box startDatePanel = Box.createHorizontalBox();
	Box endDatePanel = Box.createHorizontalBox();
	Box agentsPanel = Box.createHorizontalBox();
	Box communityModelPanel = Box.createHorizontalBox();
	Box communityNamePanel = Box.createHorizontalBox();
	Box subCommunityPanel = Box.createHorizontalBox();
	Box tableBasicPanel = Box.createHorizontalBox();
	Box tablePanel = Box.createHorizontalBox();
	Box buttonPanel = Box.createHorizontalBox();
	Box tableButtonPanel = Box.createHorizontalBox();
	Box changesButtonPanel = Box.createHorizontalBox();
	Box extraPanel = Box.createHorizontalBox();

	// Labels
	JLabel simulationNLabel = new JLabel("Simulation name:");
	JLabel currentTimeLabel = new JLabel(
			"Should the current time be used as start date?");
	JLabel startDateLabel = new JLabel("Start date and hour:");
	JLabel endDateLabel = new JLabel("End date and hour:");
	JLabel agentsLabel = new JLabel("Number of agents:");

	JLabel communityModel = new JLabel("Community Model:");
	JLabel communityName = new JLabel("Community:");
	JLabel subCommunity = new JLabel("subCommunity:");

	JLabel chooseLabel = new JLabel("Choose a file:");
	JLabel extraLabel = new JLabel("Additional properties:");

	JLabel statusLabel = new JLabel("");

	// TextFields
	JTextField filePathTextField = new JTextField(100);
	JTextField simulationNTextField = new JTextField(20);
	JTextField communityModelTextField = new JTextField(100);
	JTextField communityNameTextField = new JTextField(100);
	JTextField subCommunityTextField = new JTextField(100);
	
	JTextField jobIdTextField = new JTextField(50);

	// DateChooser
	JDateChooser startDateChooser = new JDateChooser();
	JDateChooser endDateChooser = new JDateChooser();

	// Spinner
	JSpinner agentsSpinner = new JSpinner();
	// JSpinner forumsSpinner = new JSpinner();
	JSpinner startHourSpinner = new JSpinner();
	JSpinner endHourSpinner = new JSpinner();

	// File Chooser
	JFileChooser fileChooser = new JFileChooser();
	JFileChooser saveChooser = new JFileChooser();

	// Buttons
	JButton startSimulationButton = new JButton("");

	JButton chooseFileButton = new JButton("");
	JButton loadButton = new JButton("");
	JButton add = new JButton("");
	JButton changes = new JButton("");
	JButton save = new JButton("");

	JButton csvButton = new JButton("CSV");
	JButton csvSaveButton = new JButton("Save");
	JButton setLog = new JButton("setLog");
	
	JButton showButton = new JButton("Show Parameter");
	
	JCheckBox chartVisibleCheckBox = new JCheckBox("Show chart", true);
	
	String[] choosableColors = {"Red", "Blue", "Magenta", "Dark gray", "Orange", "Green"};
	JComboBox colorList = new JComboBox(choosableColors);
	
	String[] choosableShapes = {"Diagonal cross", "Circle", "Rectangle", "Triangle (Up)", "Diamond", "Triangle (Down)", "Cross"};
	JComboBox shapeList = new JComboBox(choosableShapes);

	// RadioButtons
	String yesString = "yes";
	String noString = "no";
	JRadioButton currentDateYesRadioButton = new JRadioButton(yesString);
	JRadioButton currentDateNoRadioButton = new JRadioButton(noString);

	// Dates
	Date newStartDate = null;
	Date newEndDate = null;

	// HashMap
	//Map<String, String> inputFileDataHashMap;

	// Table
	DefaultTableModel model = new DefaultTableModel();
	JTable table = new JTable(model);

	// Variable for Database inquiry
	float summe = 0;

	Timer timer;

	public MainPanel(int Id/*, Map<String, String> inputFileDataHashMap*/) {

		// the components are added to their frames and arranged
		addComponents();

		// the components are set into order
		panelOrder();

		// timer = new Timer(500, new statusLabelActionListener());

		this.Id = Id;
		//this.inputFileDataHashMap = inputFileDataHashMap;

	}

	public void addComponents() {

		// The components of the mainup Panel

		// The arrangement of the row in which you choose the file

		fileChooserPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		// fileChooserPanel.setMaximumSize(new Dimension(800, 30));
		fileChooserPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		fileChooserPanel.add(chooseLabel);
		fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		fileChooserPanel.add(chooseFileButton);
		fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		//filePathTextField.setEditable(false);
		fileChooserPanel.add(filePathTextField);
		fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		fileChooserPanel.add(loadButton);
		fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		fileChooserPanel.add(save);
		fileChooserPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));
		// chooseFileButton.addActionListener(new ChooseButtonAction());
		// loadButton.addActionListener(new LoadButtonAction());

		// The arrangement of the row "Template name"

		simulationNPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		simulationNPanel.setMaximumSize(new Dimension(windowWidth, 30));
		simulationNPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		simulationNPanel.add(simulationNLabel);
		simulationNPanel.add(Box.createRigidArea(new Dimension(31, 0)));
		simulationNPanel.add(simulationNTextField);
		simulationNPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The components of the mainDownLeft Panel

		// The arrangement of the row "current time = start time?"

		currentTPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		currentTPanel.setMaximumSize(new Dimension(windowWidth, 30));
		currentTPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		currentTPanel.add(currentTimeLabel);
		currentTPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		currentDateYesRadioButton.setActionCommand(yesString);
		currentDateNoRadioButton.setActionCommand(noString);

		ButtonGroup group = new ButtonGroup();
		group.add(currentDateYesRadioButton);
		group.add(currentDateNoRadioButton);

		// currentDateYesRadioButton.addActionListener(new RButtonAction());
		// currentDateNoRadioButton.addActionListener(new RButtonAction());

		currentTPanel.add(currentDateYesRadioButton);
		currentTPanel.add(currentDateNoRadioButton);
		currentTPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// Arrangement of the "Start Date?"-Row

		startDateChooser.setDateFormatString("yyyy-MM-dd");

		Calendar sthour = new GregorianCalendar();
		sthour.set(Calendar.HOUR_OF_DAY, 1);
		SpinnerDateModel model = new SpinnerDateModel(sthour.getTime(), null,
				null, Calendar.HOUR_OF_DAY);
		startHourSpinner.setModel(model);
		JSpinner.DateEditor startHEditor = new JSpinner.DateEditor(
				startHourSpinner, "kk");
		startHourSpinner.setEditor(startHEditor);

		startDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		startDatePanel.setMaximumSize(new Dimension(windowWidth, 30));
		startDatePanel.add(Box.createRigidArea(new Dimension(0, 0)));
		startDatePanel.add(startDateLabel);
		startDatePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		startDatePanel.add(startDateChooser);
		startDatePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		startDatePanel.add(startHourSpinner);
		startDatePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// Arrangement of the "End Date?"-Row

		endDateChooser.setDateFormatString("yyyy-MM-dd");

		Calendar enhour = new GregorianCalendar();
		enhour.set(Calendar.HOUR_OF_DAY, 1);
		SpinnerDateModel model2 = new SpinnerDateModel(enhour.getTime(), null,
				null, Calendar.HOUR_OF_DAY);
		endHourSpinner.setModel(model2);
		JSpinner.DateEditor endHEditor = new JSpinner.DateEditor(
				endHourSpinner, "kk");
		endHourSpinner.setEditor(endHEditor);

		endDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		endDatePanel.setMaximumSize(new Dimension(windowWidth, 30));
		endDatePanel.add(Box.createRigidArea(new Dimension(0, 0)));
		endDatePanel.add(endDateLabel);
		endDatePanel.add(Box.createRigidArea(new Dimension(17, 0)));
		endDatePanel.add(endDateChooser);
		endDatePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		endDatePanel.add(endHourSpinner);
		endDatePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The arrangement of the row: "Number of Agents"

		agentsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		agentsPanel.setMaximumSize(new Dimension(windowWidth, 30));
		agentsPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		agentsPanel.add(agentsLabel);
		agentsPanel.add(Box.createRigidArea(new Dimension(18, 0)));
		agentsPanel.add(agentsSpinner);
		agentsPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The arrangement of the row: "Number of forums"

		communityModelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		communityModelPanel.setMaximumSize(new Dimension(windowWidth, 30));
		communityModelPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		communityModelPanel.add(communityModel);
		communityModelPanel.add(Box.createRigidArea(new Dimension(17, 0)));
		// forumsPanel.add(forumsSpinner);
		communityModelPanel.add(communityModelTextField);

		communityNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		communityNamePanel.setMaximumSize(new Dimension(windowWidth, 30));
		communityNamePanel.add(Box.createRigidArea(new Dimension(0, 0)));
		communityNamePanel.add(communityName);
		communityNamePanel.add(Box.createRigidArea(new Dimension(54, 0)));
		communityNamePanel.add(communityNameTextField);

		subCommunityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		subCommunityPanel.setMaximumSize(new Dimension(windowWidth, 30));
		subCommunityPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		subCommunityPanel.add(subCommunity);
		subCommunityPanel.add(Box.createRigidArea(new Dimension(33, 0)));
		subCommunityPanel.add(subCommunityTextField);

		communityModelPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1,
				1, new Color(238, 238, 238)));
		communityNamePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1,
				1, new Color(238, 238, 238)));
		subCommunityPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The arrangement of the "Start Simulation"-Button-Panel

		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.setMaximumSize(new Dimension(windowWidth, 30));
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 0)));
		// buttonPanel.add(changes);
		// buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		buttonPanel.add(startSimulationButton);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));
		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		//jobIdTextField.setMaximumSize(new Dimension(50, 25));
		//jobIdTextField.setMinimumSize(new Dimension(50, 25));
		jobIdTextField.setEditable(false);
		jobIdTextField.setText("   JobID");
		buttonPanel.add(jobIdTextField);
		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		JPanel statusPanel = new JPanel();
		statusPanel.setMaximumSize(new Dimension(100, 25));
		statusPanel.setMinimumSize(new Dimension(100, 25));
		statusPanel.setOpaque(true);
		statusPanel.setBackground(Color.BLACK);
		statusPanel.add(statusLabel);
		statusPanel.setPreferredSize(new Dimension(100,25));
		buttonPanel.add(statusPanel);
		
		// startSimulationButton.addActionListener(new
		// StartSimulationButtonAction());
		//csvButton.setMaximumSize(new Dimension(100,23));


		// Components of the "Main Down Right" Panel

		// The arrangement of the row with "Table Basic Panel"-Label

		tableBasicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		tableBasicPanel.setMaximumSize(new Dimension(windowWidth, 30));
		tableBasicPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		tableBasicPanel.add(extraLabel);
		tableBasicPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The arrangement of the table

		tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		tablePanel.setMaximumSize(new Dimension(380, 121));
		tablePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		setTable();
		tablePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));

		// The arrangement of the button "add property"

		tableButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		tableButtonPanel.setMaximumSize(new Dimension(windowWidth, 30));
		tableButtonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		tableButtonPanel.add(add);
		tableButtonPanel.add(Box.createRigidArea(new Dimension(189, 0)));
		tableButtonPanel.add(showButton);
		// tableButtonPanel.add(statusLabel);
		tableButtonPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1,
				new Color(238, 238, 238)));
		// add.addActionListener(new TableButtonAction());
		
		extraPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		extraPanel.setMaximumSize(new Dimension(windowWidth, 30));
		extraPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		//chartVisibleButton.setMaximumSize(new Dimension(150,23));
		//chartVisibleCheckBox.setText("Show chart");
		//chartVisibleCheckBox.

		//buttonPanel.add(Box.createRigidArea(new Dimension(36, 0)));
		extraPanel.add(chartVisibleCheckBox);
		extraPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		extraPanel.add(colorList);
		extraPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		extraPanel.add(shapeList);
		extraPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		
		extraPanel.add(csvButton);
		extraPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		extraPanel.add(csvSaveButton);
		extraPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		

		// Tooltips
		chooseFileButton.setToolTipText("Browse Directories");
		loadButton.setToolTipText("Load File");
		add.setToolTipText("Add Property");
		changes.setToolTipText("Apply Changes");
		startSimulationButton.setToolTipText("Start Simulation");
		this.save.setToolTipText("Save Data to File");
		// Icons
		ImageIcon folder = createImageIcon("/icons/folder.png", "folder");
		chooseFileButton.setIcon(folder);
		ImageIcon load = createImageIcon("/icons/load2.png", "load");
		loadButton.setIcon(load);
		ImageIcon addI = createImageIcon("/icons/add.png", "add");
		add.setIcon(addI);
		ImageIcon ok = createImageIcon("/icons/ok.png", "ok");
		changes.setIcon(ok);
		ImageIcon graph = createImageIcon("/icons/play2.png", "graph");
		startSimulationButton.setIcon(graph);
		ImageIcon saveI = createImageIcon("/icons/save.png", "save");
		this.save.setIcon(saveI);
		// Hover Effect for Browse Button
		chooseFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
			ImageIcon folderClose = createImageIcon("/icons/folder.png",
					"folder");
			ImageIcon folderOpen = createImageIcon("/icons/folder-open.png",
					"folder-open");

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				chooseFileButton.setIcon(folderOpen);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				chooseFileButton.setIcon(folderClose);
			}
		});
	}

	public void panelOrder() {

		// the order of the panels is set here

		// setLayout(new BorderLayout());

		int downHeigth = 340;

		main.setLayout(new BorderLayout());
		main.setPreferredSize(new Dimension(800, downHeigth));
		main.add(mainup, BorderLayout.NORTH);
		main.add(maindown);

		// maindowntab.add(maindown);

		mainup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		mainup.setLayout(new BoxLayout(mainup, BoxLayout.Y_AXIS));
		mainup.setPreferredSize(new Dimension(800, 40));
		mainup.add(fileChooserPanel);
		// mainup.add(simulationNPanel);

		maindown.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		maindown.setLayout(new BorderLayout());
		maindown.setPreferredSize(new Dimension(800, downHeigth));
		maindown.add(maindownleft, BorderLayout.WEST);
		maindown.add(maindownright, BorderLayout.CENTER);

		maindownleft.setLayout(new BoxLayout(maindownleft, BoxLayout.Y_AXIS));
		maindownleft.setPreferredSize(new Dimension(windowWidth, downHeigth));
		maindownleft.add(simulationNPanel);
		maindownleft.add(currentTPanel);
		maindownleft.add(startDatePanel);
		maindownleft.add(endDatePanel);
		maindownleft.add(agentsPanel);
		maindownleft.add(communityModelPanel);
		maindownleft.add(communityNamePanel);
		maindownleft.add(subCommunityPanel);
		maindownleft.add(Box.createVerticalStrut(10));

		maindownleft.add(Box.createHorizontalStrut(10));
		maindownleft.add(changes);

		maindownleft.add(Box.createVerticalStrut(10));
		maindownleft.add(new JSeparator(SwingConstants.HORIZONTAL));
		maindownleft.add(buttonPanel);
		maindownleft.add(Box.createVerticalStrut(10));

		maindownright.setLayout(new BoxLayout(maindownright, BoxLayout.Y_AXIS));
		maindownright.setPreferredSize(new Dimension(windowWidth, downHeigth));
		maindownright.add(tableBasicPanel);
		maindownright.add(tablePanel);
		maindownright.add(tableButtonPanel);
		maindownright.add(Box.createVerticalStrut(9));
		maindownright.add(new JSeparator(SwingConstants.HORIZONTAL));
		maindownright.add(extraPanel);
		maindownright.add(Box.createVerticalStrut(10));
	}

	public void setTable() {
		// The table is set

		table.setAutoCreateRowSorter(true);

		model.addColumn("Property");
		model.addColumn("Value");
		model.addRow(new Object[] { "" });

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(windowWidth, 185));
		tablePanel.add(scrollPane);
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
