package com.roots.map;

import java.awt.BorderLayout;
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
			gui.getMapPanel().setLocation(Double.parseDouble(s[1]), Double.parseDouble(s[0]), 8);
		}
	}

	@Override
	public void close() {
		setURI(null);
		getContentPane().remove(gui);
		gui = null;
	}
}
