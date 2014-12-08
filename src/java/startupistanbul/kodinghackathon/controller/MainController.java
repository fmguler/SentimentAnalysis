/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package startupistanbul.kodinghackathon.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import startupistanbul.kodinghackathon.service.SentimentService;

/**
 *
 * @author Fatih
 */
@Controller
public class MainController {
     //json enumerations
    public static final String JSON_STATUS_SUCCESS = "0";
    public static final String JSON_STATUS_FAIL = "-1";
    
    //service
    private SentimentService sentimentService;
            
    
     //ajax - analyze tweets
    @RequestMapping()
    @ResponseBody
    public String analyzeTweets(String tweets, String batchId, Integer start, Integer end, HttpServletRequest request) {
        if (tweets==null || tweets.equals("")) return toStatusJson(JSON_STATUS_FAIL, "You sent empty tweets...", null);

        List list = new LinkedList();
                
    
        try {
            BufferedReader reader = new BufferedReader(new StringReader(tweets));
            String line = null;
            while((line=reader.readLine())!=null){
               list.add(sentimentService.analyzeTweet(line));
            }
            
            Map object = new HashMap();
            object.put("batchId", batchId);
            object.put("start", start);
            object.put("end", end);
            object.put("list", list);
                    
            return toStatusJson(JSON_STATUS_SUCCESS, "OK", object); 
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "AdminController: could not get uploaded file bytes", ex);
            return toStatusJson(JSON_STATUS_FAIL, "Could not get uploaded file bytes", null);
        }
    }
    
    
   

    
    public static String toStatusJson(String status, String message, Object object) {
        Map map = new HashMap();
        map.put("status", status);
        map.put("message", message);
        map.put("object", object);

        Gson gson = getGson();
        String json = gson.toJson(map);
        return json;
    }

    //create gson with custom serializers
    private static Gson getGson() {
        return new GsonBuilder().setDateFormat("dd.MM.yyyy HH:mm").create();
    }
    
    
    //--------------------------------------------------------------------------
    //SETTERS
    //--------------------------------------------------------------------------
    @Autowired
    public void setSentimentService(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }
    
}
