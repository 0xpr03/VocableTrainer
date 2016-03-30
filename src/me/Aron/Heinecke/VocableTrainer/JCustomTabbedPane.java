/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class JCustomTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = -6710447615006126978L;
	private static boolean showTabsHeader = false;

	public JCustomTabbedPane() {
		setUI(new MyTabbedPaneUI());
	}

	private class MyTabbedPaneUI extends BasicTabbedPaneUI {

		@Override
		protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
			if (showTabsHeader) {
				return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
			} else {
				return 0;
			}
		}

		@Override
		protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect) {
			if (showTabsHeader) {
				super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
			}
		}

		@Override
		protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
			if (showTabsHeader) {
				super.paintContentBorder(g, tabPlacement, selectedIndex);
			}
		}

		@Override
		public int tabForCoordinate(JTabbedPane pane, int x, int y) {
			if (showTabsHeader) {
				return super.tabForCoordinate(pane, x, y);
			} else {
				return -1;
			}
		}
	}
}
