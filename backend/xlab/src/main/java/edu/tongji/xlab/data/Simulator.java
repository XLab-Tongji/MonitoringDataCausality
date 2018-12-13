package edu.tongji.xlab.data;

import edu.tongji.xlab.util.TetradSerializable;

/**
 * Created by jdramsey on 12/22/15.
 */
public interface Simulator extends TetradSerializable {
    long serialVersionUID = 23L;
    DataSet simulateData(int sampleSize, boolean latentDataSaved);
    DataSet simulateData(int sampleSize, long sampleSeed, boolean latentDataSaved);
}
