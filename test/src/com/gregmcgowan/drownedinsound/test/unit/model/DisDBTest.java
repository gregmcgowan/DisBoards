package com.gregmcgowan.drownedinsound.test.unit.model;

import android.test.AndroidTestCase;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;

public class DisDBTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	DatabaseHelper.getInstance(getContext()); 
    }

    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
	DatabaseHelper.getInstance(getContext()).clearAllTables();
    }

    
    
}
