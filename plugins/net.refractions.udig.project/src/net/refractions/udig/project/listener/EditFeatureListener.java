package net.refractions.udig.project.listener;

import java.beans.PropertyChangeEvent;

import net.refractions.udig.project.EditFeature.AttributeStatus;

import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * EditFeature event notification, provides feedback during attribute value editing with additional
 * events covering isDirty, isVisible, isEnabled, isEditable model changes.
 * <p>
 * This is similar to {@link IPropertyChangeListener}.
 * 
 * @author Levi Putna
 * @since 1.3.0
 */
public interface EditFeatureListener {

    /**
     * Called before an attribute value changes, returning false will cause the edit event to be
     * canceled. It is the responsibility of the implementing method to inform the EditManager as to
     * why the edit was canceled. {@link EditManager.addErrorMessage()}
     * 
     * @param event
     */
    public void attributeValueBeforeChange(PropertyChangeEvent event);

    /**
     * Triggered whenever an attribute value changes on the feature.
     * 
     * <p>
     * Note that this method is not guaranteed to execute in the UI thread, so UI work must be
     * performed with Display.getDefault().asyncExec();
     * </p>
     * 
     * @param the change event that accrued
     */
    public void attributeValueChange(PropertyChangeEvent event);

    /**
     * Notifies this listener that the status of one of the features on this attribute have changed.
     * <p>
     * The supplied event gives details. This event object (and the resource delta within it) is
     * valid only for the duration of the invocation of this method.
     * </p>
     * <p>
     * Note that this method is not guaranteed to execute in the UI thread, so UI work must be
     * performed with Display.getDefault().asyncExec();
     * </p>
     * 
     * @param event the resource change event
     * @see IResourceDelta
     */
    public void attributeStateChange(EditFeatureStateChangeEvent stateChangeEvent);

}