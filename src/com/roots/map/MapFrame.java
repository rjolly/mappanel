package com.roots.map;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.net.URI;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import linoleum.application.Frame;

public class MapFrame extends Frame {
	private MapPanel.Gui gui;

	public MapFrame() {
		setName("Maps");
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setSize(800, 600);
		setTitle("Maps");
		setFrameIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/WebComponent16.gif")));
		setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/WebComponent24.gif")));
		setScheme("geo");
	}

	private void setLocation(final double lon, final double lat) {
		final MapPanel panel = gui.getMapPanel();
		panel.setZoom(8);
		final Point position = panel.computePosition(new Point2D.Double(lon, lat));
		panel.setCenterPosition(position);
		panel.repaint();
	}

	@Override
	public void open() {
		if (gui == null) {
			gui = new MapPanel.Gui();
			getContentPane().add(gui, BorderLayout.CENTER);
			setJMenuBar(gui.createMenuBar());
		}
		final URI uri = getURI();
		if (uri != null) {
			final String ssp = uri.getSchemeSpecificPart();
			final String s[] = ssp.split(",");
			setLocation(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		}
	}

	@Override
	public void close() {
		setURI(null);
		getContentPane().remove(gui);
		gui = null;
	}
}
