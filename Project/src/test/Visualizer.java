package test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import distance.DistanceMeasure;
import function.*;
import line.Vertex;
import simplifier.LineSimplifier;
import line.PolyLine;
import util.Tuple;
import util.Util;

public class Visualizer extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int margin = 10;
	private static final int marginInDrawing = 8;
	private static final int pointSize = 4;
	private static final int sliderWidth = 30;
	private static final int sliderMargin = 60;
	private static final int textMargin = 20;
	private static final int textHeight = 20;

	private int cur = 0;

	private static final OptimizationFunction[] errorMeasures = { new Max(), new Sum(), new SumMaxActive(), new SumMaxTotal(),
			new SumSumActive(), new WeightedSum()};
	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		Tuple<Tuple<PolyLine, LineSimplifier>, DistanceMeasure> fromArgs = Simplify.getFromArgs(args);

		PolyLine line = fromArgs.l.l;
		LineSimplifier simplifier = fromArgs.l.r;
		DistanceMeasure distance = fromArgs.r;

		Tuple<int[], double[]> solution = simplifier.simplify(line, distance);
		if (solution.r == null)
			solution.r = Util.errorFromSimplification(solution.l, line, distance);

		double[][] measures = new double[errorMeasures.length][];

		for (int i = 0; i < measures.length; i++) {
			measures[i] = errorMeasures[i].measure(solution.l, solution.r);
		}

		// start visualizer
		new Visualizer(line, solution.l, solution.r, measures);

	}

	public Visualizer(PolyLine l, int[] simplification, double[] error, double[][] measures) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int height = (int) (screenSize.height * 0.8);
		int width = (int) (screenSize.width * 0.8);
		Container content = getContentPane();

		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		int drawPanelSize = height - 2 * margin - getInsets().top - textMargin - textHeight;

		double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

		for (int i = 0; i < l.length(); i++) {
			Vertex p = l.getPoint(i);
			double x = p.getX();
			double y = p.getY();

			minX = (x < minX) ? x : minX;
			minY = (y < minY) ? y : minY;
			maxX = (x > maxX) ? x : maxX;
			maxY = (y > maxY) ? y : maxY;
		}

		double spanX = maxX - minX;
		double spanY = maxY - minY;

		double span = (spanX > spanY) ? spanX : spanY;

		for (int i = 0; i < l.length(); i++) {
			Vertex p = l.getPoint(i);

			double newX = marginInDrawing
					+ ((span - spanX) / 2 + (p.getX() - minX)) / span * (drawPanelSize - 2 * marginInDrawing);

			double newY = marginInDrawing + (drawPanelSize - 2 * marginInDrawing)
					- +((span - spanY) / 2 + (p.getY() - minY)) / span * (drawPanelSize - 2 * marginInDrawing);

			p.setX(newX);
			p.setY(newY);
		}

		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.WHITE);

		JCheckBox checkBoxPoints = new JCheckBox("Draw Points", true);
		checkBoxPoints.setSize(200, textHeight);
		checkBoxPoints.setLocation(width - margin - 200, margin * 3);
		content.add(checkBoxPoints);

		JPanel drawPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g1) {
				Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());

				g.setColor(Color.BLACK);
				g.drawRect(0, 0, drawPanelSize, drawPanelSize);

				// draw line
				Vertex before = l.getPoint(0);
				boolean beforeActive = true;
				int lastNonRemoved = 0;

				for (int i = 1; i < l.length(); i++) {
					boolean isActive = l.isActive(i);
					if (isActive && beforeActive) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.LIGHT_GRAY);
					}

					Vertex cur = l.getPoint(i);

					g.drawLine((int) before.getX(), (int) before.getY(), (int) cur.getX(), (int) cur.getY());

					if (isActive) {
						if (lastNonRemoved != i - 1) {
							g.setColor(Color.BLUE);
							Vertex last = l.getPoint(lastNonRemoved);
							g.drawLine((int) last.getX(), (int) last.getY(), (int) cur.getX(), (int) cur.getY());

						}

						lastNonRemoved = i;
					}

					beforeActive = isActive;
					before = cur;
				}

				if (!checkBoxPoints.isSelected())
					return;
				// draw points
				for (int i = 0; i < l.length(); i++) {
					if (l.isActive(i)) {
						g.setColor(Color.RED);

					} else {
						g.setColor(Color.GRAY);
					}

					Vertex p = l.getPoint(i);

					g.fillOval((int) p.getX() - pointSize / 2, (int) p.getY() - pointSize / 2, pointSize, pointSize);
				}

				// super.paintComponents(g);
			}

		};
		drawPanel.setSize(drawPanelSize, drawPanelSize);
		drawPanel.setLocation(margin, margin);

		checkBoxPoints.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawPanel.repaint();
			}
		});

		JLabel curText = new JLabel("n = " + l.length() + "; summed error = 0.0");

		curText.setLocation(drawPanel.getX() + textMargin, getHeight() - getInsets().top - textHeight - margin);

		curText.setSize(drawPanelSize * 2, 20);

		l.reset();

		JLabel nText = new JLabel("n");
		nText.setSize(70, textHeight);
		nText.setLocation(margin + drawPanelSize + textMargin + sliderMargin + sliderWidth,
				margin + drawPanelSize / 2 - textHeight);
		nText.setHorizontalAlignment(JLabel.CENTER);

		JTextField nField = new JTextField("" + l.length());
		nField.setSize(70, textHeight);
		nField.setLocation(nText.getX(), margin + drawPanelSize / 2);
		nField.setHorizontalAlignment(JTextField.CENTER);

		JSlider slider = new JSlider(JSlider.VERTICAL, 0, simplification.length, 0);
		slider.setSize(sliderWidth, drawPanelSize - 2 * marginInDrawing);
		slider.setLocation(drawPanel.getX() + drawPanel.getWidth() + sliderMargin, margin + marginInDrawing);

		ChangeListener sliderListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();

				update(value, l, simplification, error, curText, nField, drawPanel, null, measures);

			}
		};
		content.setFocusable(true);
		drawPanel.setFocusable(true);

		nField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (nField.getText().equals("")) {
					nField.setText("2");
				}
				int n = Integer.valueOf(nField.getText());

				if (n > l.length())
					n = l.length();
				if (n < 2)
					n = 2;

				update(l.length() - n, l, simplification, error, curText, nField, drawPanel, slider, measures);

			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});

		nField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				content.requestFocus();
			}
		});
		nField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				int key = e.getExtendedKeyCode();

				if (key == 32)

					if (key < 48 || key > 57) {
						e.setKeyChar((char) 0);
					}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JButton save = new JButton("Save Drawing");
		save.setLocation(checkBoxPoints.getX(), checkBoxPoints.getY() + textHeight + textMargin);
		save.setSize(checkBoxPoints.getSize());
		content.add(save);

		JFrame window = this;

		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				if (!(chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION)) {

					return;

				}

				String dir = chooser.getSelectedFile().getAbsolutePath();
				StringBuffer sb = new StringBuffer(dir);
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				String name = JOptionPane.showInputDialog("File name");

				File f = new File(dir + "/" + name + ".png");

				BufferedImage bi = new BufferedImage(drawPanelSize, drawPanelSize, BufferedImage.TYPE_INT_ARGB);

				Graphics2D g = bi.createGraphics();

				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

				// draw line
				Vertex before = l.getPoint(0);
				boolean beforeActive = true;
				int lastNonRemoved = 0;

				for (int i = 1; i < l.length(); i++) {
					boolean isActive = l.isActive(i);
					if (isActive && beforeActive) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.LIGHT_GRAY);
					}

					Vertex cur = l.getPoint(i);

					g.drawLine((int) before.getX(), (int) before.getY(), (int) cur.getX(), (int) cur.getY());

					if (isActive) {
						if (lastNonRemoved != i - 1) {
							g.setColor(Color.BLUE);
							Vertex last = l.getPoint(lastNonRemoved);
							g.drawLine((int) last.getX(), (int) last.getY(), (int) cur.getX(), (int) cur.getY());

						}

						lastNonRemoved = i;
					}

					beforeActive = isActive;
					before = cur;
				}

				if (checkBoxPoints.isSelected()) {
					// draw points
					for (int i = 0; i < l.length(); i++) {
						if (l.isActive(i)) {
							g.setColor(Color.RED);

						} else {
							g.setColor(Color.GRAY);
						}

						Vertex p = l.getPoint(i);

						g.fillOval((int) p.getX() - pointSize / 2, (int) p.getY() - pointSize / 2, pointSize,
								pointSize);
					}
				}

				try {
					ImageIO.write(bi, "PNG", f);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		slider.addChangeListener(sliderListener);

		content.add(curText);
		content.add(slider);
		content.add(nText);
		content.add(nField);

		drawPanel.setIgnoreRepaint(false);
		content.add(drawPanel);

		content.repaint();

	}

	private void update(int value, PolyLine l, int[] simplification, double[] error, JLabel curText, JTextField nField,
			JPanel drawPanel, JSlider slider, double[][] measures) {
		if (value > cur) {

			for (int i = cur + 1; i <= value; i++) {
				l.remove(simplification[i - 1]);
			}

		}

		if (value < cur) {
			for (int i = cur; i > value; i--) {
				l.add(simplification[i - 1]);
			}
		}

		cur = value;
		int n = (l.length() - cur);

		double err = 0;
		if (cur != 0)
			err = error[cur - 1];

		String s = "error = " + ((double) Math.round(err * 100) / 100.0);

		for (int i = 0; i < measures.length; i++) {
			s += "; " + errorMeasures[i] + " = "
					+ (cur == 0 ? 0.0 : ((double) Math.round(measures[i][cur - 1] * 100) / 100.0));
		}

		curText.setText(s);

		nField.setText("" + n);

		if (slider != null) {
			slider.setValue(cur);
		}

		drawPanel.repaint();
	}

}
