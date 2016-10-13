package lia.chapter1;

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

import lia.common.Utils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import static java.nio.file.Paths.get;


// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        writer = Utils.getIndexWriter(indexDir);
    }

    /**
     * /Users/rajanishivarajmaski1/University/CSC899/lucene_in_action/lia2e/index
     /Users/rajanishivarajmaski1/University/CSC899/lucene_in_action/lia2e/src/lia/meetlucene/data/
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String indexDir = scanner.nextLine();
        String dataDir = scanner.nextLine();

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + " milliseconds");
    }

    public void close() throws IOException {
        writer.close();                             //4
    }

    public int index(String dataDir, FileFilter filter)
            throws Exception {

        File[] files = new File(dataDir).listFiles();

        for (File f : files) {
            if (!f.isDirectory() &&
                    !f.isHidden() &&
                    f.exists() &&
                    f.canRead() &&
                    (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }

        return writer.numDocs();                     //5
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        String entireFileText = new Scanner(f)
                .useDelimiter("\\A").next();
        FieldType type = new FieldType();
        type.setStoreTermVectors(true);
        type.setStored(true);
        // System.out.println((new String(readAllBytes(get("test.txt")));
        doc.add(new Field("content", entireFileText, TextField.TYPE_STORED));
        doc.add(new Field("filename", f.getName(), TextField.TYPE_STORED));
        doc.add(new Field("filepath", f.getCanonicalPath(), TextField.TYPE_STORED));
        writer.addDocument(doc);
        return doc;
    }

    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);                              //10
    }


    private static class TextFilesFilter implements FileFilter {
        public boolean accept(File path) {
            return path.getName().toLowerCase()        //6
                    .endsWith(".txt");                  //6
        }
    }
}

/*
#1 Create index in this directory
#2 Index *.txt files from this directory
#3 Create Lucene IndexWriter
#4 Close IndexWriter
#5 Return number of documents indexed
#6 Index .txt files only, using FileFilter
#7 Index file content
#8 Index file name
#9 Index file full path
#10 Add document to Lucene index
*/
