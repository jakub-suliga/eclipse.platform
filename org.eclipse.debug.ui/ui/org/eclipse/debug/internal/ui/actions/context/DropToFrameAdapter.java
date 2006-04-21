/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.debug.internal.ui.actions.context;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDropToFrame;
import org.eclipse.debug.internal.ui.actions.provisional.IAsynchronousDropToFrameAdapter;
import org.eclipse.debug.internal.ui.actions.provisional.IBooleanRequestMonitor;
import org.eclipse.debug.internal.ui.viewers.provisional.IAsynchronousRequestMonitor;
import org.eclipse.debug.ui.IDebugUIConstants;

public class DropToFrameAdapter implements IAsynchronousDropToFrameAdapter {

	    public void canDropToFrame(final Object element, final IBooleanRequestMonitor monitor) {
	        Job job = new Job("canDropToFrame") { //$NON-NLS-1$
	            protected IStatus run(IProgressMonitor pm) {
	                if (!pm.isCanceled()) {
	                    IDropToFrame dropToFrame = getTarget(element);
	                    if (dropToFrame != null) {
	                    	monitor.setResult(dropToFrame.canDropToFrame());
	                    } else {
	                    	monitor.setResult(false);
	                    }
	                    monitor.done();
	                }
	                return Status.OK_STATUS;
	            }
	        };
	        job.setSystem(true);
	        job.schedule();
	    }

    public void dropToFrame(final Object element, final IAsynchronousRequestMonitor monitor) {
        Job job = new Job("dropToFrame") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor pm) {
                if (!pm.isCanceled()) {
                    IDropToFrame dropToFrame = getTarget(element);
                    if (dropToFrame != null) {
	                    try {
	                        dropToFrame.dropToFrame();
	                    } catch (DebugException e) {
	                        monitor.setStatus(e.getStatus());
	                    }
                    }
                    else
                    {
                    	monitor.setStatus(new Status(IStatus.ERROR, IDebugUIConstants.PLUGIN_ID,
                    			IDebugUIConstants.INTERNAL_ERROR,
                    			"element must be an instance of or adapt to IDropToFrame", //$NON-NLS-1$
                    			null));
                    }
                    monitor.done();
                }
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
    }
    
    private IDropToFrame getTarget(Object element) {
        if (element instanceof IDropToFrame) {
			return (IDropToFrame) element;
		} else if (element instanceof IAdaptable) {
			return (IDropToFrame) ((IAdaptable)element).getAdapter(IDropToFrame.class);
		}
        return null;
    }

}
