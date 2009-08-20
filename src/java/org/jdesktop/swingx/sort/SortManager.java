/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.sort;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

import org.jdesktop.swingx.JXList;

import sun.swing.SwingUtilities2;

/**
 * SortManager provides support for managing the selection and variable
 * row heights when sorting is enabled. This information is encapsulated
 * into a class to avoid bulking up JTable.
 */
public final class SortManager implements RowSorterListener {
    RowSorter<? extends ListModel> sorter;
    JXList table;

    // Selection, in terms of the model. This is lazily created
    // as needed.
    private ListSelectionModel modelSelection;
    private int modelLeadIndex;
    // Set to true while in the process of changing the selection.
    // If this is true the selection change is ignored.
    private boolean syncingSelection;
    // Temporary cache of selection, in terms of model. This is only used
    // if we don't need the full weight of modelSelection.
    private int[] lastModelSelection;
    private boolean sorterChanged;
    private boolean ignoreSortChange;



    public SortManager(RowSorter<? extends ListModel> sorter, JXList list) {
        this.sorter = sorter;
        this.table = list;
        sorter.addRowSorterListener(this);
    }

    /**
     * Disposes any resources used by this SortManager.
     */
    public void dispose() {
        if (sorter != null) {
            sorter.removeRowSorterListener(this);
        }
    }


    /**
     * @return
     */
    private int getModelRowCount() {
        return sorter.getModelRowCount();
    }

    private ListSelectionModel getViewSelectionModel() {
        return table.getSelectionModel();
    }
    /**
     * Invoked when the underlying model has completely changed.
     */
    public void allChanged() {
        modelLeadIndex = -1;
        modelSelection = null;
    }

    /**
     * Invoked when the selection, on the view, has changed.
     */
    public void viewSelectionChanged(ListSelectionEvent e) {
        if (!syncingSelection && modelSelection != null) {
            modelSelection = null;
        }
    }

    /**
     * Invoked when either the table model has changed, or the RowSorter
     * has changed. This is invoked prior to notifying the sorter of the
     * change.
     */
    public void prepareForChange(RowSorterEvent sortEvent,
                                 ModelChange change) {
//        if (table.getUpdateSelectionOnSort()) {
//        }
        cacheSelection(sortEvent, change);
    }

    /**
     * Updates the internal cache of the selection based on the change.
     */
    private void cacheSelection(RowSorterEvent sortEvent,
                                ModelChange change) {
        if (sortEvent != null) {
            // sort order changed. If modelSelection is null and filtering
            // is enabled we need to cache the selection in terms of the
            // underlying model, this will allow us to correctly restore
            // the selection even if rows are filtered out.
            if (modelSelection == null &&
                    sorter.getViewRowCount() != getModelRowCount()) {
                modelSelection = new DefaultListSelectionModel();
                ListSelectionModel viewSelection = getViewSelectionModel();
                int min = viewSelection.getMinSelectionIndex();
                int max = viewSelection.getMaxSelectionIndex();
                int modelIndex;
                for (int viewIndex = min; viewIndex <= max; viewIndex++) {
                    if (viewSelection.isSelectedIndex(viewIndex)) {
                        modelIndex = convertRowIndexToModel(
                                sortEvent, viewIndex);
                        if (modelIndex != -1) {
                            modelSelection.addSelectionInterval(
                                modelIndex, modelIndex);
                        }
                    }
                }
                modelIndex = convertRowIndexToModel(sortEvent,
                        viewSelection.getLeadSelectionIndex());
                SwingUtilities2.setLeadAnchorWithoutSelection(
                        modelSelection, modelIndex, modelIndex);
            } else if (modelSelection == null) {
                // Sorting changed, haven't cached selection in terms
                // of model and no filtering. Temporarily cache selection.
                cacheModelSelection(sortEvent);
            }
        } else if (change.allRowsChanged) {
            // All the rows have changed, chuck any cached selection.
            modelSelection = null;
        } else if (modelSelection != null) {
            // Table changed, reflect changes in cached selection model.
            switch(change.type) {
            case ListDataEvent.INTERVAL_REMOVED:
                modelSelection.removeIndexInterval(change.startModelIndex,
                                                   change.endModelIndex);
                break;
            case ListDataEvent.INTERVAL_ADDED:
                modelSelection.insertIndexInterval(change.startModelIndex,
                                                   change.endModelIndex,
                                                   true);
                break;
            default:
                break;
            }
        } else {
            // table changed, but haven't cached rows, temporarily
            // cache them.
            cacheModelSelection(null);
        }
    }

    private int convertRowIndexToModel(RowSorterEvent e, int viewIndex) {
        if (e != null) {
            if (e.getPreviousRowCount() == 0) {
                return viewIndex;
            }
            // range checking handled by RowSorterEvent
            return e.convertPreviousRowIndexToModel(viewIndex);
        }
        // Make sure the viewIndex is valid
        if (viewIndex < 0 || viewIndex >= sorter.getViewRowCount()) {
            return -1;
        }
        return sorter.convertRowIndexToModel(viewIndex);
    }

    /**
     * Converts the selection to model coordinates.  This is used when
     * the model changes or the sorter changes.
     */
    private int[] convertSelectionToModel(RowSorterEvent e) {
        int[] selection = table.getSelectedIndices();
        for (int i = selection.length - 1; i >= 0; i--) {
            selection[i] = convertRowIndexToModel(e, selection[i]);
        }
        return selection;
    }

    private void cacheModelSelection(RowSorterEvent sortEvent) {
        lastModelSelection = convertSelectionToModel(sortEvent);
        modelLeadIndex = convertRowIndexToModel(sortEvent,
                    getViewSelectionModel().getLeadSelectionIndex());
    }

    /**
     * Inovked when either the table has changed or the sorter has changed
     * and after the sorter has been notified. If necessary this will
     * reapply the selection and variable row heights.
     */
    public void processChange(RowSorterEvent sortEvent,
                              ModelChange change,
                              boolean sorterChanged) {
        if (change != null) {
//            if (change.allRowsChanged) {
//                modelRowSizes = null;
//                rowModel = null;
//            } else if (modelRowSizes != null) {
//                if (change.type == TableModelEvent.INSERT) {
//                    modelRowSizes.insertEntries(change.startModelIndex,
//                                                change.endModelIndex -
//                                                change.startModelIndex + 1,
//                                                getRowHeight());
//                } else if (change.type == TableModelEvent.DELETE) {
//                    modelRowSizes.removeEntries(change.startModelIndex,
//                                                change.endModelIndex -
//                                                change.startModelIndex +1 );
//                }
//            }
        }
        if (sorterChanged) {
            restoreSelection(change);
        }
    }

    /**
     * Restores the selection from that in terms of the model.
     */
    private void restoreSelection(ModelChange change) {
        syncingSelection = true;
        if (lastModelSelection != null) {
            restoreSortingSelection(lastModelSelection,
                                    modelLeadIndex, change);
            lastModelSelection = null;
        } else if (modelSelection != null) {
            ListSelectionModel viewSelection = getViewSelectionModel();
            viewSelection.setValueIsAdjusting(true);
            viewSelection.clearSelection();
            int min = modelSelection.getMinSelectionIndex();
            int max = modelSelection.getMaxSelectionIndex();
            int viewIndex;
            for (int modelIndex = min; modelIndex <= max; modelIndex++) {
                if (modelSelection.isSelectedIndex(modelIndex)) {
                    viewIndex = sorter.convertRowIndexToView(modelIndex);
                    if (viewIndex != -1) {
                        viewSelection.addSelectionInterval(viewIndex,
                                                           viewIndex);
                    }
                }
            }
            // Restore the lead
            int viewLeadIndex = modelSelection.getLeadSelectionIndex();
            if (viewLeadIndex != -1) {
                viewLeadIndex = sorter.convertRowIndexToView(viewLeadIndex);
            }
            SwingUtilities2.setLeadAnchorWithoutSelection(
                    viewSelection, viewLeadIndex, viewLeadIndex);
            viewSelection.setValueIsAdjusting(false);
        }
        syncingSelection = false;
    }
    
    /**
     * Restores the selection after a model event/sort order changes.
     * All coordinates are in terms of the model.
     */
    private void restoreSortingSelection(int[] selection, int lead,
            ModelChange change) {
        // Convert the selection from model to view
        for (int i = selection.length - 1; i >= 0; i--) {
            selection[i] = convertRowIndexToView(selection[i], change);
        }
        lead = convertRowIndexToView(lead, change);

        // Check for the common case of no change in selection for 1 row
        if (selection.length == 0 ||
            (selection.length == 1 && selection[0] == table.getSelectedIndex())) {
            return;
        }
        ListSelectionModel selectionModel = getViewSelectionModel();
        // And apply the new selection
        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        for (int i = selection.length - 1; i >= 0; i--) {
            if (selection[i] != -1) {
                selectionModel.addSelectionInterval(selection[i],
                                                    selection[i]);
            }
        }
        SwingUtilities2.setLeadAnchorWithoutSelection(
                selectionModel, lead, lead);
        selectionModel.setValueIsAdjusting(false);
    }

    /**
     * Converts a model index to view index.  This is called when the
     * sorter or model changes and sorting is enabled.
     *
     * @param change describes the TableModelEvent that initiated the change;
     *        will be null if called as the result of a sort
     */
    private int convertRowIndexToView(int modelIndex, ModelChange change) {
        if (modelIndex < 0) {
            return -1;
        }
        if (change != null && modelIndex >= change.startModelIndex) {
            if (change.type == ListDataEvent.INTERVAL_ADDED) {
                if (modelIndex + change.length >= change.modelRowCount) {
                    return -1;
                }
                return sorter.convertRowIndexToView(
                        modelIndex + change.length);
            }
            else if (change.type == ListDataEvent.INTERVAL_REMOVED) {
                if (modelIndex <= change.endModelIndex) {
                    // deleted
                    return -1;
                }
                else {
                    if (modelIndex - change.length >= change.modelRowCount) {
                        return -1;
                    }
                    return sorter.convertRowIndexToView(
                            modelIndex - change.length);
                }
            }
            // else, updated
        }
        if (modelIndex >= getModelRowCount()) {
            return -1;
        }
        return sorter.convertRowIndexToView(modelIndex);
    }

    /**
     * ModelChange is used when sorting to restore state, it corresponds
     * to data from a TableModelEvent.  The values are precalculated as
     * they are used extensively.
     */
     final static class ModelChange {
        // Starting index of the change, in terms of the model
        int startModelIndex;

        // Ending index of the change, in terms of the model
        int endModelIndex;

        // Type of change
        int type;

        // Number of rows in the model
        int modelRowCount;

//        // The event that triggered this.
//        ListDataEvent event;

        // Length of the change (end - start + 1)
        int length;

        // True if the event indicates all the contents have changed
        boolean allRowsChanged;

        public ModelChange(ListDataEvent e) {
            type = e.getType();
            startModelIndex = e.getIndex0();
            endModelIndex = e.getIndex1();
            allRowsChanged = startModelIndex < 0;
            modelRowCount = ((ListModel) e.getSource()).getSize();
            if (allRowsChanged) {
                startModelIndex = Math.max(0, startModelIndex);
                endModelIndex = Math.max(0, modelRowCount - 1);
            }
            length = endModelIndex - startModelIndex + 1;
        }
    }

    @Override
    public void sorterChanged(RowSorterEvent e) {
        ListDataListener l;
        if (e.getType() == RowSorterEvent.Type.SORTED) {
            sorterChanged = true;
            if (!ignoreSortChange) {
                sortedTableChanged(e);
            }
        }
    }

    /**
     * @param e
     */
    private void sortedTableChanged(RowSorterEvent e) {
        prepareForChange(e, null);
        processChange(e, null, sorterChanged);
        table.repaint();
    }

    /**
     * Invoked when <code>sorterChanged</code> is invoked, or
     * when <code>tableChanged</code> is invoked and sorting is enabled.
     */
    private void sortedTableChanged(RowSorterEvent sortedEvent,
                                    ListDataEvent e) {
        int editingModelIndex = -1;
        ModelChange change = (e != null) ? new ModelChange(e) : null;


        prepareForChange(sortedEvent, change);

        if (change != null) {
            if (change.type == ListDataEvent.CONTENTS_CHANGED) {
//                repaintSortedRows(change);
            }
            notifySorter(change);
            if (change.type != ListDataEvent.CONTENTS_CHANGED) {
                // If the Sorter is unsorted we will not have received
                // notification, force treating insert/delete as a change.
                sorterChanged = true;
            }
        }
        else {
            sorterChanged = true;
        }

        processChange(sortedEvent, change, sorterChanged);

        if (sorterChanged) {

            // And handle the appropriate repainting.
//            if (e == null || change.type != TableModelEvent.UPDATE) {
////                resizeAndRepaint();
//            }
            table.repaint();
        }

        // Check if lead/anchor need to be reset.
        if (change != null && change.allRowsChanged) {
//            table.clearSelectionAndLeadAnchor();
//            resizeAndRepaint();
        }
    }

    /**
     * Notifies the sorter of a change in the underlying model.
     */
    private void notifySorter(ModelChange change) {
        try {
            ignoreSortChange = true;
            sorterChanged = false;
            if (change.allRowsChanged) {
                sorter.allRowsChanged();
            } else {
            switch(change.type) {
            case ListDataEvent.CONTENTS_CHANGED:
                    sorter.rowsUpdated(change.startModelIndex,
                                       change.endModelIndex);
                break;
            case ListDataEvent.INTERVAL_ADDED:
                sorter.rowsInserted(change.startModelIndex,
                                    change.endModelIndex);
                break;
            case ListDataEvent.INTERVAL_REMOVED:
                sorter.rowsDeleted(change.startModelIndex,
                                   change.endModelIndex);
                break;
            }
            }
        } finally {
            ignoreSortChange = false;
        }
    }

}

