/*
 * Created on 14.07.2005
 *
 */
package org.jdesktop.swingx.decorator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Responsible for keeping track of selection in model coordinates.<p>
 * 
 * updates view selection on pipeline change.
 * updates model selection on view selection change.
 * 
 * @author Jeanette Winzenburg
 */
public class Selection {
    // protected int[] selected; // used ONLY within save/restoreSelection();
    // protected int lead = -1;
    private ListSelectionModel viewSelection;

    private DefaultListSelectionModel modelSelection;

    private FilterPipeline pipeline;

    private ListSelectionListener viewSelectionListener;

    private PipelineListener pipelineListener;

    private boolean isListening;

//    private int lead;

    /**
     * PRE: selection != null;
     * 
     * @param pipeline
     * @param selection
     */
    public Selection(FilterPipeline pipeline, ListSelectionModel selection) {
        modelSelection = new DefaultListSelectionModel();
        setViewSelectionModel(selection);
        setFilters(pipeline);
//        mapTowardsModel();
    }

    public void setViewSelectionModel(ListSelectionModel selection) {
        ListSelectionModel old = this.viewSelection;
        if (old != null) {
            old.removeListSelectionListener(viewSelectionListener);
            modelSelection.clearSelection();
        }
        this.viewSelection = selection;
        unlock();
        mapTowardsModel();
    }



    public void setFilters(FilterPipeline pipeline) {
        FilterPipeline old = this.pipeline;
        if (old != null) {
            old.removePipelineListener(pipelineListener);
        }
        this.pipeline = pipeline;
        if (pipeline != null) {
            pipeline.addPipelineListener(getPipelineListener());
        }
        restoreSelection();
    }




    public int[] getSelectedRows(ListSelectionModel selection) {
        int iMin = selection.getMinSelectionIndex();
        int iMax = selection.getMaxSelectionIndex();

        if ((iMin == -1) || (iMax == -1)) {
            return new int[0];
        }

        int[] rvTmp = new int[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (selection.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    public void restoreSelection() {
        lock();
        viewSelection.setValueIsAdjusting(true);
        viewSelection.clearSelection();

        // RG: calculate rowCount once, not inside a loop
        final int rowCount = pipeline != null ? pipeline.getInputSize() : Integer.MAX_VALUE;
        int[] selected = getSelectedRows(modelSelection);
        int lead = modelSelection.getLeadSelectionIndex();
        for (int i = 0; i < selected.length; i++) {
            // JW: make sure we convert valid row indices (in model coordinates)
            // only fix #16
            // PENDING: check if selected >= rowCount can really happen! 
            // modelSelection should be cleared on update
            if ((selected[i] != lead) && (selected[i] < rowCount)) {
                int index = convertToView(selected[i]);
                viewSelection.addSelectionInterval(index, index);
            }
        }

        // JW: make sure we convert valid row indices (in model coordinates)
        // only fix #16
        // PENDING: check if selected >= rowCount can really happen! 
        // modelSelection should be cleared on update
        if ((lead >= 0) && (lead < rowCount)) {
            lead = convertToView(lead);
            if (viewSelection instanceof DefaultListSelectionModel) {
                // #223 - part d)
                ((DefaultListSelectionModel) viewSelection).moveLeadSelectionIndex(lead);
            } else {
                viewSelection.addSelectionInterval(lead, lead);
            }
        }
        viewSelection.setValueIsAdjusting(false);
        unlock();
    }

    public void unlock() {
        if (!isListening) {
            viewSelection.addListSelectionListener(getViewSelectionListener());
            isListening = true;
        }
    }

    public void lock() {
        if (viewSelectionListener != null) {
            viewSelection.removeListSelectionListener(viewSelectionListener);
            isListening = false;
        }
    }

    public void clearModelSelection() {
        modelSelection.clearSelection();
        
    }
    
    private void mapTowardsModel() {
        // if (!pipeline.isAssigned()) return;
        modelSelection.clearSelection();
        int[] selected = getSelectedRows(viewSelection); // in view
                                                            // coordinates
        for (int i = 0; i < selected.length; i++) {
            int modelIndex = convertToModel(selected[i]);
            modelSelection.addSelectionInterval(modelIndex, modelIndex); // model
                                                                            // coordinates
        }
//        lead = selected.length > 0 ? convertToModel(viewSelection.getLeadSelectionIndex()) : -1;
        if (selected.length > 0) {
            // convert lead selection index to model coordinates
            modelSelection.moveLeadSelectionIndex(convertToModel(viewSelection.getLeadSelectionIndex()));
        }
    }

    private int convertToModel(int index) {
        return pipeline != null ? pipeline.convertRowIndexToModel(index) : index;
    }
    
    private int convertToView(int index) {
        return pipeline != null ? pipeline.convertRowIndexToView(index) : index;
    }
    
    protected void updateFromViewSelectionChanged(int firstIndex, int lastIndex) {
        for (int i = firstIndex; i <= lastIndex; i++) {
            int modelIndex = convertToModel(i);
            if (viewSelection.isSelectedIndex(i)) {
                modelSelection.addSelectionInterval(modelIndex, modelIndex);
            } else {
                modelSelection.removeSelectionInterval(modelIndex, modelIndex);
            }
        }
        
    }

    protected void updateFromPipelineChanged() {
        restoreSelection();
    }

    private PipelineListener getPipelineListener() {
        if (pipelineListener == null) {
            pipelineListener = new PipelineListener() {

                public void contentsChanged(PipelineEvent e) {
                    updateFromPipelineChanged();
                    
                }
                
            };
        }
        return pipelineListener;
    }

    private ListSelectionListener getViewSelectionListener() {
        if (viewSelectionListener == null) {
            viewSelectionListener = new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    updateFromViewSelectionChanged(e.getFirstIndex(), e.getLastIndex());
                    
                }
                
            };
        }
        return viewSelectionListener;
    }

    public void insertIndexInterval(int start, int length, boolean before) {
        modelSelection.insertIndexInterval(start, length, before);
        
    }

    public void removeIndexInterval(int start, int end) {
        modelSelection.removeIndexInterval(start, end);
        
    }


}
