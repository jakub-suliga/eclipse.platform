/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial implementation
 ******************************************************************************/
package org.eclipse.team.internal.ccvs.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * This class acts as an abstract superclass for CVS preference pages that use
 * field editors.
 */
public abstract class CVSFieldEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static IPreferenceStore getCVSPreferenceStore() {
		return CVSUIPlugin.getPlugin().getPreferenceStore();
	}
	
	/**
	 * Constructor for CVSFieldEditorPreferencePage.
	 */
	public CVSFieldEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(getCVSPreferenceStore());
		String description = getPageDescription();
		if (description != null)
			setDescription(description);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		String id = getPageHelpContextId();
		if (id != null)
			WorkbenchHelp.setHelp(control, id);
		Dialog.applyDialogFont(control);
		return control;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/**
	 * Method getPageHelpContextId must be overridden by subclasses to provide
	 * the help context ID of the page. Return null for no page F1 help.
	 * 
	 * @return String
	 */
	protected abstract String getPageHelpContextId();
	
	/**
	 * Method getPageDescription must be overridden by subclasses to provide the
	 * description of the page. Return null for no description.
	 * @return String
	 */
	protected abstract String getPageDescription();
	
}
