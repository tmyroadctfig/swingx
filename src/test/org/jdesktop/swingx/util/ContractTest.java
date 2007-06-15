package org.jdesktop.swingx.util;

import junit.framework.TestCase;

public class ContractTest extends TestCase {
    public void testAsNotNull() {
        //expected good cases with Objects
        Contract.asNotNull("1", "Works");
        Contract.asNotNull(new String[]{}, "Works");
        Contract.asNotNull(new String[0], "Works");
        Contract.asNotNull(new String[]{"1"}, "Works");
        
        //expected failure cases with Objects
        try {
            Contract.asNotNull(null, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{null}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[1], "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{"1", null}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{"1", null, "2"}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        //expected failure cases with primitives
        int[] badArray = null;
        
        try {
            Contract.asNotNull(badArray, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
    }
}
