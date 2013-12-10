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
 * Created by       	: Nicole Marienhagen
 *
 * Creation Time    	: 26.06.2012
 *
 * Created for Project  : ROBUST
 */

package eu.robust.simulation.ui;

import javax.swing.*;
import org.jfree.util.ShapeUtilities;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class UI extends JFrame {

	private static final boolean DEBUG = false;

	private RobustRequester rr;

	JTabbedPane maintab = new JTabbedPane();
	JPanel tabButtonPanel = new JPanel();
	Box bottomPanel = Box.createHorizontalBox();

	Boolean chartExists = false;

	private ArrayList<SimulationTask> tasks;
	private int nextId = 0;

	Chart chart;

	JButton refreshChartButton = new JButton("Refresh Chart");

	JLabel logarithmAxisLabel = new JLabel("Which axis should be logarithmic?");
	JCheckBox logXAxis = new JCheckBox("X-Axis", true);
	JCheckBox logYAxis = new JCheckBox("Y-Axis", true);

	JCheckBox cumulativePlot = new JCheckBox("Cumulative Plot", true);

	JLabel zeroValuesOmitted = new JLabel("(Zero values are omitted)");

	Map<Integer, Boolean> chartsThatAreShown = new HashMap<Integer, Boolean>();

	Map<Integer, Color> colorsThatCanBeChosen = new HashMap<Integer, Color>();
	Map<Integer, Shape> shapesThatCanBeChosen = new HashMap<Integer, Shape>();

	public UI() {
		// title
		super("Robust Simulation Framework GUI Module");

		tabButtonPanel
				.setLayout(new BoxLayout(tabButtonPanel, BoxLayout.Y_AXIS));

		setLayout(new BorderLayout());

		tasks = new ArrayList<SimulationTask>();
		// simulationModel = new ArrayList<SimulationModel>();

		add(tabButtonPanel, BorderLayout.EAST);
		add(maintab, BorderLayout.NORTH);

		add(bottomPanel, BorderLayout.SOUTH);

		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(refreshChartButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(25, 0)));
		bottomPanel.add(cumulativePlot);
		bottomPanel.add(Box.createRigidArea(new Dimension(25, 0)));
		bottomPanel.add(logarithmAxisLabel);
		bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		bottomPanel.add(logXAxis);
		bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		bottomPanel.add(logYAxis);
		bottomPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		zeroValuesOmitted.setForeground(Color.GRAY);

		SimulationTask task = new SimulationTask(getNextId(), 0,
				new SimulationModel());

		MainPanel main = new MainPanel(task.getId());

		initializePanel(main);

		if (DEBUG) {
			maintab.addTab(task.getTab() + "-" + task.getId() + ": " + "empty",
					main.main);
		} else {
			maintab.addTab("empty", main.main);
		}
		JPanel addempty = new JPanel();
		maintab.addTab("", addempty);
		maintab.setToolTipTextAt(maintab.getTabCount() - 1, "Add new Tab");
		ImageIcon addI = createImageIcon("/icons/add.png", "add");
		maintab.setIconAt(maintab.getTabCount() - 1, addI);

		maintab.addMouseListener(new tabListenerMouse());

		maintab.setSelectedIndex(0);
		maintab.addMouseListener(new PopClickListener());

		tasks.add(task);

		rr = new RobustRequester(tasks);

		colorsThatCanBeChosen.put(0, Color.RED);
		colorsThatCanBeChosen.put(1, Color.BLUE);
		colorsThatCanBeChosen.put(2, Color.MAGENTA);
		colorsThatCanBeChosen.put(3, Color.DARK_GRAY);
		colorsThatCanBeChosen.put(4, Color.ORANGE);
		colorsThatCanBeChosen.put(5, Color.GREEN);

		shapesThatCanBeChosen.put(0, ShapeUtilities.createDiagonalCross(3, 0));
		shapesThatCanBeChosen
				.put(1, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
		shapesThatCanBeChosen.put(2, new Rectangle2D.Double(-3.0, -3.0, 6.0,
				6.0));
		shapesThatCanBeChosen.put(3, ShapeUtilities.createUpTriangle(4));
		shapesThatCanBeChosen.put(4, ShapeUtilities.createDiamond(4));
		shapesThatCanBeChosen.put(5, ShapeUtilities.createDownTriangle(4));
		shapesThatCanBeChosen.put(6, ShapeUtilities.createRegularCross(4, 0));

	}

	private int getNextId() {

		int id = nextId;
		nextId++;
		return id;

	}

	/**
	 * This method refreshes the chart, which means that the old chart is
	 * deleted and a new chart (probably with new parameters) is created.
	 */
	private void refreshChart() {

		boolean cumulative;

		int width = getWidth();
		int height = getHeight();

		if (cumulativePlot.isSelected() == true) {
			cumulative = true;
		} else {
			cumulative = false;
		}

		// if there is already a chart, this chart is deleted
		if (chartExists == true) {
			remove(chart.chartpanel);
		}

		// a chart is created in the Chart-class
		chart = new Chart(tasks, logXAxis.isSelected(), logYAxis.isSelected(),
				cumulativePlot.isSelected());

		// if a zero value is omitted (because zero values can't be displayed if
		// the axis is logarithmic) an information is displayed in the
		// bottomPanel. If no zero value is omitted, than the information should
		// be removed
		if (chart.isZeroOmitted == true) {
			bottomPanel.add(zeroValuesOmitted);
		} else {
			bottomPanel.remove(zeroValuesOmitted);
		}

		add(chart.chartpanel, BorderLayout.CENTER);

		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		pack();
		repaint();

		chartExists = true;
	}

	/**
	 * 
	 * Used classed if the Button to start the simulation is pressed. The
	 * simulation is started and the Simulation Status of the tab is set to
	 * "STARTED".
	 * 
	 * If no color/shape is manually chosen before the simulation is started,
	 * the method in the class checks whether the next color/shape in line is
	 * already used for another tab. If yes, then it checks the next
	 * color/shape. If not the color/shape is passed to the SimulationTask
	 * 
	 * If the color/shape is manually chosen before the simulation is started,
	 * the chosen color/shape will be used.
	 * 
	 */
	class StartSimulationButtonAction implements ActionListener {

		public StartSimulationButtonAction(MainPanel m) {
			this.m = m;
		}

		private MainPanel m;

		public void actionPerformed(ActionEvent e) {

			// after checking whether the start simulation button was already
			// pressed on one of the tabs, maybe this has to be changed later.
			if (tasks.get(tasksGetTabAt(m.getId())).getStatus() != SimulationStatus.STARTED) {
				m.statusLabel.setName("working");
				m.statusLabel.setForeground(Color.ORANGE);
				m.timer.start();
				tasks.get(tasksGetTabAt(m.getId())).setStatus(
						SimulationStatus.STARTED);
				rr.createNewSimulationRun(tasksGetTabAt(m.getId()));

				m.jobIdTextField.setText(tasks.get(tasksGetTabAt(m.getId()))
						.getSimId());

				m.startSimulationButton.setToolTipText("Stop Simulation");
				ImageIcon pauseI = createImageIcon("/icons/stop.png", "stop");
				m.startSimulationButton.setIcon(pauseI);
			} else {
				m.timer.stop();

				tasks.get(tasksGetTabAt(m.getId())).setStatus(
						SimulationStatus.LOADED);
				rr.stopSimulationRun(tasksGetTabAt(m.getId()));

				m.jobIdTextField.setText("   JobID");

				m.startSimulationButton.setToolTipText("Start Simulation");
				ImageIcon playI = createImageIcon("/icons/play2.png", "play");
				m.startSimulationButton.setIcon(playI);

				m.statusLabel.setName("not started");
				m.statusLabel.setForeground(Color.RED);
			}

			// checking the color
			if (tasks.get(tasksGetTabAt(m.getId())).getColor() == null) {
				for (Map.Entry<Integer, Color> color : colorsThatCanBeChosen
						.entrySet()) {
					boolean colorAlreadyExists = false;
					for (SimulationTask simtask : tasks) {
						if (color.getValue().equals(simtask.getColor())) {
							colorAlreadyExists = true;
						}
					}
					if (colorAlreadyExists == false) {
						tasks.get(tasksGetTabAt(m.getId())).setColor(
								colorsThatCanBeChosen.get(color.getKey()));
						m.colorList.setSelectedIndex(color.getKey());
						break;
					}
				}
			} else {
				m.colorList.setSelectedItem(tasks.get(tasksGetTabAt(m.getId()))
						.getColor());
			}

			// check the shape
			if (tasks.get(tasksGetTabAt(m.getId())).getShape() == null) {
				for (Map.Entry<Integer, Shape> shape : shapesThatCanBeChosen
						.entrySet()) {
					boolean shapeAlreadyExists = false;
					for (SimulationTask simtask : tasks) {
						if (shape.getValue().equals(simtask.getShape())) {
							shapeAlreadyExists = true;
						}
					}
					if (shapeAlreadyExists == false) {
						tasks.get(tasksGetTabAt(m.getId())).setShape(
								shapesThatCanBeChosen.get(shape.getKey()));
						m.shapeList.setSelectedIndex(shape.getKey());
						break;
					}
				}
			} else {
				m.shapeList.setSelectedItem(tasks.get(tasksGetTabAt(m.getId()))
						.getShape());
			}

		}
	}

	class ChooseButtonAction implements ActionListener {

		public ChooseButtonAction(MainPanel m) {
			this.m = m;
		}

		private MainPanel m;

		public void actionPerformed(ActionEvent e) {
			int open = m.fileChooser.showOpenDialog(null);

			if (open == JFileChooser.APPROVE_OPTION) {

				// Enter the filePath into the TextField
				tasks.get(tasksGetTabAt(m.getId())).getModel()
						.setPath(m.fileChooser.getSelectedFile().getPath());
			}

			tasks.get(tasksGetTabAt(m.getId())).getModel().readFromFile();

			loadData(m);

		}
	}

	class LoadButtonAction implements ActionListener {

		public LoadButtonAction(MainPanel m) {
			this.m = m;
		}

		private MainPanel m;

		public void actionPerformed(ActionEvent e) {

			loadData(m);

		}
	}

	/**
	 * A new row is added to the table of additional properties
	 */
	class TableButtonAction implements ActionListener {

		public TableButtonAction(MainPanel m) {
			this.m = m;
		}

		private MainPanel m;

		public void actionPerformed(ActionEvent e) {

			m.model.addRow(new Object[] { "" });

		}
	}

	/**
	 * 
	 * puts the current date and time in the start date/time field if it is
	 * selected via the radio buttons
	 * 
	 */
	class RButtonAction implements ActionListener {

		public RButtonAction(MainPanel m) {
			this.m = m;
		}

		MainPanel m;

		public void actionPerformed(ActionEvent e) {
			// if yes is chosen, the current date should appear
			// as start date (incl. hour)
			SimpleDateFormat sdf = new SimpleDateFormat("kk");

			if (e.getActionCommand() == m.yesString) {
				m.startDateChooser.setDate(new Date());

				Calendar sthour = new GregorianCalendar();
				sthour.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(sdf.format(new Date())));
				SpinnerDateModel model = new SpinnerDateModel(sthour.getTime(),
						null, null, Calendar.HOUR_OF_DAY);
				m.startHourSpinner.setModel(model);
				JSpinner.DateEditor startHEditor = new JSpinner.DateEditor(
						m.startHourSpinner, "kk");
				m.startHourSpinner.setEditor(startHEditor);
			}

			if (e.getActionCommand() == m.noString) {
				if (m.newStartDate == null) {
					m.startDateChooser.setDate(null);

					Calendar sthour = new GregorianCalendar();
					sthour.set(Calendar.HOUR_OF_DAY, 1);
					SpinnerDateModel model = new SpinnerDateModel(
							sthour.getTime(), null, null, Calendar.HOUR_OF_DAY);
					m.startHourSpinner.setModel(model);
					JSpinner.DateEditor startHEditor = new JSpinner.DateEditor(
							m.startHourSpinner, "kk");
					m.startHourSpinner.setEditor(startHEditor);

				} else {
					m.startDateChooser.setDate(m.newStartDate);

					Calendar sthour = new GregorianCalendar();
					sthour.set(
							Calendar.HOUR_OF_DAY,
							Integer.parseInt(tasks
									.get(tasksGetTabAt(m.getId())).getModel()
									.getStartHour()));
					SpinnerDateModel model = new SpinnerDateModel(
							sthour.getTime(), null, null, Calendar.HOUR_OF_DAY);
					m.startHourSpinner.setModel(model);
					JSpinner.DateEditor startHEditor = new JSpinner.DateEditor(
							m.startHourSpinner, "kk");
					m.startHourSpinner.setEditor(startHEditor);
				}
			}

			pack();
			repaint();
		}
	}

	class ChangeButtonListener implements ActionListener {

		MainPanel m;

		public ChangeButtonListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {

			updateData(m);

			tasks.get(tasksGetTabAt(m.getId())).getModel().print();

		}
	}

	class SaveButtonListener implements ActionListener {

		MainPanel m;

		public SaveButtonListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {

			m.saveChooser = new JFileChooser(tasks
					.get(tasksGetTabAt(m.getId())).getModel().getPath());

			m.saveChooser.showSaveDialog(null);
			String tmpPath = m.saveChooser.getSelectedFile().getPath();
			String oldPath = tasks.get(tasksGetTabAt(m.getId())).getModel()
					.getPath();
			tasks.get(tasksGetTabAt(m.getId())).getModel().setPath(tmpPath);

			boolean check = tasks.get(tasksGetTabAt(m.getId())).getModel()
					.writeToFile(0);

			if (!check) {

				JFrame frame = new JFrame();

				// Custom button text
				Object[] options = { "Yes", "No", "Cancel" };
				int n = JOptionPane.showOptionDialog(frame,
						"File already exists. " + "Overwrite it?", "Warning",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[2]);

				// System.out.println(n);
				n++;
				if (n == 1) {
					m.filePathTextField.setText(tmpPath);
				} else {
					tasks.get(tasksGetTabAt(m.getId())).getModel()
							.setPath(oldPath);
				}
				tasks.get(tasksGetTabAt(m.getId())).getModel().writeToFile(n);
			}

		}
	}

	class TextField implements ActionListener {

		private MainPanel m;

		public TextField(MainPanel m) {
			this.m = m;
		}

		// Show text when user presses ENTER.
		public void actionPerformed(ActionEvent ae) {
			String simulationName = m.simulationNTextField.getText();
			if (DEBUG) {
				maintab.setTitleAt(maintab.getSelectedIndex(),
						tasks.get(tasksGetTabAt(m.getId())).getTab() + "-"
								+ tasks.get(tasksGetTabAt(m.getId())).getId()
								+ ": " + simulationName);
			} else {
				maintab.setTitleAt(maintab.getSelectedIndex(), simulationName);
			}

		}
	}

	/**
	 * 
	 * A new window (JFrame) opens and shows the results of the simulation
	 * 
	 */
	class CsvButtonListener implements ActionListener {

		private MainPanel m;

		public CsvButtonListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {
			if (tasks.get(tasksGetTabAt(m.getId())).getStatus() == SimulationStatus.FINISHED) {
				JFrame csvOutput = new JFrame(tasks
						.get(tasksGetTabAt(m.getId())).getModel()
						.getSimulationName()
						+ "'s Result");
				csvOutput.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				int w = 300;
				int h = 300;
				csvOutput.setSize(w, h);
				csvOutput.setPreferredSize(new Dimension(w, h));

				Dimension d = csvOutput.getToolkit().getScreenSize();
				csvOutput.setLocation(((d.width - w) / 2) + 450,
						((d.height - h) / 2) - 100);

				csvOutput.setVisible(true);

				Map<String, String> resultOutputData = tasks.get(
						tasksGetTabAt(m.getId())).getOutputData();

				Map<Integer, Float> mapSorted = new TreeMap<Integer, Float>();

				// changing Map-Data from String, String to Integer, Float

				for (Map.Entry<String, String> eee : resultOutputData
						.entrySet()) {
					String k = eee.getKey();
					Integer number = new Integer(k);
					mapSorted.put(number, Float.parseFloat(resultOutputData
							.get(number.toString())));
				}

				JPanel csvPanel = new JPanel();
				csvOutput.add(csvPanel);

				JTextArea textArea = new JTextArea();
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane
						.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setPreferredSize(new Dimension(250, 250));
				textArea.setEditable(false);

				csvPanel.add(scrollPane);
				for (Entry<Integer, Float> line : mapSorted.entrySet()) {
					textArea.append(line.getKey() + ";" + line.getValue()
							+ "\n");

				}

				// tasks.get(m.getId()).writeResult();

			}
		}
	}

	class CsvSaveButtonListener implements ActionListener {

		private MainPanel m;

		public CsvSaveButtonListener(MainPanel m) {
			this.m = m;
		}

		// A new window will open and show the results of the simulation
		public void actionPerformed(ActionEvent e) {
			if (tasks.get(tasksGetTabAt(m.getId())).getStatus() == SimulationStatus.FINISHED) {
				tasks.get(tasksGetTabAt(m.getId())).writeResult();
			}
		}
	}

	class ShowButtonListener implements ActionListener {

		private MainPanel m;

		public ShowButtonListener(MainPanel m) {
			this.m = m;
		}

		// TODO
		public void actionPerformed(ActionEvent e) {

			int width = (int) (getWidth() * 0.5f);
			int height = (int) (getHeight() * 0.5f);

			JFrame paraOutput = new JFrame(tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getSimulationName()
					+ "'s Result");
			paraOutput.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			paraOutput.setSize(width, height);
			paraOutput.setPreferredSize(new Dimension(width, height));
			int d = 25;
			paraOutput.setLocation((int) getLocation().getX() + d,
					(int) getLocation().getY() + d);

			paraOutput.setVisible(true);

			Map<String, String> paraData = tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getMap();

			JPanel paraPanel = new JPanel();
			paraOutput.add(paraPanel);

			JTextArea textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setPreferredSize(new Dimension((int) (width * 0.95f),
					(int) (height * 0.87f)));
			textArea.setEditable(false);

			textArea.append("{ \n");
			paraPanel.add(scrollPane);
			int counter = 0;
			int max = paraData.size();
			for (Entry<String, String> line : paraData.entrySet()) {
				if (counter < max - 1) {
					textArea.append("  \"" + line.getKey() + "\": \""
							+ line.getValue() + "\", \n");
					counter++;
				} else {
					textArea.append("  \"" + line.getKey() + "\": \""
							+ line.getValue() + "\"\n");
				}
			}
			textArea.append("}");

			// tasks.get(m.getId()).writeResult();

		}
	}

	/**
	 * 
	 * this class is triggered if the "show chart"-checkbox is changed and
	 * switches the chart on and off.
	 * 
	 */
	class ChartVisibleButtonListener implements ActionListener {

		private MainPanel m;

		public ChartVisibleButtonListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {

			tasks.get(tasksGetTabAt(m.getId())).setChartVisibility(
					m.chartVisibleCheckBox.isSelected());

			refreshChart();

			pack();
			repaint();
		}
	}

	/**
	 * 
	 * Triggered when the "refresh chart" button is pushed
	 * 
	 */
	class RefreshChartListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			refreshChart();
		}
	}

	/**
	 * 
	 * Is triggered when the setting for the x-axis is changed
	 * 
	 */
	class LogXAxisListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			refreshChart();
		}

	}

	/**
	 * 
	 * Is triggered when the setting for the y-axis is changed
	 * 
	 */
	class LogYAxisListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			refreshChart();
		}
	}

	/**
	 * 
	 * Is triggered when the cumulative-checkbox is switched on or off
	 * 
	 */
	class CumulativePlotListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			refreshChart();
		}
	}

	/**
	 * 
	 * When the color of the tab is changed in the combobox, the chart will be
	 * created new. The simulation result of this tab is then represented in the
	 * chosen color
	 * 
	 */
	class ChooseColorListener implements ActionListener {

		public ChooseColorListener(MainPanel m) {
			this.m = m;
		}

		MainPanel m;

		public void actionPerformed(ActionEvent e) {

			JComboBox cb = (JComboBox) e.getSource();
			int chosenColor = (int) cb.getSelectedIndex();

			tasks.get(tasksGetTabAt(m.getId())).setColor(
					colorsThatCanBeChosen.get(chosenColor));

			if (chartExists == true) {
				refreshChart();
			}
		}

	}

	/**
	 * 
	 * When the shape of the tab is changed in the combobox, the chart will be
	 * created new. The simulation result of this tab is then represented in the
	 * chosen shape (if it's non cumulative)
	 * 
	 */
	class ChooseShapeListener implements ActionListener {

		public ChooseShapeListener(MainPanel m) {
			this.m = m;
		}

		MainPanel m;

		public void actionPerformed(ActionEvent e) {

			JComboBox cb = (JComboBox) e.getSource();
			int chosenShape = (int) cb.getSelectedIndex();

			tasks.get(tasksGetTabAt(m.getId())).setShape(
					shapesThatCanBeChosen.get(chosenShape));

			if (chartExists == true) {
				refreshChart();
			}

		}

	}

	class statusLabelActionListener implements ActionListener {

		private MainPanel m;
		int cInt = 0;
		boolean cBool = false;

		public statusLabelActionListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {
			// if (!cBool) {

			if (rr.checkForSimulationFinish(tasksGetTabAt(m.getId()))) {

				m.statusLabel.setForeground(Color.GREEN);
				m.statusLabel.setText("finished");
				rr.retrieveSimulationResult(tasksGetTabAt(m.getId()));

				tasks.get(tasksGetTabAt(m.getId())).setStatus(
						SimulationStatus.FINISHED);
				cBool = true;

				// Creating the Chartpanel (with the help of the Class "Chart")
				// and adding the chart
				// After connecting upper part with lower part of the GUI, the
				// following should look like this
				// Chart chart = new Chart(m,
				// tasks.get(tasksGetTabAt(m.getId())).getInputData().get("templateName"),
				// +threadsize);

				tasks.get(tasksGetTabAt(m.getId())).setChartVisibility(
						m.chartVisibleCheckBox.isSelected());

				refreshChart();

				m.timer.stop();

				ImageIcon playI = createImageIcon("/icons/play2.png", "play");
				m.startSimulationButton.setIcon(playI);

			} else {
				m.statusLabel.setForeground(Color.ORANGE);
				if (cInt == 0) {
					m.statusLabel.setText("working");
				} else if (cInt == 1) {
					m.statusLabel.setText("working .");
				} else if (cInt == 2) {
					m.statusLabel.setText("working ..");
				} else if (cInt == 3) {
					m.statusLabel.setText("working ...");
					cInt = -1;
				}
				cInt++;
			}
			// }

		}
	}

	private void addTab() {

		int s = maintab.getComponentCount();

		SimulationTask task = new SimulationTask(getNextId(), s - 1,
				new SimulationModel());

		MainPanel m = new MainPanel(task.getId());
		initializePanel(m);
		if (DEBUG) {
			maintab.insertTab(task.getTab() + "-" + task.getId() + ": empty",
					null, m.main, "", s - 1);
		} else {
			maintab.insertTab("empty", null, m.main, "", s - 1);
		}

		maintab.setSelectedIndex(s - 1);

		tasks.add(task);

		pack();
		repaint();

	}

	private boolean removeTab() {

		int s = maintab.getSelectedIndex();
		boolean check = false;

		if (maintab.getComponentCount() > 2) {

			if (s == 0)
				maintab.setSelectedIndex(s + 1);
			else
				maintab.setSelectedIndex(s - 1);
			maintab.remove(s);

			tasks.remove(s);

			for (int i = s; i < tasks.size(); i++) {

				tasks.get(i).setTab(i);

			}

			check = true;

			// if (!guiData.get(s).getPath().isEmpty())
			refreshChart();

			pack();
			repaint();

		}
		return check;

	}

	private void duplicateTab(int id) {

		int s = maintab.getComponentCount();
		int t = maintab.getSelectedIndex();
		SimulationModel data = new SimulationModel(tasks.get(tasksGetTabAt(id))
				.getModel());

		SimulationTask task = new SimulationTask(getNextId(), s - 1, data);

		task.setName(tasks.get(t).getName());
		MainPanel m = new MainPanel(task.getId());
		m.filePathTextField.setText(data.getPath());
		tasks.add(task);

		initializePanel(m);

		if (DEBUG) {
			maintab.insertTab(
					task.getTab() + "-" + task.getId() + ": "
							+ data.getSimulationName(), null, m.main, "", s - 1);
		} else {
			maintab.insertTab(data.getSimulationName(), null, m.main, "", s - 1);
		}

		maintab.setSelectedIndex(s - 1);

		loadData(m);

		pack();
		repaint();

	}

	class removeButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			removeTab();

		}

	}

	class duplicateButtonListener implements ActionListener {

		MainPanel m;

		public duplicateButtonListener(MainPanel m) {
			this.m = m;
		}

		public void actionPerformed(ActionEvent e) {

			updateData(m);

			duplicateTab(m.getId());

		}

	}

	class tabListenerMouse implements MouseListener {

		private boolean check = false;

		public void mousePressed(MouseEvent e) {

			if (maintab.getSelectedIndex() == maintab.getComponentCount() - 1) {
				if (e.getButton() == MouseEvent.BUTTON1 && !check) {

					check = true;
					addTab();
					check = false;

				} else {
					maintab.setSelectedIndex(maintab.getComponentCount() - 2);
				}
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class PopUpTab extends JPopupMenu {
		JMenuItem duplicate;
		JMenuItem remove;
		int id;

		public PopUpTab(final int id) {
			// generate PopUp Items
			duplicate = new JMenuItem("Duplicate Tab");
			remove = new JMenuItem("Close Tab");
			this.id = id;

			// add Action
			duplicate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					duplicateTab(tasksGetIdAt(id));
				}
			});
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					removeTab();
				}
			});

			// add PopUp Item to PopUp
			add(duplicate);
			add(remove);
		}
	}

	class PopClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (!(maintab.getSelectedIndex() == maintab.getComponentCount() - 1)) {
				if (e.isPopupTrigger())
					doPop(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (!(maintab.getSelectedIndex() == maintab.getComponentCount() - 1)) {
				if (e.isPopupTrigger())
					doPop(e);
			}
		}

		private void doPop(MouseEvent e) {
			if (!(maintab.getSelectedIndex() == maintab.getComponentCount() - 1)) {
				PopUpTab menu = new PopUpTab(maintab.getSelectedIndex());
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void initializePanel(MainPanel mp) {

		mp.chooseFileButton.addActionListener(new ChooseButtonAction(mp));
		mp.loadButton.addActionListener(new LoadButtonAction(mp));

		mp.currentDateYesRadioButton.addActionListener(new RButtonAction(mp));
		mp.currentDateNoRadioButton.addActionListener(new RButtonAction(mp));

		mp.startSimulationButton
				.addActionListener(new StartSimulationButtonAction(mp));

		mp.csvButton.addActionListener(new CsvButtonListener(mp));
		mp.csvSaveButton.addActionListener(new CsvSaveButtonListener(mp));

		mp.showButton.addActionListener(new ShowButtonListener(mp));

		mp.chartVisibleCheckBox
				.addActionListener(new ChartVisibleButtonListener(mp));

		refreshChartButton.addActionListener(new RefreshChartListener());

		logXAxis.addActionListener(new LogXAxisListener());

		logYAxis.addActionListener(new LogYAxisListener());

		cumulativePlot.addActionListener(new CumulativePlotListener());

		mp.add.addActionListener(new TableButtonAction(mp));

		mp.simulationNTextField.addActionListener(new TextField(mp));

		mp.changes.addActionListener(new ChangeButtonListener(mp));

		mp.save.addActionListener(new SaveButtonListener(mp));

		mp.colorList.addActionListener(new ChooseColorListener(mp));

		mp.shapeList.addActionListener(new ChooseShapeListener(mp));

		JButton removeButton = new JButton("");
		removeButton.setToolTipText("Close Tab");
		ImageIcon icon = createImageIcon("/icons/delete.png", "close");
		removeButton.setIcon(icon);
		JButton duplicateButton = new JButton("");
		duplicateButton.setToolTipText("Duplicate Tab");
		ImageIcon icon2 = createImageIcon("/icons/clone.png", "clone");
		duplicateButton.setIcon(icon2);

		duplicateButton.addActionListener(new duplicateButtonListener(mp));
		removeButton.addActionListener(new removeButtonListener());
		mp.fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		mp.fileChooserPanel.add(new JSeparator(SwingConstants.VERTICAL));
		mp.fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		mp.fileChooserPanel.add(duplicateButton);
		mp.fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		mp.fileChooserPanel.add(removeButton);
		mp.fileChooserPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		mp.timer = new Timer(500, new statusLabelActionListener(mp));

	}

	private void loadData(MainPanel m) {

		// If existing, adding the data into the intended fields

		m.statusLabel.setForeground(Color.RED);
		m.statusLabel.setText("not started");

		m.filePathTextField.setText(tasks.get(tasksGetTabAt(m.getId()))
				.getModel().getPath());

		// Template name
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getSimulationName()
				.isEmpty()) {
			String simulationName = tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getSimulationName();
			m.simulationNTextField.setText(simulationName);
			if (DEBUG) {
				maintab.setTitleAt(maintab.getSelectedIndex(),
						tasks.get(tasksGetTabAt(m.getId())).getTab() + "-"
								+ tasks.get(tasksGetTabAt(m.getId())).getId()
								+ ": " + simulationName);
			} else {
				maintab.setTitleAt(maintab.getSelectedIndex(), simulationName);
			}
		} else {
			m.simulationNTextField.setText("--No Data Found--");
		}

		// Current time = Start time?
		if (tasks.get(tasksGetTabAt(m.getId())).getModel().isUseCurrentTime() == true) {
			m.currentDateYesRadioButton.setSelected(true);
			m.startDateChooser.setDate(new Date());
		} else {
			m.currentDateNoRadioButton.setSelected(true);
		}

		// Start date
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getStartDay()
				.isEmpty()
				&& !tasks.get(tasksGetTabAt(m.getId())).getModel()
						.getStartMonth().isEmpty()
				&& !tasks.get(tasksGetTabAt(m.getId())).getModel()
						.getStartDay().isEmpty()) {
			if (!tasks.get(tasksGetTabAt(m.getId())).getModel()
					.isUseCurrentTime()) {
				String startDateString = tasks.get(tasksGetTabAt(m.getId()))
						.getModel().getStartYear()
						+ "-"
						+ tasks.get(tasksGetTabAt(m.getId())).getModel()
								.getStartMonth()
						+ "-"
						+ tasks.get(tasksGetTabAt(m.getId())).getModel()
								.getStartDay();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date convertedDate = null;
				try {
					convertedDate = dateFormat.parse(startDateString);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				m.newStartDate = convertedDate;
				m.startDateChooser.setDate(convertedDate);
			}
		}

		// Start Hour
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getStartHour()
				.isEmpty()) {
			Calendar sthour = new GregorianCalendar();
			sthour.set(
					Calendar.HOUR_OF_DAY,
					Integer.parseInt(tasks.get(tasksGetTabAt(m.getId()))
							.getModel().getStartHour()));
			SpinnerDateModel model = new SpinnerDateModel(sthour.getTime(),
					null, null, Calendar.HOUR_OF_DAY);
			m.startHourSpinner.setModel(model);
			JSpinner.DateEditor startHEditor = new JSpinner.DateEditor(
					m.startHourSpinner, "kk");
			m.startHourSpinner.setEditor(startHEditor);
		}

		// End date
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getEndDay()
				.isEmpty()
				&& !tasks.get(tasksGetTabAt(m.getId())).getModel()
						.getEndMonth().isEmpty()
				&& !tasks.get(tasksGetTabAt(m.getId())).getModel().getEndDay()
						.isEmpty()) {

			String endDateString = tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getEndYear()
					+ "-"
					+ tasks.get(tasksGetTabAt(m.getId())).getModel()
							.getEndMonth()
					+ "-"
					+ tasks.get(tasksGetTabAt(m.getId())).getModel()
							.getEndDay();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date convertedDate = null;
			try {
				convertedDate = dateFormat.parse(endDateString);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			m.newEndDate = convertedDate;
			m.endDateChooser.setDate(convertedDate);

		}

		// End Hour
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getEndHour()
				.isEmpty()) {
			Calendar sthour = new GregorianCalendar();
			sthour.set(
					Calendar.HOUR_OF_DAY,
					Integer.parseInt(tasks.get(tasksGetTabAt(m.getId()))
							.getModel().getEndHour()));
			SpinnerDateModel model = new SpinnerDateModel(sthour.getTime(),
					null, null, Calendar.HOUR_OF_DAY);
			m.endHourSpinner.setModel(model);
			JSpinner.DateEditor endHEditor = new JSpinner.DateEditor(
					m.endHourSpinner, "kk");
			m.endHourSpinner.setEditor(endHEditor);
		}

		// Communiuty Model
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getCommunityModel()
				.isEmpty()) {
			m.communityModelTextField.setText(tasks
					.get(tasksGetTabAt(m.getId())).getModel()
					.getCommunityModel());
		}

		// Number of agents
		if (tasks.get(tasksGetTabAt(m.getId())).getModel().getNumberOfAgents() > -1) {
			m.agentsSpinner.setValue(tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getNumberOfAgents());
		}

		// Community Name
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getCommunity()
				.isEmpty()) {
			m.communityNameTextField.setText(tasks
					.get(tasksGetTabAt(m.getId())).getModel().getCommunity());
		}

		// subCommunity
		if (!tasks.get(tasksGetTabAt(m.getId())).getModel().getSubCommunity()
				.isEmpty()) {
			m.subCommunityTextField.setText(tasks.get(tasksGetTabAt(m.getId()))
					.getModel().getSubCommunity());
		}

		// Checking if extra data is available and putting this straight to
		// the table
		for (String elem : tasks.get(tasksGetTabAt(m.getId())).getModel()
				.getAdditionalProperties().keySet()) {

			// m.model.insertRow(0,
			// new Object[] {
			// elem,
			// tasks.get(tasksGetTabAt(m.getId()))
			// .getInputData().get(elem) });
			HashMap<String, String> fromTable = new HashMap<String, String>();
			for (int i = 0; i < m.table.getRowCount(); i++) {
				if (m.table.getModel().getValueAt(i, 1) != null) {
					fromTable.put((String) m.table.getModel().getValueAt(i, 0),
							(String) m.table.getModel().getValueAt(i, 1));
				}
			}

			fromTable.put(elem, tasks.get(tasksGetTabAt(m.getId())).getModel()
					.getAdditionalProperties().get(elem));

			m.model.setRowCount(0);

			for (String s : fromTable.keySet()) {
				m.model.insertRow(0, new Object[] { s, fromTable.get(s) });
			}

		}

		// Update SimulationTask

		tasks.get(tasksGetTabAt(m.getId())).setStatus(SimulationStatus.LOADED);

	}

	private void updateData(MainPanel m) throws java.lang.NumberFormatException {

		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setPath(m.filePathTextField.getText());

		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setSimulationName(m.simulationNTextField.getText());
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setCommunityModel(m.communityModelTextField.getText());
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setCommunity(m.communityNameTextField.getText());
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setSubCommunity(m.subCommunityTextField.getText());
		tasks.get(tasksGetTabAt(m.getId()))
				.getModel()
				.setNumberOfAgents(
						Integer.parseInt(m.agentsSpinner.getValue().toString()));

		Date startHourDate = (Date) m.startHourSpinner.getValue();
		Calendar calendar1 = GregorianCalendar.getInstance(); // creates a
																// new
																// calendar
																// instance
		calendar1.setTime(startHourDate); // assigns calendar to given date
		tasks.get(tasksGetTabAt(m.getId()))
				.getModel()
				.setStartHour(
						String.valueOf(calendar1.get(Calendar.HOUR_OF_DAY)));
		Date endHourDate = (Date) m.endHourSpinner.getValue();
		Calendar calendar2 = GregorianCalendar.getInstance(); // creates a
																// new
																// calendar
																// instance
		calendar2.setTime(endHourDate); // assigns calendar to given date
		tasks.get(tasksGetTabAt(m.getId()))
				.getModel()
				.setEndHour(String.valueOf(calendar2.get(Calendar.HOUR_OF_DAY)));

		Calendar start = m.startDateChooser.getCalendar();
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setStartYear(String.valueOf(start.get(Calendar.YEAR)));
		tasks.get(tasksGetTabAt(m.getId()))
				.getModel()
				.setStartMonth(
						String.valueOf((Integer.parseInt(String.valueOf(start
								.get(Calendar.MONTH) + 1)))));
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setStartDay(String.valueOf(start.get(Calendar.DAY_OF_MONTH)));

		Calendar end = m.endDateChooser.getCalendar();
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setEndYear(String.valueOf(end.get(Calendar.YEAR)));
		tasks.get(tasksGetTabAt(m.getId()))
				.getModel()
				.setEndMonth(
						String.valueOf((Integer.parseInt(String.valueOf(end
								.get(Calendar.MONTH) + 1)))));
		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setEndDay(String.valueOf(end.get(Calendar.DAY_OF_MONTH)));

		HashMap<String, String> fromTable = new HashMap<String, String>();
		for (int i = 0; i < m.table.getRowCount(); i++) {
			if (m.table.getModel().getValueAt(i, 1) != null) {
				fromTable.put((String) m.table.getModel().getValueAt(i, 0),
						(String) m.table.getModel().getValueAt(i, 1));
			}
		}

		tasks.get(tasksGetTabAt(m.getId())).getModel()
				.setAdditionalProperties(fromTable);

		if (DEBUG) {
			maintab.setTitleAt(maintab.getSelectedIndex(),
					tasks.get(tasksGetTabAt(m.getId())).getTab()
							+ "-"
							+ tasks.get(tasksGetTabAt(m.getId())).getId()
							+ ": "
							+ tasks.get(tasksGetTabAt(m.getId())).getModel()
									.getSimulationName());
		} else {
			maintab.setTitleAt(maintab.getSelectedIndex(),
					tasks.get(tasksGetTabAt(m.getId())).getModel()
							.getSimulationName());
		}

	}

	// returns the Tab Count of the Id
	private int tasksGetTabAt(int m) {

		for (int i = 0; i < tasks.size(); i++) {
			if (m == tasks.get(i).getId())
				return tasks.get(i).getTab();
		}

		return -1;

	}

	// returns the Id of the Tab Count
	@SuppressWarnings("unused")
	private int tasksGetIdAt(int t) {

		for (int i = 0; i < tasks.size(); i++) {
			if (t == tasks.get(i).getTab())
				return tasks.get(i).getId();
		}

		return -1;

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Setting the default settings of the frame
		UI frame = new UI();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		int w = 1024;
		int h = 700;
		frame.setSize(w, h);
		frame.setPreferredSize(new Dimension(w, h));
		// frame.setResizable(false);

		Dimension d = frame.getToolkit().getScreenSize();
		frame.setLocation((d.width - w) / 2, /* (d.height - h) / 2 */0);

		frame.setVisible(true);
	}

}
