package com.roots.map;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import linoleum.application.Frame;

public class MapFrame extends Frame {
	private final OptionPanel options = new OptionPanel();
	private final PreferenceChangeListener listener = new PreferenceChangeListener() {
		@Override
		public void preferenceChange(final PreferenceChangeEvent evt) {
			if (evt.getKey().equals(getKey("tileServer"))) {
				update();
			}
		}
	};
	private final Preferences prefs = Preferences.userNodeForPackage(getClass());
	private final Properties properties = new Properties();
	private MapPanel.Gui gui;

	public MapFrame() {
		setName("Maps");
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setSize(800, 600);
		setTitle("Maps");
		try {
			properties.load(getClass().getResourceAsStream("MapPanel.properties"));
		} catch (final IOException  e) {
			e.printStackTrace();
		}
		setFrameIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/WebComponent16.gif")));
		setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/WebComponent24.gif")));
		setOptionPanel(options);
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
			prefs.addPreferenceChangeListener(listener);
			getContentPane().add(gui, BorderLayout.CENTER);
			setJMenuBar(gui.createMenuBar());
			update();
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
		prefs.removePreferenceChangeListener(listener);
		gui = null;
	}

	@Override
	public void load() {
		options.getTextField().setText(getTileServer());
	}

	private void update() {
		gui.getMapPanel().setTileServer(getTileServer());
	}

	private String getTileServer() {
		return prefs.get(getKey("tileServer"), properties.getProperty("tileServer"));
	}

	@Override
	public void save() {
		prefs.put(getKey("tileServer"), options.getTextField().getText());
	}
}
