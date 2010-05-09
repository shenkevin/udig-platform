package net.refractions.udig.feature.editor.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

public class BooleanAttributeField extends AttributeField {

    /**
     * Style constant (value <code>0</code>) indicating the default layout where
     * the field editor's check box appears to the left of the label.
     */
    public static final int DEFAULT = 0;

    /**
     * Style constant (value <code>1</code>) indicating a layout where the field
     * editor's label appears on the left with a check box on the right.
     */
    public static final int SEPARATE_LABEL = 1;

    /**
     * Style bits. Either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>.
     */
    private int style;

    /**
     * The previously selected, or "before", value.
     */
    private boolean wasSelected;

    /**
     * The checkbox control, or <code>null</code> if none.
     */
    private Button checkBox = null;

    /**
     * Creates a new boolean attribute field
     */
    protected BooleanAttributeField() {
    }

    @Override
    public Control getControl() {
        return checkBox;
    }
    
    /**
     * Creates a boolean attribute field in the given style.
     * 
     * @param name
     *            the name of the preference this attribute field works on
     * @param labelText
     *            the label text of the attribute field
     * @param style
     *            the style, either <code>DEFAULT</code> or
     *            <code>SEPARATE_LABEL</code>
     * @param parent
     *            the parent of the attribute field's control
     * @see #DEFAULT
     * @see #SEPARATE_LABEL
     */
    public BooleanAttributeField(String name, String labelText, int style, Composite parent) {
        init(name, labelText);
        this.style = style;
        createControl(parent);
    }

    /**
     * Creates a boolean attribute field in the default style.
     * 
     * @param name
     *            the name of the preference this attribute field works on
     * @param label
     *            the label text of the attribute field
     * @param parent
     *            the parent of the attribute field's control
     */
    public BooleanAttributeField(String name, String label, Composite parent) {
        this(name, label, DEFAULT, parent);
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    public void adjustForNumColumns(int numColumns) {
        if (style == SEPARATE_LABEL) {
            numColumns--;
        }
        ((GridData) checkBox.getLayoutData()).horizontalSpan = numColumns;
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        String text = getLabelText();
        switch (style) {
        case SEPARATE_LABEL:
            getLabelControl(parent);
            numColumns--;
            text = null;
            //$FALL-THROUGH$
        default:
            checkBox = getChangeControl(parent);
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns;
            checkBox.setLayoutData(gd);
            if (text != null) {
                checkBox.setText(text);
            }
        }
    }

    /**
     * Returns the control responsible for displaying this attribute field's label.
     * This method can be used to set a tooltip for a
     * <code>BooleanAttributeField</code>. Note that the normal pattern of
     * <code>getLabelControl(parent).setToolTipText(tooltipText)</code> does not
     * work for boolean attribute fields, as it can lead to duplicate text (see bug
     * 259952).
     * 
     * @param parent
     *            the parent composite
     * @return the control responsible for displaying the label
     * 
     * @since 3.5
     */
    public Control getDescriptionControl(Composite parent) {
        if (style == SEPARATE_LABEL) {
            return getLabelControl(parent);
        }
        return getChangeControl(parent);
    }

    /*
     * (non-Javadoc) Method declared on AttributeField. Loads the value from the
     * feature type schema and sets it to the check box.
     */
    public void doLoad() {
        if (checkBox != null) {
            Object value = getFeature().getAttribute( getAttributeName() );            
            Boolean check = Converters.convert(value, Boolean.class );
            checkBox.setSelection(check);
            wasSelected = check;
        }
    }

    /*
     * (non-Javadoc) Method declared on AttributeField. Loads the default value
     * from the feature type schema and sets it to the check box.
     */
    protected void doLoadDefault() {
        if (checkBox != null) {          
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());            
            Object value = descriptor.getDefaultValue();          
            Boolean check = Converters.convert(value, Boolean.class );
            checkBox.setSelection(check);
            wasSelected = check;
        }
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    protected void doStore() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());  
        Object value = Converters.convert( checkBox, descriptor.getType().getBinding() );        
        getFeature().setAttribute( getAttributeName(), value );
    }

    /**
     * Returns this attribute field's current value.
     * 
     * @return the value
     */
    public boolean getBooleanValue() {
        return checkBox.getSelection();
    }

    /**
     * Returns the change button for this attribute field.
     * 
     * @param parent
     *            The Composite to create the receiver in.
     * 
     * @return the change button
     */
    protected Button getChangeControl(Composite parent) {
        if (checkBox == null) {
            checkBox = new Button(parent, SWT.CHECK | SWT.LEFT);
            checkBox.setFont(parent.getFont());
            checkBox.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    boolean isSelected = checkBox.getSelection();
                    valueChanged(wasSelected, isSelected);
                    wasSelected = isSelected;
                }
            });
            checkBox.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    checkBox = null;
                }
            });
        } else {
            checkParent(checkBox, parent);
        }
        return checkBox;
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    public int getNumberOfControls() {
        switch (style) {
        case SEPARATE_LABEL:
            return 2;
        default:
            return 1;
        }
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    public void setFocus() {
        if (checkBox != null) {
            checkBox.setFocus();
        }
    }
    
    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    public void setLabelText(String text) {
        super.setLabelText(text);
        Label label = getLabelControl();
        if (label == null && checkBox != null) {
            checkBox.setText(text);
        }
    }

    /**
     * Informs this attribute field's listener, if it has one, about a change to
     * the value (<code>VALUE</code> property) provided that the old and new
     * values are different.
     * 
     * @param oldValue
     *            the old value
     * @param newValue
     *            the new value
     */
    protected void valueChanged(boolean oldValue, boolean newValue) {
        setPresentsDefaultValue(false);
        if (oldValue != newValue) {
            fireStateChanged(VALUE, oldValue, newValue);
        }
    }

    @Override
    public void setVisible( boolean visible ) {
        // Only call super if there is a label already
        if (style == SEPARATE_LABEL) {
            super.setVisible(visible);
        }
        if( checkBox != null && !checkBox.isDisposed()){
            checkBox.setVisible(visible);
        }
    }
    /*
     * @see AttributeField.setEnabled
     */
    public void setEnabled(boolean enabled) {
        // Only call super if there is a label already
        if (style == SEPARATE_LABEL) {
            super.setEnabled(enabled);
        }
        if( checkBox != null && !checkBox.isDisposed()){
            checkBox.setEnabled(enabled);
        }
    }

}