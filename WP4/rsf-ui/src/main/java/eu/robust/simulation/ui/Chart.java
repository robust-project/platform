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

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class generates the Chart.
 */
public class Chart {

	Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

	ChartPanel chartpanel;

	ArrayList<SimulationTask> tasks;
	XYSeriesCollection dataset = new XYSeriesCollection();
	XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

	XYPlot plot = new XYPlot();

	Shape circle = new Ellipse2D.Float(4.0f, 4.0f, 4.0f, 4.0f);

	boolean xAxisLog;
	boolean yAxisLog;
	boolean cumulative;
	boolean isZeroOmitted;

	//Map<Integer, Boolean> chartsThatAreShown;

	public Chart(ArrayList<SimulationTask> tasks, boolean xAxis, boolean yAxis, boolean cumulative) {

		this.tasks = tasks;
		this.xAxisLog = xAxis;
		this.yAxisLog = yAxis;
		//this.chartsThatAreShown = chartsThatAreShown;
		this.isZeroOmitted = false;

		this.cumulative = cumulative;

		colorMap.put(0, Color.RED);
		colorMap.put(1, Color.BLUE);
		colorMap.put(2, Color.GREEN);
		colorMap.put(3, Color.GRAY);
		colorMap.put(4, Color.CYAN);
		colorMap.put(5, Color.MAGENTA);
		colorMap.put(6, Color.ORANGE);

		chartpanel = createChart();

		setAxes();
	}

	/**
	 * @return the chartpanel that will be added to the added to the UI
	 */
	public ChartPanel createChart() {

		dataset.removeAllSeries();

		// for each Tab this will be conducted
		for (SimulationTask simTask : tasks) {

			if (simTask.getStatus() == SimulationStatus.FINISHED) {

				// it's checked whether the tab should be displayed in the chart
				// (can be manually changed on the UI)
				if (simTask.getChartVisibility() == true) {

					// changing Output-Data from the respective tab from
					// <String, String> to <Integer, Float>
					Map<Integer, Float> map = new TreeMap<Integer, Float>();

					for (Map.Entry<String, String> e : simTask.getOutputData()
							.entrySet()) {
						map.put(new Integer(e.getKey()),
								Float.parseFloat(simTask.getOutputData().get(
										new Integer(e.getKey()).toString())));
					}

					// getting the name of the respective Simulation, including
					// the number of the tab in case two simulations have the
					// same name
					String seriesName = "[" + simTask.getTab() + "] "
							+ simTask.getModel().getSimulationName();

					// for (Map.Entry<Integer, Float> e : map.entrySet()) {
					// System.out.println(e.getKey()+", "+e.getValue());
					// }
					
					if (cumulative == true) {
						// if the chart should be a cumulative one, extra
						// calculations have to be done with the original
						// results (which are passed to the methode
						// "createCumulativeDistribution"
						Map<Integer, Float> map2 = createCumulativeDistribution(map);
						// then the created Map with the calculated values will
						// be added to the current dataset
						createDataset(map2, seriesName, simTask.getColor());
					} else {
						// if the chart wil be a non cumulative one, the map
						// with the output results is added to the non
						// cumultaive dataset
						createDatasetNonCumulative(map, seriesName,
								simTask.getColor(), simTask.getShape());
					}
				}
			}
		}

		if (cumulative == true) {
			// the cumulative chart should only show the lines, no shapes
			renderer.setBaseShapesVisible(false);
			renderer.setBaseLinesVisible(true);
		} else {
			// the non cumulative chart should only show the shapes, not the
			// lines
			renderer.setBaseShapesVisible(true);
			renderer.setBaseLinesVisible(false);
		}

		renderer.setUseFillPaint(true);

		// dataset and renderer are added to the plot
		plot.setDataset(dataset);
		plot.setRenderer(renderer);

		// a new chart is created with the plot
		JFreeChart chart = new JFreeChart(plot);

		ChartPanel chartpanel = new ChartPanel(chart, true);
		return chartpanel;
	}

	/**
	 * In this method, maps are added to the dataset, which will be then
	 * displayed in the cumulative chart. Further depending on the chosen axis,
	 * it's decided whether zero values will be displayed or not.
	 * 
	 * @param map
	 *            this is the map that will be added to the dataset
	 * @param SimulationName
	 *            the name that will be shown in the legend of the chart
	 * @param tabColor
	 *            the color in which the map will be displayed in the chart
	 */
	public void createDataset(Map<Integer, Float> map, String SimulationName,
			Color tabColor) {

		XYSeries series1 = new XYSeries(SimulationName);

		renderer.setSeriesPaint(dataset.getSeriesCount(), tabColor);

		int[] array = new int[map.size()];
		int counter = 0;

		// the map is transformed in a array
		for (Map.Entry<Integer, Float> e : map.entrySet()) {
			Integer k = e.getKey();
			array[counter] = k;
			counter++;
		}

		//System.out.println(map.size() + "|" + array.length);

		Arrays.sort(array);

		// the values are added to the actual series.
		// according to the chosen axis setting, it's decided whether a zero can
		// be displayed or not (if an axis is a logarithmic one, zero values can
		// not be displayed). If a zero value is omitted, the boolean
		// "isZeroOmitted" is set to true, so that an information can be
		// displayed later
		for (int i = 0; i < array.length - 1; i++) {
			if (xAxisLog == true && yAxisLog == false) {
				if (array[i] > 0) {
					series1.add(array[i], map.get(array[i]));
					series1.add(array[i], map.get(array[i + 1]));
				} else {
					isZeroOmitted = true;
				}
			} else if (xAxisLog == false && yAxisLog == true) {
				if (map.get(array[i]) > 0 && map.get(array[i + 1]) > 0) {
					series1.add(array[i], map.get(array[i]));
					series1.add(array[i], map.get(array[i + 1]));
				} else {
					isZeroOmitted = true;
				}
			} else if (xAxisLog == true && yAxisLog == true) {
				if (map.get(array[i]) > 0 && map.get(array[i + 1]) > 0
						&& array[i] > 0) {
					series1.add(array[i], map.get(array[i]));
					series1.add(array[i], map.get(array[i + 1]));
					//System.out.println(i + "| " + array[i] + ": "
					//		+ map.get(array[i]) + ", " + map.get(array[i + 1]));
				} else {
					isZeroOmitted = true;
				}
			} else {
				series1.add(array[i], map.get(array[i]));
				series1.add(array[i], map.get(array[i + 1]));
			}
			if(xAxisLog == false && cumulative == true) {
				series1.add(0, 1);
			}
		}

		if (map.get(array[array.length - 1]) > 0) {
			series1.add(array[array.length - 1],
					map.get(array[array.length - 1]));
		} else {
			isZeroOmitted = true;
		}

		// the created series is added to the dataset
		dataset.addSeries(series1);

	}

	/**
	 * Here the series for the non cumulative chart is created and added to the
	 * dataset.
	 * 
	 * @param map
	 *            the results from the simulation
	 * @param SimulationName
	 *            the name of the simulation (including the tab number in case
	 *            different simulations have the same name)
	 * @param tabColor
	 *            the color in which the simulation result should be displayed
	 * @param tabShape
	 *            the shape in which the result should be displayed
	 */
	/**
	 * @param map
	 * @param SimulationName
	 * @param tabColor
	 * @param tabShape
	 */
	private void createDatasetNonCumulative(Map<Integer, Float> map,
			String SimulationName, Color tabColor, Shape tabShape) {

		XYSeries series1 = new XYSeries(SimulationName);

		renderer.setSeriesPaint(dataset.getSeriesCount(), tabColor);
		renderer.setSeriesFillPaint(dataset.getSeriesCount(), tabColor);
		renderer.setSeriesShape(dataset.getSeriesCount(), tabShape);

		// if an axis is logarithmic it has to be checked if zero values are
		// part of the result, and if yes, thay must be omitted
		for (Map.Entry<Integer, Float> e : map.entrySet()) {
			if (yAxisLog == true && e.getValue() > 0) {
				series1.add(e.getKey(), e.getValue());
			} else {
				if (yAxisLog == true && e.getValue() <= 0) {
					isZeroOmitted = true;
				} else {
					series1.add(e.getKey(), e.getValue());
				}
			}

		}

		// the created series is added to the dataset
		dataset.addSeries(series1);
	}

	/**
	 * If the chart should be cumulative, the output results of the simulation
	 * must be manipulated and calculated accordingly. This is done in this
	 * method.
	 * 
	 * @param map
	 *            containing the original output results of the simulation
	 * @return the map with the calculated result that can be used to display
	 *         the cumulative chart
	 */
	public Map<Integer, Float> createCumulativeDistribution(
			Map<Integer, Float> map) {
		// map.put(0, 0.0F);
		//
		// float summe = 0;
		//
		// // The data from the database is edited to fit our needs
		// for (Map.Entry<Integer, Float> e : map.entrySet()) {
		// Integer k = e.getKey();
		// summe = summe + map.get(k);
		// }
		//
		// HashMap<Integer, Float> map2 = new HashMap<Integer, Float>();
		//
		// for (Map.Entry<Integer, Float> e : map.entrySet()) {
		// Integer k = e.getKey();
		// map2.put(k, (map.get(k) / summe));
		// }
		//
		// float summe2 = 0;
		//
		// HashMap<Integer, Float> map3 = new HashMap<Integer, Float>();
		//
		// for (Map.Entry<Integer, Float> e : map.entrySet()) {
		// Integer k = e.getKey();
		// map3.put(k, (1 - (map2.get(k) + summe2)));
		// summe2 = summe2 + map2.get(k);
		// }
		//
		// // if (this.xAxisLog == false && this.yAxisLog == false) {
		// // map3.put(0,1.0F);
		// // }
		// for (Map.Entry<Integer, Float> e : map3.entrySet()){
		// System.out.println(e.getKey() + ": " + e.getValue());
		// }
		//
		// return map3;

		// map.put(0, 1f);

		float summe = 0;

		// Step 1: Adding up of all the original results
		for (Map.Entry<Integer, Float> e : map.entrySet()) {
			summe = summe + e.getValue();
		}

		HashMap<Integer, Float> map2 = new HashMap<Integer, Float>();

		// Step 2: creating a map (here map2) with the culutaive data
		for (Map.Entry<Integer, Float> e : map.entrySet()) {
			map2.put(e.getKey(), (e.getValue() / summe));
		}
		float teilsumme = 0;
		int max = 0;

		// Step 3: the reverse cumulative map (map3) is created
		for (Map.Entry<Integer, Float> e : map.entrySet()) {
			Integer k = e.getKey();
			if (k > max)
				max = k;
			float value = summe - teilsumme;
			// System.out.println(k+"| Value: "+map.get(k)+", Teilsumme: "+teilsumme+", Kumulativ: "+value);
			value /= summe;
			map2.put(k, value);
			teilsumme += map.get(k);
		}
		// map2.put(max + 1, 0f);

		return map2;

	}

	/**
	 * 
	 * checking which checkboxes are selected for the axis thus which axis
	 * should be logarithmic and which should be standard and passing the
	 * setting to the plot (which will later be used to set the chart)
	 * 
	 */
	public void setAxes() {
		if (this.xAxisLog == true && this.yAxisLog == true) {
			final NumberAxis domainAxis = new LogarithmicAxis("Log(x)");
			final NumberAxis rangeAxis = new LogarithmicAxis("Log(y)");
			plot.setDomainAxis(domainAxis);
			plot.setRangeAxis(rangeAxis);
		} else {
			if (this.xAxisLog == false && this.yAxisLog == false) {
				plot.setDomainAxis(new NumberAxis("Standard(x)"));
				plot.setRangeAxis(new NumberAxis("Standard(y)"));
			} else {
				if (this.xAxisLog == true && this.yAxisLog == false) {
					final NumberAxis domainAxis = new LogarithmicAxis("Log(x)");
					plot.setDomainAxis(domainAxis);
					plot.setRangeAxis(new NumberAxis("Standard(y)"));
				} else {
					if (this.xAxisLog == false && this.yAxisLog == true) {
						final NumberAxis rangeAxis = new LogarithmicAxis(
								"Log(y)");
						plot.setDomainAxis(new NumberAxis("Standard(x)"));
						plot.setRangeAxis(rangeAxis);
					}
				}
			}
		}
	}
}
