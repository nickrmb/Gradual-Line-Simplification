package line;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Visualizer extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int margin = 10;
	private static final int marginInDrawing = 8;
	private static final int pointSize = 4;
	private static final int sliderWidth = 30;
	private static final int sliderMargin = 80;
	private static final int textMargin = 80;

	private int cur = 0;
	

	public Visualizer(PolyLine l, int[] simplification, double[] error) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int height = (int) (screenSize.height * 0.8);
		int width = (int) (screenSize.width * 0.8);

		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		int drawPanelSize = height - 2 * margin - getInsets().top;

		double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

		for (int i = 0; i < l.length(); i++) {
			Point p = l.getPoint(i);
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

		System.out.println(spanX);

		for (int i = 0; i < l.length(); i++) {
			Point p = l.getPoint(i);

			double newX = marginInDrawing
					+ ((span - spanX) / 2 + (p.getX() - minX)) / span * (drawPanelSize - 2 * marginInDrawing);

			double newY = marginInDrawing
					+ ((span - spanY) / 2 + (p.getY() - minY)) / span * (drawPanelSize - 2 * marginInDrawing);

			p.setX(newX);
			p.setY(newY);
		}

		getContentPane().setLayout(null);

		JPanel drawPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g1) {
				Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0, 0, getWidth(), getHeight());

				// draw line
				Point before = l.getPoint(0);
				boolean beforeActive = true;
				int lastNonRemoved = 0;

				for (int i = 1; i < l.length(); i++) {
					boolean isActive = l.isActive(i);
					if (isActive && beforeActive) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.GRAY);
					}

					Point cur = l.getPoint(i);

					g.drawLine((int) before.getX(), (int) before.getY(), (int) cur.getX(), (int) cur.getY());

					if (isActive) {
						if (lastNonRemoved != i - 1) {
							g.setColor(Color.BLUE);
							Point last = l.getPoint(lastNonRemoved);
							g.drawLine((int) last.getX(), (int) last.getY(), (int) cur.getX(), (int) cur.getY());

						}

						lastNonRemoved = i;
					}

					beforeActive = isActive;
					before = cur;
				}

				// draw points
				for (int i = 0; i < l.length(); i++) {
					if (l.isActive(i)) {
						g.setColor(Color.BLACK);

					} else {
						g.setColor(Color.DARK_GRAY);
					}
					
					Point p = l.getPoint(i);

					g.fillOval((int) p.getX() - pointSize / 2, (int) p.getY() - pointSize / 2, pointSize, pointSize);
				}

				// super.paintComponents(g);
			}

		};
		drawPanel.setSize(drawPanelSize, drawPanelSize);
		drawPanel.setLocation(margin, margin);
		
		JLabel curText = new JLabel("n = " + l.length());
		
		JLabel errorText = new JLabel("summed error = 0.0");

		curText.setLocation(drawPanel.getX() + drawPanel.getWidth() + sliderMargin + sliderMargin, getHeight() / 2 - textMargin / 2);
		errorText.setLocation(drawPanel.getX() + drawPanel.getWidth() + sliderMargin + sliderMargin, getHeight() / 2 + textMargin / 2);

		curText.setSize(300, 20);
		errorText.setSize(300, 20);
		
		
		l.reset();
		
		JSlider slider = new JSlider(JSlider.VERTICAL, 0, simplification.length, 0);
		slider.setSize(sliderWidth, drawPanelSize - 2 * marginInDrawing);
		slider.setLocation(drawPanel.getX() + drawPanel.getWidth() + sliderMargin, margin + marginInDrawing);
		
		
		Container content = getContentPane();
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
				System.out.println(value);
				
				if(value > cur) {
					
					for(int i = cur + 1; i <= value; i++) {
						l.remove(simplification[i - 1]);
					}
					
				}
				
				if(value < cur) {
					for(int i = cur; i > value; i--) {
						l.add(simplification[i - 1]);
					}
				}
				
				cur = value;
				
				curText.setText("n = " + (l.length() - cur));
				double err = 0;
				if(cur != 0) 
					err = error[cur - 1];
				
				errorText.setText("summed error = " + err);

				content.repaint();
				
			}
		});
		
		
		content.add(curText);
		content.add(errorText);
		content.add(slider);

		drawPanel.setIgnoreRepaint(false);
		content.add(drawPanel);
		
		content.repaint();

	}

}
