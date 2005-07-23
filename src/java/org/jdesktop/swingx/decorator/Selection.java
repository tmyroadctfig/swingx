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

    /** selection in view coordinates. */
    private ListSelectionModel viewSelection;
    
    /** selection in model coordinates. */
    private DefaultListSelectionModel modelSelection;

    /** mapping pipeline. */
    private FilterPipeline pipeline;

    /** listener to view selection. */
    private ListSelectionListener viewSelectionListener;
    /** state flag for locking non-listening phases. */
    private boolean isListening;

    /** listener to mapping pipeline. */
    private PipelineListener pipelineListener;


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
    }

    /**
     * sets the view selection model. Must not be null.
     * 
     * @param selection holding selected indices in view coordinates
     */
    public void setViewSelectionModel(ListSelectionModel selection) {
        ListSelectionModel old = this.viewSelection;
        if (old != null) {
            old.removeListSelectionListener(viewSelectionListener);
            modelSelection.clearSelection();
        }
        this.viewSelection = selection;
        mapTowardsModel();
        unlock();
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


    public void restoreSelection() {
        lock();
        // JW - hmm... clearSelection doesn't reset the lead/anchor. Why not?
        viewSelection.clearSelection();

        int[] selected = getSelectedRows(modelSelection);
        for (int i = 0; i < selected.length; i++) {
          int index = convertToView(selected[i]);
          // index might be -1, but then addSelectionInterval ignores it. 
          viewSelection.addSelectionInterval(index, index);
        }
        int lead = modelSelection.getLeadSelectionIndex();
        lead = convertToView(lead);
        if (viewSelection instanceof DefaultListSelectionModel) {
            ((DefaultListSelectionModel) viewSelection).moveLeadSelectionIndex(lead);
        } else {
            // PENDING: not tested, don't have a non-DefaultXX handy
            viewSelection.removeSelectionInterval(lead, lead);
            viewSelection.addSelectionInterval(lead, lead);
        }
        unlock();
    }

    public void unlock() {
        if (!isListening) {
            viewSelection.setValueIsAdjusting(false);
            viewSelection.addListSelectionListener(getViewSelectionListener());
            isListening = true;
        }
    }

    public void lock() {
        if (viewSelectionListener != null) {
            viewSelection.removeListSelectionListener(viewSelectionListener);
            viewSelection.setValueIsAdjusting(true);
            isListening = false;
        }
    }

    public void clearModelSelection() {
        // JW: need to reset anchor/lead?
        modelSelection.clearSelection();
    }

    public void insertIndexInterval(int start, int length, boolean before) {
        modelSelection.insertIndexInterval(start, length, before);
    }

    public void removeIndexInterval(int start, int end) {
        modelSelection.removeIndexInterval(start, end);
    }

    private void mapTowardsModel() {
        modelSelection.clearSelection();
        int[] selected = getSelectedRows(viewSelection); 
        for (int i = 0; i < selected.length; i++) {
            int modelIndex = convertToModel(selected[i]);
            modelSelection.addSelectionInterval(modelIndex, modelIndex); 
        }
        if (selected.length > 0) {
            // convert lead selection index to model coordinates
            modelSelection.moveLeadSelectionIndex(convertToModel(viewSelection.getLeadSelectionIndex()));
        }
    }

    private int convertToModel(int index) {
        // JW: check for valid index? must be < pipeline.getOutputSize()
        return pipeline != null ? pipeline.convertRowIndexToModel(index) : index;
    }
    
    private int convertToView(int index) {
        // JW: check for valid index? must be < pipeline.getInputSize()
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

    private int[] getSelectedRows(ListSelectionModel selection) {
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

}
