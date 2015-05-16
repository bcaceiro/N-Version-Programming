package client.Server;

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

public class MealtimeInsulinDoseRest extends HttpServlet
{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int carbohydrateAmount = Integer.valueOf(request.getParameter("carbohydrateAmount"));
            int carbohydrateToInsulinRatio = Integer.valueOf(request.getParameter("carbohydrateToInsulinRatio"));
            int preMealBloodSugar = Integer.valueOf(request.getParameter("preMealBloodSugar"));
            int targetBloodSugar = Integer.valueOf(request.getParameter("targetBloodSugar"));
            int personalSensitivity = Integer.valueOf(request.getParameter("personalSensitivity"));
            System.out.println(carbohydrateAmount);
            System.out.println(carbohydrateToInsulinRatio);
            System.out.println(preMealBloodSugar);
            System.out.println(targetBloodSugar);
            System.out.println(personalSensitivity);
            Voter v = new Voter(new InsulineServiceMealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity));
            VoterResults voterResults = v.vote();
            System.out.println(voterResults);
            String json = new Gson().toJson(voterResults);
            response.getWriter().write(json);
        } catch (Exception e) {
            response.getWriter().write("{urls:[], majority:-1, reason: 'Invalid Parameters'};");
        }
    }
}
