package com.roots.map;

import java.awt.BorderLayout;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
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

	@Override
	public URI getURI() {
		if (gui != null) try {
			final MapPanel panel = gui.getMapPanel();
			final double[] location = panel.getLocationAsDouble();
			String str = String.format("%f,%f", location[1], location[0]);
			final int zoom = panel.getZoom();
			if (zoom != 8) {
				str += ";zoom=" + zoom;
			}
			return new URI("geo", str, null);
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void open() {
		if (gui == null) {
			gui = new MapPanel.Gui();
			getContentPane().add(gui, BorderLayout.CENTER);
			setJMenuBar(gui.createMenuBar());
		}
		final URI uri = super.getURI();
		if (uri != null) {
			final Map<String, String> map = new HashMap<>();
			final String ssp = uri.getSchemeSpecificPart();
			final String ss[] = ssp.split(";");
			for (final String kv : ss) {
				final String s[] = kv.split("=");
				if (s.length > 1) {
					map.put(s[0], s[1]);
				}
			}
			final String s[] = ss[0].split(",");
			final MapPanel panel = gui.getMapPanel();
			if (map.containsKey("zoom")) {
				panel.setZoom(Integer.parseInt(map.get("zoom")));
			}
			panel.setLocation(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
		}
	}

	@Override
	public void close() {
		setURI(null);
		getContentPane().remove(gui);
		gui = null;
	}
}
