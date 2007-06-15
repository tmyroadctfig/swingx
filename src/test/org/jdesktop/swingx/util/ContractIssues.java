package org.jdesktop.swingx.util;

import junit.framework.TestCase;

public class ContractIssues extends TestCase {
    /**
     * Fails with a ClassCastException
     */
    public void testPrimitiveArray1() {
        Contract.asNotNull(new int[]{}, "Should work");
    }
    
    /**
     * Fails with a ClassCastException
     */
    public void testPrimitiveArray2() {
        Contract.asNotNull(new int[0], "Should work");
    }
    
    /**
     * Fails with a ClassCastException
     */
    public void testPrimitiveArray3() {
        Contract.asNotNull(new int[]{1}, "Should work");
    }
    
    /**
     * Fails with a ClassCastException
     */
    public void testPrimitiveArray4() {
        Contract.asNotNull(new int[1], "Should work");
    }
}
