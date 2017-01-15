package lia.common;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Paths.get;

/**
 * Created by rajanishivarajmaski1 on 10/9/16.
 */
public class Utils {

    /**
     * @param indexDir
     * @return
     * @throws IOException
     */
    public static IndexWriter getIndexWriter(String indexDir) throws IOException {
        IndexWriter writer;
        Path path = get(indexDir);
        Directory dir = NIOFSDirectory.open(path);
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, indexWriterConfig);

        return writer;
    }

    /**
     * @return
     * @throws IOException
     */
    public static IndexWriter getIndexWriter() throws IOException {
        IndexWriter writer;
        Directory dir = new RAMDirectory();
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, indexWriterConfig);

        return writer;
    }

    /**
     * @return
     * @throws IOException
     */
    public static IndexWriter getIndexWriter(Directory directory) throws IOException {
        IndexWriter writer;
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(directory, indexWriterConfig);

        return writer;
    }


    public static IndexWriter getIndexWriterWithInfoStream(Directory directory) throws IOException {
        IndexWriter writer;
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriterConfig.setInfoStream(System.out);

        writer = new IndexWriter(directory, indexWriterConfig);
        return writer;
    }

    /**
     * @param indexDir
     * @return
     * @throws IOException
     */
    public static IndexSearcher getIndexSearcher(String indexDir) throws IOException {
        Path path = Paths.get(indexDir);
        Directory dir = NIOFSDirectory.open(path);
        final IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher searcher = new IndexSearcher(reader);

        return searcher;
    }

    /**
     * @param
     * @return
     * @throws IOException
     */
    public static IndexSearcher getIndexSearcher(Directory dir) throws IOException {
        final IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher searcher = new IndexSearcher(reader);

        return searcher;
    }


    public static IndexSearcher getBookIndexSearcher() throws IOException {

        final IndexSearcher searcher = getIndexSearcher("/Users/rajanishivarajmaski1/University/CSC899/" +
                "lucene_in_action/lia2e_2/index");

        return searcher;
    }

    /**
     * @return
     */
    public static FieldType getTextFieldTypeWithTermVectors() {
        FieldType fieldType = new FieldType();
        fieldType.setDocValuesType(DocValuesType.NUMERIC);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        IndexOptions value = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
        fieldType.setIndexOptions(value);
        return fieldType;
    }


}



