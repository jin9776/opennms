/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.groovy.poller.remote

import groovy.swing.SwingBuilder

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.font.TextAttribute

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.plaf.BorderUIResource
import javax.swing.plaf.ColorUIResource
import javax.swing.plaf.FontUIResource

import net.miginfocom.swing.MigLayout

import org.opennms.poller.remote.GroovyGui

public abstract class AbstractGui implements GroovyGui {
    private def m_gui
    private def m_defaultButton

    private def m_headerFont
    private def m_labelFont
    private def m_mainFont
    private def m_preferredFonts = ['Helvetica Neue', 'Roboto Sans', 'Roboto', 'Futura']

    private def m_backgroundColor = Color.WHITE
    private def m_foregroundColor = Color.BLACK
    private def m_detailColor = new Color(0x005AAA)
    private def m_listCellRenderer = new CellRenderer(getDetailColor(), getForegroundColor(), getBackgroundColor())

    protected def debugString = ""

    protected def swing = new SwingBuilder()
    protected def repaintDelay = 300

    public AbstractGui() {
        //debugString = ", debug 100"

        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())

        def background = new ColorUIResource(m_backgroundColor)
        def foreground = new ColorUIResource(m_foregroundColor)
        def detail = new ColorUIResource(m_detailColor)
        def buttonBorder = new BorderUIResource.LineBorderUIResource(m_foregroundColor)

        UIManager.put("Button.background", detail)
        UIManager.put("Button.foreground", background)
        //UIManager.put("ComboBox.background", background)
        //UIManager.put("ComboBox.foreground", foreground)
        //UIManager.put("ComboBox.listBackground", background)
        //UIManager.put("ComboBox.listForeground", foreground)
        UIManager.put("ComboBox.selectionBackground", background)
        UIManager.put("ComboBox.selectionForeground", foreground)
        UIManager.put("Label.background", background)
        UIManager.put("Label.foreground", foreground)
        //UIManager.put("textHighlight", detail)
        //UIManager.put("textHighlightText", background)
        UIManager.put("ProgressBar.selectionBackground", Color.TRANSLUCENT)
        UIManager.put("ProgressBar.selectionForeground", foreground)
        //UIManager.put("ProgressBar.foreground", Color.TRANSLUCENT)
        //UIManager.put("ProgressBar.background", foreground)

        swing.registerBeanFactory('migLayout', MigLayout.class)

        def fontNames = new TreeSet<String>()
        def environment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        for (def font : environment.getAvailableFontFamilyNames()) {
            fontNames.add(font)
        }

        for (def fontName : m_preferredFonts) {
            if (fontNames.contains(fontName)) {
                m_mainFont = getFont(fontName)
                if (m_mainFont != null) {
                    break
                }
            }
        }

        if (m_mainFont == null) {
            //m_mainFont = new Font("SansSerif", Font.PLAIN, 12.0)
            def fontAttributes = new HashMap<TextAttribute,Object>()
            fontAttributes.put(TextAttribute.FAMILY, Font.SANS_SERIF)
            fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR)
            fontAttributes.put(TextAttribute.SIZE, 12.0)
            m_mainFont = new Font(fontAttributes)
        }
        m_labelFont = m_mainFont.deriveFont(Font.BOLD)
        m_headerFont = m_mainFont.deriveFont(Font.BOLD, 24.0)
        //println "Header font:  " + m_headerFont
        //println "Main font: " + m_mainFont

        setUIFont(new FontUIResource(m_mainFont))
    }

    public static void setUIFont (final FontUIResource f){
        Enumeration<Object> keys = UIManager.getDefaults().keys()
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement()
            final Object value = UIManager.get (key)
            if (value != null && value instanceof FontUIResource)
                UIManager.put(key, f)
        }
    }

    protected abstract String getHeaderText()

    protected abstract JPanel getMainPanel()

    protected Dimension getWindowSize() {
        return m_gui.getSize()
    }

    protected void repaint() {
        m_gui.repaint(repaintDelay)
    }

    protected void setWindowSize(final Dimension d) {
        m_gui.setSize(d)
    }

    protected JFrame getGui() {
        return m_gui
    }

    protected Color getBackgroundColor() {
        return m_backgroundColor
    }

    protected Color getForegroundColor() {
        return m_foregroundColor
    }

    protected Color getDetailColor() {
        return m_detailColor
    }

    protected Font getMainFont() {
        return m_mainFont
    }

    protected Font getHeaderFont() {
        return m_headerFont
    }

    protected Font getLabelFont() {
        return m_labelFont
    }

    protected ListCellRenderer<Object> getRenderer() {
        return m_listCellRenderer
    }

    protected JButton getDefaultButton() {
        return m_defaultButton
    }

    protected void setDefaultButton(final JButton button) {
        m_defaultButton = button
        if (button.rootPane != null) {
            button.rootPane.defaultButton = button
        }
    }

    @Override
    public final void createAndShowGui() {
        m_gui = swing.frame(title:'Scan', size:[750,550], pack:true, visible:false, background:getBackgroundColor(), defaultCloseOperation:WindowConstants.DISPOSE_ON_CLOSE)
        def rootPanel = swing.panel(background:getBackgroundColor(), opaque:true) {
            migLayout(
                    layoutConstraints:"fill" + debugString,
                    columnConstraints:"[grow, center]",
                    rowConstraints:"[grow 0]u[fill, grow]"
                    )
            panel(opaque:true, background:getDetailColor(), constraints:"dock north, wrap") {
                migLayout(
                        layoutConstraints:"fill" + debugString,
                        columnConstraints:"10[grow]10",
                        rowConstraints:"10[grow]10"
                        )
                label(text:getHeaderText(), font:getHeaderFont(), foreground:getBackgroundColor())
            }
        }
        def mainPanel = getMainPanel()
        rootPanel.add(mainPanel, "dock south, width 100%, height 100%")
        //rootPanel.add(mainPanel)
        m_gui.add(rootPanel)
        m_gui.pack()
        m_gui.setLocationRelativeTo(null)
        if (m_defaultButton) {
            rootPanel.rootPane.defaultButton = m_defaultButton
        }
        m_gui.setVisible(true)
        m_gui.setResizable(false)
        //m_gui.show()
    }

    protected Font getFont(final String fontName) {
        def environment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        for (def font : environment.getAllFonts()) {
            if (font.getFamily().equals(fontName)) {
                return font.deriveFont(Font.PLAIN, 12.0)
            }
        }
        return null
    }


}
