package lia.chapter2;

/**
 * Copyright Manning Publications Co.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */

import junit.framework.TestCase;
import lia.common.Utils;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// From chapter 2
public class LockTest extends TestCase {

    private Directory dir;
    private String indexDir;

    protected void setUp() throws IOException {
        //TODO : provide index directory path
        indexDir = "";
        Path path = Paths.get(indexDir);
        dir = NIOFSDirectory.open(path);
    }

    public void testWriteLock() throws IOException {

        IndexWriter writer1 = Utils.getIndexWriter(dir);
        IndexWriter writer2 = null;
        try {
            writer2 = Utils.getIndexWriter(dir);
            fail("We should never reach this point");
        } catch (LockObtainFailedException e) {
            // e.printStackTrace();  // #A
        } finally {
            writer1.close();
            assertNull(writer2);
        }
    }
}

/*
#A Expected exception: only one IndexWriter allowed at once
*/
