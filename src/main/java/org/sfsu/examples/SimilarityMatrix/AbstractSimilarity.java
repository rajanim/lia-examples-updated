package org.sfsu.examples.SimilarityMatrix;


import org.apache.commons.collections.Transformer;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Specifies the transformation of a Term Document Matrix into a Similarity
 * matrix. Specifies a protected abstract computeSimilarity() method that
 * must be defined for concrete subclasses.
 *
 * @author Sujit Pal
 * @version $Revision: 21 $
 */
public abstract class AbstractSimilarity {


    public abstract double computeSimilarity(RealMatrix sourceDoc, RealMatrix targetDoc);

    public RealMatrix transform(RealMatrix termDocumentMatrix) {
        int numDocs = termDocumentMatrix.getColumnDimension();
        RealMatrix similarityMatrix = new OpenMapRealMatrix(numDocs, numDocs);
        for (int i = 0; i < numDocs; i++) {
            RealMatrix sourceDocMatrix = termDocumentMatrix.getSubMatrix(
                    0, termDocumentMatrix.getRowDimension() - 1, i, i);
            for (int j = 0; j < numDocs; j++) {
                RealMatrix targetDocMatrix = termDocumentMatrix.getSubMatrix(
                        0, termDocumentMatrix.getRowDimension() - 1, j, j);
                similarityMatrix.setEntry(i, j,
                        computeSimilarity(sourceDocMatrix, targetDocMatrix));
            }
        }
        return similarityMatrix;
    }


}