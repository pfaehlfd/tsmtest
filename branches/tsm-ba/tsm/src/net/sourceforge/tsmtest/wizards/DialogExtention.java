/***********************************************************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved. This program and the accompanying
 * materials! are made available under the terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Sebastian Davids <sdavids@gmx.de>- Fix for bug 19346 -
 * Dialog font should be activated and used by other components.
 *    Verena Käfer - enhancements
 **********************************************************************************************************************/
package net.sourceforge.tsmtest.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

/**
 * @author Sebastian Davids
 * @author Verena Käfer
 *
 */
public final class DialogExtention extends ResourceListSelectionDialog {

    private String initialPattern;

    //private IResourceSelector selector;

    /**
     * @param shell
     *            container to get resources from
     * @param resources
     *            resources to display in the dialog
     */
    public DialogExtention(final Shell shell, final IResource[] resources) {
	super(shell, resources);
    }

    /**
     * @param initialPattern The pattern that is initially displayed
     */
    public void setInitialPattern(final String initialPattern) {
	this.initialPattern = initialPattern;
    }

    /**
     * @param selector Is needed in further method
     */
    /*public void setSelector(final IResourceSelector selector) {
	this.selector = selector;
    }*/

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ResourceListSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
	final Control area = super.createDialogArea(parent);

	// HACKY: use reflection to set the initial value. But still better than
	// to copy the whole implementation...
	if (initialPattern != null) {
	    try {
		final java.lang.reflect.Field field = ResourceListSelectionDialog.class
			.getDeclaredField("pattern"); //$NON-NLS-1$
		field.setAccessible(true);
		final Text pattern = (Text) field.get(this);
		pattern.setText(initialPattern);
	    } catch (final SecurityException e) {
		e.printStackTrace();
	    } catch (final NoSuchFieldException e) {
		e.printStackTrace();
	    } catch (final IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (final IllegalAccessException e) {
		e.printStackTrace();
	    }
	}

	return area;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ResourceListSelectionDialog#select(org.eclipse.core.resources.IResource)
     */
    /*@Override
    protected boolean select(final IResource resource) {
	if (selector == null)
	    return super.select(resource);

	return (this.select(resource));
    }*/
}