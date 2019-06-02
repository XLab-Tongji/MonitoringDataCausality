package edu.tongji.xlab.util;

import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.pitt.dbmi.data.reader.Data;
import edu.pitt.dbmi.data.reader.DataColumn;

import java.io.IOException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.pitt.dbmi.data.reader.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.reader.validation.ValidationResult;
import edu.pitt.dbmi.data.reader.validation.tabular.TabularColumnFileValidation;
import edu.pitt.dbmi.data.reader.validation.tabular.TabularColumnValidation;
import edu.pitt.dbmi.data.reader.validation.tabular.TabularDataFileValidation;


public class DataFileUtils {
    private static Delimiter delimiter = Delimiter.COMMA;
    private static String missingDataMarker = "null";

    public static DataSet convertDataFileToDataset(File file) throws Exception{
        TabularColumnValidation tabularColumnValidation = new TabularColumnFileValidation(file.toPath(), delimiter);

        List<ValidationResult> results = tabularColumnValidation.validate();

        DataColumn[] dataColumns = readInTabularColumns(file);

        TabularDataFileValidation validatior = new TabularDataFileValidation(file.toPath(), Delimiter.COMMA);
        validatior.setMissingDataMarker(missingDataMarker);

        results.addAll(validatior.validate(dataColumns, true));

        List<ValidationResult> infos = new LinkedList<>();
        List<ValidationResult> warnings = new LinkedList<>();
        List<ValidationResult> errors = new LinkedList<>();

        for (ValidationResult result : results) {
            switch (result.getCode()) {
                case INFO:
                    System.out.println("INFO:"+result.toString());
                    infos.add(result);
                    break;
                case WARNING:
                    System.out.println("WARNING:"+result.toString());
                    warnings.add(result);
                    break;
                default:
                    System.out.println("ERROR:"+result.toString());
                    errors.add(result);
            }
        }

        System.out.println("INFO: " + infos.size() + " WARNING: " + warnings.size() + " ERRORS: " + errors.size());

        TabularDataReader dataReader = new TabularDataFileReader(file.toPath(), delimiter);
        dataReader.setMissingDataMarker(missingDataMarker);

        Data data = dataReader.read(dataColumns, true);
        DataSet dataset = (DataSet) DataConvertUtils.toDataModel(data);

        return dataset;
    }

    public static DataColumn[] readInTabularColumns(File file) throws IOException {
        DataColumn[] dataColumns = null;
        TabularColumnReader columnReader = new TabularColumnFileReader(file.toPath(), delimiter);
        boolean isDiscrete = false;
        dataColumns = columnReader.readInDataColumns(new int[0], isDiscrete);
        return dataColumns;
    }
}
