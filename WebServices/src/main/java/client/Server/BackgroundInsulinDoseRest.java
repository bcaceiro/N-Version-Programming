package client.Server;

import client.Voter.InsulineServiceBackgroundInsulineDose;
import client.Voter.InsulineServiceMealtimeInsulinDose;
import client.Voter.Voter;
import client.Voter.VoterResults;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackgroundInsulinDoseRest extends HttpServlet
{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int weight = Integer.valueOf(request.getParameter("weight"));
            Voter v = new Voter(new InsulineServiceBackgroundInsulineDose(weight));
            VoterResults voterResults = v.vote();
            System.out.println(voterResults);
            String json = new Gson().toJson(voterResults);
            response.getWriter().write(json);
        } catch (Exception e) {
            response.getWriter().write("{urls:[], majority:-1, reason: 'Invalid Parameters'};");
        }
    }
}
