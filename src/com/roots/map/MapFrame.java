package com.roots.map;

import java.awt.BorderLayout;
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
	}

	@Override
	public void open() {
		gui = new MapPanel.Gui();
		getContentPane().add(gui, BorderLayout.CENTER);
		setJMenuBar(gui.createMenuBar());
	}

	@Override
	public void close() {
		setJMenuBar(null);
		getContentPane().remove(gui);
		gui = null;
	}
}
