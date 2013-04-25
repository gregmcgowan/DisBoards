package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.io.InputStream;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

public abstract class InputStreamTest extends InstrumentationTestCase {
    
    private AssetManager assetManager;
    private InputStream testInputStream;
    
    public AssetManager getAssetManager() {
        return assetManager;
    }

    public InputStream getTestInputStream() {
        return testInputStream;
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	assetManager = getInstrumentation().getContext().getAssets();
	testInputStream = assetManager.open(getTestInputStreamFilename()); 
    }
    
    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
	testInputStream.close();
    }
    
    protected abstract String getTestInputStreamFilename();
}
