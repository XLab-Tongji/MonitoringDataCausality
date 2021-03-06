package edu.tongji.xlab.controller;

import edu.cmu.tetrad.search.*;
import edu.tongji.xlab.util.DataFileUtils;
import edu.tongji.xlab.util.Response;

import edu.cmu.tetrad.data.DataUtils;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.File;

@RestController
public class DataController {

    @CrossOrigin("*")
    @PostMapping("/causality")
    public Response upload(HttpServletRequest request,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam("algorithm") String algorithm,
                           @RequestParam("format") String format,
                           @RequestParam(value = "indtest", defaultValue = "fisherz" ,required = false) String indtest)
            throws IOException {
//        model.addAttribute("data", "");
        Response response;
        if (file.isEmpty()) {
            response = new Response(500, "The file is empty.");
        }
        try {
            String fileName = file.getOriginalFilename();
            String destFileName = request.getServletContext().getRealPath("") + "/uploaded" + File.separator + fileName;
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);

            DataSet dataset = DataFileUtils.convertDataFileToDataset(destFile);

            DataSet filteredSet = DataUtils.removeConstantColumns(dataset);

            IndependenceTest ind_test;
            if(indtest.equals("fisherz")) {
                ind_test = new IndTestFisherZ(filteredSet, 0.05);
            }else if(indtest.equals("ttest")){
                ind_test = new IndTestCorrelationT(filteredSet, 0.05);
            }else if(indtest.equals("gsquare")){
                ind_test = new IndTestGSquare(filteredSet, 0.05);
            }else{
                ind_test = new IndTestFisherZ(filteredSet, 0.05);
            }


            String jsonText = new String();
            Graph graph = null;
            if (algorithm.equals("Fci")) {
                Fci fci = new Fci(ind_test);
                graph = fci.search();
            } else if (algorithm.equals("Pc")) {
                Pc pc = new Pc(ind_test);
                graph = pc.search();
            }

            if (format.equals("text")) {
                jsonText = graph.toString();
            } else {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                jsonText = gson.toJson(graph);
            }

            if (destFile.exists()) {
                destFile.delete();
            }

            response = new Response(200, "Success", jsonText);

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(500, "Error"+e.getMessage());
        }
        return response;
    }
}
