/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.update.internal.ui;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.update.ui.*;

/**
 * Insert the type's description here.
 * @see IWorkbenchWindowActionDelegate
 */
public class ConfigurationManagerAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	ApplicationWindow applicationWindow;
	
	/**
	 * The constructor.
	 */
	public ConfigurationManagerAction() {
	}

	/**
	 * Runs the action when selected
	 */
	public void run(IAction action) {
		BusyIndicator
			.showWhile(window.getShell().getDisplay(), new Runnable() {
			public void run() {
				openConfigurationManager();
			}
		});
	}

	private void openConfigurationManager() {
		if (applicationWindow!=null) {
			restoreConfigurationWindow();
			return;
		}
		ApplicationWindow cwindow = UpdateManagerUI.createConfigurationManagerWindow(window.getShell());
		cwindow.create();
		cwindow.getShell().setSize(800, 600);
		cwindow.getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				applicationWindow = null;
			}
		});
		applicationWindow = cwindow;
		cwindow.open();
	}
	
	private void restoreConfigurationWindow() {
		Shell shell = applicationWindow.getShell();
		if (shell.getMinimized())
			shell.setMinimized(false);
		shell.open();
	}

	/**
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}