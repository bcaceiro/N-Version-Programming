package client.Server;

import client.Voter.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class MealtimeInsulinDosePersonalRest extends HttpServlet
{

    // No comprehension lists make Maxi very sad...
    private int[] toIntegerArray(String[] in) {
        int[] ret = new int[in.length];
        for (int i = 0; i < in.length; i++)
            ret[i] = Integer.valueOf(in[i]);
        return ret;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int carbohydrateAmount = Integer.valueOf(request.getParameter("carbohydrateAmount"));
            int carbohydrateToInsulinRatio = Integer.valueOf(request.getParameter("carbohydrateToInsulinRatio"));
            int preMealBloodSugar = Integer.valueOf(request.getParameter("preMealBloodSugar"));
            int targetBloodSugar = Integer.valueOf(request.getParameter("targetBloodSugar"));
            int physicalActivityLevel = Integer.valueOf(request.getParameter("physicalActivityLevel"));

            int[] physicalActivitySamples = toIntegerArray(request.getParameter("physicalActivitySamples").split(","));
            int[] bloodSugarDropSamples = toIntegerArray(request.getParameter("bloodSugarDropSamples").split(","));
            System.out.println(Arrays.toString(physicalActivitySamples));
            System.out.println(Arrays.toString(bloodSugarDropSamples));
            Voter v = new Voter(new InsulineServicePersonalSensitivityToInsuline(carbohydrateAmount,carbohydrateToInsulinRatio,preMealBloodSugar,targetBloodSugar,physicalActivityLevel,physicalActivitySamples,bloodSugarDropSamples));
            VoterResults voterResults = v.vote();
            System.out.println(voterResults);
            String json = new Gson().toJson(voterResults);
            response.getWriter().write(json);
        } catch (Exception e) {
            response.getWriter().write("{urls:[], majority:-1, reason: 'Invalid Parameters'};");
        }
    }
}
