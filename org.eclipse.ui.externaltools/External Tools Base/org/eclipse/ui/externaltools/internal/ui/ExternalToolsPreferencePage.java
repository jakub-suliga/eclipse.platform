package org.eclipse.ui.externaltools.internal.ui;

/**********************************************************************
Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
This file is made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
**********************************************************************/

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;
import org.eclipse.ui.externaltools.internal.model.IPreferenceConstants;

/**
 * Preference page that allows the user to customize external tools
 */
public class ExternalToolsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button promptForMigrationButton;
	
	public ExternalToolsPreferencePage() {
		setDescription(ExternalToolsUIMessages.getString("ExternalToolsPreferencePage.Preferences_for_external_tools__1")); //$NON-NLS-1$
		setPreferenceStore(ExternalToolsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Font font = parent.getFont();
		
		//The main composite
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight=0;
		layout.marginWidth=0;
		composite.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		
		Group group= new Group(composite, SWT.NONE);
		group.setText(ExternalToolsUIMessages.getString("ExternalToolsPreferencePage.Project_Builders_2")); //$NON-NLS-1$
		data= new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		layout= new GridLayout();
		group.setLayout(layout);
		group.setFont(font);
		
		promptForMigrationButton= new Button(group, SWT.CHECK | SWT.LEFT);
		promptForMigrationButton.setFont(font);
		promptForMigrationButton.setText(ExternalToolsUIMessages.getString("ExternalToolsPreferencePage.Always_&prompt_before_migrating_project_builders_3")); //$NON-NLS-1$
		promptForMigrationButton.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.PROMPT_FOR_MIGRATION));
		
		return composite;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		getPreferenceStore().setValue(IPreferenceConstants.PROMPT_FOR_MIGRATION, promptForMigrationButton.getSelection());
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		promptForMigrationButton.setSelection(getPreferenceStore().getDefaultBoolean(IPreferenceConstants.PROMPT_FOR_MIGRATION));
		super.performDefaults();
	}

}
