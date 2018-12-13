package edu.tongji.xlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tongji.xlab.data.DataModel;
import edu.tongji.xlab.data.DataSet;
import edu.tongji.xlab.graph.Graph;
import edu.tongji.xlab.search.Fci;
import edu.tongji.xlab.search.IndTestFisherZ;
import edu.tongji.xlab.search.Pc;
import edu.tongji.xlab.util.DataConvertUtils;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.validation.DataValidation;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

@Controller
public class FormController {

    @RequestMapping("/")
    public String formData() {
        return "upload";
    }

    @RequestMapping("/upload")
    public String upload(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("algorithm") String algorithm, Model model) throws IOException {
        model.addAttribute("data", "");
        if (file.isEmpty()) {
            model.addAttribute("message", "File is empty");
            return "display";
        }
        try {
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

            System.out.println("INFO: " + infos.size() + " WARNING: " + warnings.size() + " ERRORS: " + errors.size());


            TabularDataReader dataReader = null;
            dataReader = new ContinuousTabularDataFileReader(destFile, delimiter);

            try {
                Dataset dataset = dataReader.readInData();
                DataModel datamodel = DataConvertUtils.toDataModel(dataset);
                if (datamodel instanceof DataSet)
                    System.out.println("This is a DataSet");
                else {
                    System.out.println("Not a DataSet, Terminated");
                }
                IndTestFisherZ indtest = new IndTestFisherZ((DataSet) datamodel, 0.01);

                String jsonText = new String();

                if (algorithm.equals("Fci")) {
                    Fci fci = new Fci(indtest);
                    Graph graph = fci.search();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    jsonText = gson.toJson(graph);
                } else if (algorithm.equals("Pc")) {
                    Pc pc = new Pc(indtest);
                    Graph graph = pc.search();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    jsonText = gson.toJson(graph);
                }

                if (destFile.exists()) {
                    destFile.delete();
                }

                model.addAttribute("message", algorithm);
                model.addAttribute("data", jsonText);

            } catch (IOException e) {

                model.addAttribute("message", "error");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
        return "display";
    }
}
