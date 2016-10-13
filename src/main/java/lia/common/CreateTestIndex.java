package lia.common;

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

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CreateTestIndex {

    /**
     * Parse each record, create lucene document and return the doc
     * @param rootDir
     * @param file
     * @return lucene document object
     * @throws IOException
     */
    public static Document getDocument(String rootDir, File file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));

        Document doc = new Document();

        // category comes from relative path below the base directory
        String category = file.getParent().substring(rootDir.length());    //1
        category = category.replace(File.separatorChar, '/');              //1

        String isbn = props.getProperty("isbn");         //2
        String title = props.getProperty("title");       //2
        String author = props.getProperty("author");     //2
        String url = props.getProperty("url");           //2
        String subject = props.getProperty("subject");   //2

        String pubmonth = props.getProperty("pubmonth"); //2

        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");

        doc.add(new Field("isbn", isbn,                       // 3
                Utils.getTextFieldTypeWithTermVectors())); // 3
        doc.add(new Field("category", category,                   // 3
                Utils.getTextFieldTypeWithTermVectors())); // 3
        doc.add(new Field("title", title,                      // 3
                Utils.getTextFieldTypeWithTermVectors()           // 3
        ));   // 3
        doc.add(new Field("title2", title.toLowerCase(),        // 3
                Utils.getTextFieldTypeWithTermVectors()));  // 3

        // split multiple authors into unique field instances
        String[] authors = author.split(",");            // 3
        for (String a : authors) {                       // 3
            doc.add(new Field("author", a,                           // 3
                    Utils.getTextFieldTypeWithTermVectors()));   // 3
        }

        doc.add(new Field("url", url,                           // 3
                StringField.TYPE_STORED));   // 3
        doc.add(new Field("subject",                     // 3  //4
                subject,                       // 3  //4
                Utils.getTextFieldTypeWithTermVectors())); // 3  //4

        doc.add(new NumericDocValuesField("pubmonth",
                (Integer.parseInt(pubmonth))));   // 3

        Date d; // 3
        try { // 3
            d = DateTools.stringToDate(pubmonth); // 3
        } catch (ParseException pe) { // 3
            throw new RuntimeException(pe); // 3
        }                                             // 3
        doc.add(new NumericDocValuesField("pubmonthAsDay",    // 3
                (Long) (d.getTime() / (1000 * 3600 * 24))));   // 3

        for (String text : new String[]{title, subject, author, category}) {           // 3 // 5
            doc.add(new Field("contents", text,                             // 3 // 5
                    Utils.getTextFieldTypeWithTermVectors()));    // 3 // 5
        }

        return doc;
    }

    private static void findFiles(List<File> result, File dir) {
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".properties")) {
                result.add(file);
            } else if (file.isDirectory()) {
                findFiles(result, file);
            }
        }
    }

    /**
     * Provide path for lia2e/data directory & location where the indexes should be stored.
     * By default, lia2e build.xml executes this class generates index and saves it into build/index.
     * But this index will be created using older version apis of lucene(referred in build.xml) and will not be compatible for latest lucene version apis,
     * so requires to re run.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String dataDir = "/Users/rajanishivarajmaski1/University/CSC899/lucene_in_action/lia2e_2/data/";
        String indexDir = "/Users/rajanishivarajmaski1/University/CSC899/lucene_in_action/lia2e_2/index";
        List<File> results = new ArrayList<>();
        findFiles(results, new File(dataDir));
        System.out.println(results.size() + " books to index");
        IndexWriter w = Utils.getIndexWriter(indexDir);
        for (File file : results) {
            Document doc = getDocument(dataDir, file);
            w.addDocument(doc);
        }
        w.close();
    }


}

/*
  #1 Get category
  #2 Pull fields
  #3 Add fields to Document instance
  #4 Flag subject field
  #5 Add catch-all contents field
  #6 Custom analyzer to override multi-valued position increment
*/
