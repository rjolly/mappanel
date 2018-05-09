package com.roots.map;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import linoleum.application.PreferenceSupport;

public class MapFrame extends PreferenceSupport implements Runnable {
	private final OptionPanel options = new OptionPanel();
	private final PropertyChangeListener locationListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("mapPosition") || evt.getPropertyName().equals("zoom")) {
				updateLocation();
			}
		}
	};
	private final Preferences prefs = Preferences.userNodeForPackage(getClass());
	private final Properties properties = new Properties();
	private MapPanel.Gui gui;

	@Override
	public void preferenceChange(final PreferenceChangeEvent evt) {
		if (evt.getKey().equals(getKey("tileServer"))) {
			update();
		}
	}

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
		setURI(getHome());
	}

	private void setURI(final String str) {
		if (!str.isEmpty()) try {
			setURI(new URI(str));
		} catch (final URISyntaxException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void open() {
		if (gui == null) {
			gui = new MapPanel.Gui();
			gui.getMapPanel().addPropertyChangeListener(locationListener);
			update();
			prefs.addPreferenceChangeListener(this);
			getContentPane().add(gui, BorderLayout.CENTER);
			gui.getMapPanel().getOverlayPanel().setVisible(showInfoPanel());
			gui.getMapPanel().getControlPanel().setVisible(showControlPanel());
			gui.getMapPanel().getSearchPanel().setVisible(showSearchPanel());
			setJMenuBar(gui.createMenuBar());
		}
		SwingUtilities.invokeLater(this);
	}

	public void run() {
		final URI uri = getURI();
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
			} else if (panel.getZoom() == 0) {
				panel.setZoom(8);
			}
			panel.setLocation(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
		}
	}

	private void updateLocation() {
		try {
			final MapPanel panel = gui.getMapPanel();
			final double[] location = panel.getLocationAsDouble();
			String str = String.format(Locale.ROOT, "%f,%f", location[1], location[0]);
			final int zoom = panel.getZoom();
			if (zoom != 8) {
				str += ";zoom=" + zoom;
			}
			setURI(new URI("geo", str, null));
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		setURI(getHome());
		getContentPane().remove(gui);
		prefs.removePreferenceChangeListener(this);
		gui.getMapPanel().removePropertyChangeListener(locationListener);
		gui = null;
	}

	@Override
	public void load() {
		options.getTextField1().setText(getTileServer());
		options.getTextField2().setText(getHome());
		options.getCheckBox1().setSelected(showInfoPanel());
		options.getCheckBox2().setSelected(showControlPanel());
		options.getCheckBox3().setSelected(showSearchPanel());
	}

	private void update() {
		gui.getMapPanel().setTileServer(getTileServer());
	}

	private String getTileServer() {
		return getPref("tileServer");
	}

	private String getHome() {
		return getPref("home");
	}

	private boolean showInfoPanel() {
		return getBooleanPref("showInfoPanel");
	}

	private boolean showControlPanel() {
		return getBooleanPref("showControlPanel");
	}

	private boolean showSearchPanel() {
		return getBooleanPref("showSearchPanel");
	}

	@Override
	public void save() {
		putPref("tileServer", options.getTextField1().getText());
		putPref("home", options.getTextField2().getText());
		putBooleanPref("showInfoPanel", options.getCheckBox1().isSelected());
		putBooleanPref("showControlPanel", options.getCheckBox2().isSelected());
		putBooleanPref("showSearchPanel", options.getCheckBox3().isSelected());
	}
}
