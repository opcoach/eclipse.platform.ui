/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.tests.keys;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.commands.ws.WorkbenchCommandSupport;
import org.eclipse.ui.keys.KeyStroke;
import org.eclipse.ui.keys.ParseException;
import org.eclipse.ui.tests.util.UITestCase;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Tests Bug 43321
 * 
 * @since 3.0
 */
public class Bug43321Test extends UITestCase {

	/**
	 * Constructor for Bug43321Test.
	 * 
	 * @param name
	 *            The name of the test
	 */
	public Bug43321Test(String name) {
		super(name);
	}

	/**
	 * Tests that non-check box items on the menu are not checked when
	 * activated from the keyboard.
	 * 
	 * @throws CoreException
	 *             If the test project cannot be created and opened.
	 * @throws ParseException
	 *             If "CTRL+C" isn't a valid key stroke.
	 */
	public void testNoCheckOnNonCheckbox() throws CoreException, ParseException {
		IWorkbenchWindow window = openTestWindow();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject testProject = workspace.getRoot().getProject("TestProject"); //$NON-NLS-1$
		testProject.create(null);
		testProject.open(null);
		IFile textFile = testProject.getFile("A.txt"); //$NON-NLS-1$
		String contents = "A blurb"; //$NON-NLS-1$
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		textFile.create(inputStream, true, null);
		AbstractTextEditor editor =
			(AbstractTextEditor) IDE.openEditor(window.getActivePage(), textFile, true);
		editor.selectAndReveal(0, 1);

		// Press "Ctrl+C" to perform a copy.
		List keyStrokes = new ArrayList();
		keyStrokes.add(KeyStroke.getInstance("CTRL+C")); //$NON-NLS-1$
		Event event = new Event();
		Workbench workbench = ((Workbench) window.getWorkbench());
		WorkbenchCommandSupport support = (WorkbenchCommandSupport) workbench.getCommandSupport();
		support.getKeyboard().press(keyStrokes, event, false);

		// Get the menu item we've just selected.
		IAction action =
			editor.getEditorSite().getActionBars().getGlobalActionHandler(
				ActionFactory.COPY.getId());
		assertTrue("Non-checkbox menu item is checked.", !action.isChecked()); //$NON-NLS-1$
	}
}
