/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsyntaxpane.actions.gui;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import jsyntaxpane.actions.ActionUtils;
import jsyntaxpane.util.ReflectUtils;
import jsyntaxpane.util.StringUtils;
import jsyntaxpane.util.SwingUtils;

/**
 *
 * @author Ayman Al-Sairafi
 */
public class ReflectCompletionDialog
	extends javax.swing.JDialog implements EscapeListener {

	/**
	 * The class we are displaying its members:
	 */
	private Class theClass;
	/**
	 * The current filter, to avoid refiltering the items
	 */
	public String escapeChars = ";(= \t\n";
	public List<Member> items;
	private final JTextComponent target;

	/**
	 * Creates new form ReflectCompletionDialog
	 * @param target Text component for this dialog
	 */
	public ReflectCompletionDialog(JTextComponent target) {
		super(SwingUtilities.getWindowAncestor(target), ModalityType.APPLICATION_MODAL);
		initComponents();
		this.target = target;
		jTxtItem.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				refilterList();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refilterList();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				refilterList();
			}
		});
		// This will allow the textfield to receive TAB keys
		jTxtItem.setFocusTraversalKeysEnabled(false);
		// Add action so we automatically filter on comboBox Enter Key
		jCmbClassName.getEditor().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateItems();
			}
		});
		SwingUtils.addEscapeListener(this);
	}

	public void setFonts(Font font) {
		jTxtItem.setFont(font);
		jLstItems.setFont(font);
		doLayout();
	}

	private String getSelection() {
		String result;
		if (jLstItems.getSelectedIndex() >= 0) {
			Object selected = jLstItems.getSelectedValue();
			if (selected instanceof Method) {
				result = ReflectUtils.getJavaCallString((Method) selected);
			} else if (selected instanceof Constructor) {
				result = ReflectUtils.getJavaCallString((Constructor) selected);
			} else if (selected instanceof Field) {
				result = ((Field) selected).getName();
			} else {
				result = selected.toString();
			}
		} else {
			result = jTxtItem.getText();
		}
		return result;
	}

	private void refilterList() {
		String prefix = jTxtItem.getText();
		Vector<Member> filtered = new Vector<Member>();
		Object selected = jLstItems.getSelectedValue();
		for (Member m : items) {
			if (StringUtils.camelCaseMatch(m.getName(), prefix)) {
				filtered.add(m);
			}
		}
		jLstItems.setListData(filtered);
		if (selected != null && filtered.contains(selected)) {
			jLstItems.setSelectedValue(selected, true);
		} else {
			jLstItems.setSelectedIndex(0);
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtItem = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLstItems = new javax.swing.JList();
        jCmbClassName = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("CompletionDialog"); // NOI18N
        setResizable(false);
        setUndecorated(true);

        jTxtItem.setBorder(null);
        jTxtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTxtItemKeyPressed(evt);
            }
        });

        jLstItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLstItems.setCellRenderer(new jsyntaxpane.actions.gui.MembersListRenderer(this));
        jLstItems.setFocusable(false);
        jLstItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLstItemsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jLstItems);

        jCmbClassName.setEditable(true);
        jCmbClassName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Object", "String" }));
        jCmbClassName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCmbClassNameItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTxtItem, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addComponent(jCmbClassName, 0, 437, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jCmbClassName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtItemKeyPressed

		int i = jLstItems.getSelectedIndex();
		switch (evt.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				target.setCaretPosition(target.getSelectionEnd());
				setVisible(false);
				return;
			case KeyEvent.VK_DOWN:
				i++;
				break;
			case KeyEvent.VK_UP:
				i--;
				break;
			case KeyEvent.VK_HOME:
				i = 0;
				break;
			case KeyEvent.VK_END:
				i = jLstItems.getModel().getSize() - 1;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				i += jLstItems.getVisibleRowCount();
				break;
			case KeyEvent.VK_PAGE_UP:
				i -= jLstItems.getVisibleRowCount();
				break;
		}

		if (escapeChars.indexOf(evt.getKeyChar()) >= 0) {
			String result = getSelection();
			char pressed = evt.getKeyChar();
			// we need to just accept ENTER, and replace the tab with a single
			// space
			if (pressed != '\n') {
				result += (pressed == '\t') ? ' ' : pressed;
			}
			target.replaceSelection(result);
			setVisible(false);
		} else {
			// perform bounds checks for i
			if (i >= jLstItems.getModel().getSize()) {
				i = jLstItems.getModel().getSize() - 1;
			}
			if (i < 0) {
				i = 0;
			}
			jLstItems.setSelectedIndex(i);
			jLstItems.ensureIndexIsVisible(i);
		}
    }//GEN-LAST:event_jTxtItemKeyPressed

    private void jCmbClassNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCmbClassNameItemStateChanged
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			updateItems();
		}
    }//GEN-LAST:event_jCmbClassNameItemStateChanged

	private void jLstItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLstItemsMouseClicked
		if (evt.getClickCount() == 2) {
			String selected = getSelection();
			target.replaceSelection(selected);
			setVisible(false);
		}
	}//GEN-LAST:event_jLstItemsMouseClicked

	private void updateItems() {
		String className = jCmbClassName.getEditor().getItem().toString();
		if (items == null) {
			items = new ArrayList<Member>();
		} else {
			items.clear();
		}
		// we must have the class in the Combo:
		Class aClass = ReflectUtils.findClass(className, ReflectUtils.DEFAULT_PACKAGES);
		if (aClass != null) {
			// for now, add everything:
			theClass = aClass;
			ReflectUtils.addConstructors(aClass, items);
			ReflectUtils.addMethods(aClass, items);
			ReflectUtils.addFields(aClass, items);
			ActionUtils.insertIntoCombo(jCmbClassName, className);
			jTxtItem.requestFocusInWindow();
		}
		refilterList();
	}

	public Class getTheClass() {
		return theClass;
	}

	/**
	 * Set the items to display
	 * @param items
	 */
	public void setItems(List<Member> items) {
		this.items = items;
	}

	/**
	 * Display the dialog.
	 * @param target text component (its Window will be the parent)
	 */
	public void displayFor(JTextComponent target) {
		try {
			int dot = target.getSelectionStart();
			Window window = SwingUtilities.getWindowAncestor(target);
			Rectangle rt = target.modelToView(dot);
			Point loc = new Point(rt.x, rt.y);
			// convert the location from Text Component coordinates to
			// Frame coordinates...
			loc = SwingUtilities.convertPoint(target, loc, window);
			// and then to Screen coordinates
			SwingUtilities.convertPointToScreen(loc, window);
			setLocationRelativeTo(window);
			setLocation(loc);
		} catch (BadLocationException ex) {
			Logger.getLogger(ReflectCompletionDialog.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			setFonts(target.getFont());
			updateItems();
			jTxtItem.setText(target.getSelectedText());
			setVisible(true);
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jCmbClassName;
    private javax.swing.JList jLstItems;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTxtItem;
    // End of variables declaration//GEN-END:variables

	@Override
	public void escapePressed() {
		target.setCaretPosition(target.getSelectionEnd());
		setVisible(false);
	}
}
