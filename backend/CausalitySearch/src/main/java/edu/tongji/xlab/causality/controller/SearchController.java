package edu.tongji.xlab.causality.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.Fci;
import edu.cmu.tetrad.search.IndTestFisherZ;
import edu.cmu.tetrad.search.Pc;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.validation.DataValidation;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

@RestController
public class SearchController {

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String search(HttpServletRequest request,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("algorithm") String algorithm) throws IOException {
        JSONObject resultJson = new JSONObject();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String destFileName = request.getServletContext().getRealPath("") + "/uploaded" + File.separator + fileName;
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);

            Delimiter delimiter = Delimiter.COMMA;
            DataValidation validatior = new ContinuousTabularDataFileValidation(destFile, delimiter);
            validatior.validate();

            List<ValidationResult> results = validatior.getValidationResults();
            List<ValidationResult> infos = new LinkedList<>();
            // Just leave the warnings here to future use - Zhou
            List<ValidationResult> warnings = new LinkedList<>();
            List<ValidationResult> errors = new LinkedList<>();

            for (ValidationResult result : results) {
                switch (result.getCode()) {
                    case INFO:
                        infos.add(result);
                        break;
                    case WARNING:
                        warnings.add(result);
                        break;
                    default:
                        errors.add(result);
                }
            }

            System.out.println("INFO: "+infos.size()+" WARNING: "+ warnings.size()+ " ERRORS: "+errors.size());


            TabularDataReader dataReader = null;
            dataReader = new ContinuousTabularDataFileReader(destFile, delimiter);

            try{
                Dataset dataset = dataReader.readInData();
                DataModel datamodel = DataConvertUtils.toDataModel(dataset);
                if(datamodel instanceof DataSet)
                    System.out.println("This is a DataSet");
                else{
                    System.out.println("Not a DataSet, Terminated");
                }
                IndTestFisherZ indtest = new IndTestFisherZ((DataSet)datamodel, 0.01);

                String jsonText = new String();

                if(algorithm.equals("Fci")){
                    Fci fci = new Fci(indtest);
                    Graph graph = fci.search();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    jsonText = gson.toJson(graph);
                }
                else if(algorithm.equals("Pc")){
                    Pc pc = new Pc(indtest);
                    Graph graph = pc.search();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    jsonText = gson.toJson(graph);
                }

                if (destFile.exists()) {
                    destFile.delete();
                }

                resultJson.put("code", 200);
                resultJson.put("msg", "Success");
                resultJson.put("data", jsonText);
            }catch (IOException e){
                resultJson.put("code", 500);
                resultJson.put("msg", "IO ERROR happens");
            }

            if (destFile.exists()) {
                destFile.delete();
            }
            return resultJson.toJSONString();

        } else {
            resultJson.put("code", 406);
            resultJson.put("msg", "The file is empty");
            return resultJson.toJSONString();
        }
    }
}
