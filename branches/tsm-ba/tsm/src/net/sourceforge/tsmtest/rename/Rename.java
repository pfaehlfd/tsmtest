package net.sourceforge.tsmtest.rename;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;

public class Rename extends
	org.eclipse.ltk.core.refactoring.participants.RenameParticipant {

    private String resourceName = "";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     * checkConditions(org.eclipse.core.runtime.IProgressMonitor,
     * org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
     */
    @Override
    public RefactoringStatus checkConditions(IProgressMonitor arg0,
	    CheckConditionsContext arg1) throws OperationCanceledException {

	ResourceChangeChecker checker = (ResourceChangeChecker) arg1
		.getChecker(ResourceChangeChecker.class);
	IResourceChangeDescriptionFactory deltaFactory = checker
		.getDeltaFactory();
	IResourceDelta[] affectedChildren = deltaFactory.getDelta()
		.getAffectedChildren();

	return verifyAffectedChildren(affectedChildren);

    }

    /**
     * @param affectedChildren
     *            The children that are probably affected by a refactoring
     * @return The status of the refactoring
     */
    private RefactoringStatus verifyAffectedChildren(
	    IResourceDelta[] affectedChildren) {
	for (IResourceDelta resourceDelta : affectedChildren) {
	    if (resourceDelta.getMovedFromPath() != null) {
		if (resourceDelta.getResource() instanceof TSMResource) {
		    String name = resourceDelta.getResource().getName();
		    boolean character = true;
		    boolean length = true;
		    boolean end = true;
		    if (resourceDelta.getResource() instanceof TSMTestCase
			    && !name.endsWith(".xml")) { //$NON-NLS-1$
			end = false;
		    }

		    if (name == null || name.isEmpty()) {
			character = false;
		    } else if (name.length() > DataModelTypes.NAME_MAX_LENGTH) {
			length = false;
		    } else if (name.contains("<")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains(">")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("?")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("\"")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains(":")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("|")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("\\")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("/")) { //$NON-NLS-1$
			character = false;
		    } else if (name.contains("*")) { //$NON-NLS-1$
			character = false;
		    }

		    if (!character) {
			return RefactoringStatus
				.createFatalErrorStatus(Messages.rename_1);
		    } else if (!length) {
			return RefactoringStatus
				.createFatalErrorStatus(Messages.rename_2);
		    } else if (!end) {
			return RefactoringStatus
				.createFatalErrorStatus(Messages.rename_3);
		    }
		} else if (resourceDelta.getMovedToPath() == null) {
		    return verifyAffectedChildren(resourceDelta
			    .getAffectedChildren());
		}
	    }
	}

	return new RefactoringStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     * createChange(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public Change createChange(IProgressMonitor arg0) throws CoreException,
	    OperationCanceledException {

	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     * getName()
     */
    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     * initialize(java.lang.Object)
     */
    @Override
    protected boolean initialize(Object arg0) {
	//System.out.println(arg0.getClass().getName());
	if (arg0 instanceof IResource) {
	    return true;
	}
	return false;
    }

}
