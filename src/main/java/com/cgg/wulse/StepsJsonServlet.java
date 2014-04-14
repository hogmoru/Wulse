package com.cgg.wulse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static java.lang.Math.random;

public class StepsJsonServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String projectOpt = request.getParameter("project");
        String prettyOpt = request.getParameter("pretty");
        String randomFail = request.getParameter("randomFail");
        if (projectOpt==null) {
            throw new IOException("Missing 'project' parameter");
        }
        if (randomFail!=null && random()<0.25) {
            throw new IOException("Random failure!");
        }
        PrintWriter out = response.getWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(out);
        if (prettyOpt!=null) {
            g.setPrettyPrinter(new DefaultPrettyPrinter());
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        List<Step> steps = getSteps(projectOpt);
        g.writeStartObject();
        g.writeArrayFieldStart("steps");
        for (Step step : steps) {
            g.writeStartObject();
            g.writeStringField("name", step.name);
            g.writeStringField("color", step.color);
            g.writeStringField("start", dateFormat.format(step.startDate));
            g.writeStringField("end", dateFormat.format(step.endDate));
            g.writeNumberField("success", step.success);
            g.writeNumberField("running", step.running);
            g.writeNumberField("error", step.error);
            g.writeNumberField("total", step.total);
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
        g.close();
    }

    private List<Step> getSteps(String projectOpt) {
        long now = System.currentTimeMillis();
        int offset = projectOpt.hashCode() % (123*60*60);
        long t0 = now - offset * 1000L;
        long day = 24 * 60 * 60 * 1000L;
        return Arrays.asList(
                randomStep(projectOpt+"_s1", t0, 5),
                randomStep(projectOpt+"_s2", t0+2*day, 4),
                randomStep(projectOpt+"_s3", t0+3*day, 8),
                randomStep(projectOpt+"_s4", t0+6*day, 12)
        );
    }

    private static Step randomStep(String baseName, long minDate, int maxDays) {
        long extent = maxDays * 24*60*60*1000L;
        long start = minDate + (long) (extent * random());
        int total = (int) (10 + random()*190);
        int success = (int) (total * random());
        int running = (int) ((total-success) * random());
        int error = (int) ((total-success-running) * random());
        StringBuilder color = new StringBuilder("#");
        for (int i=5; i>=0; i--) {
            color.append(Integer.toHexString((int) (16*Math.random())));
        }
        return new Step(
                baseName,
                color.toString(),
                start,
                start + extent + (long) (extent * random()),
                success, running, error, total
        );
    }

    private static class Step {
        private String name;
        private String color;
        private Date startDate;
        private Date endDate;
        private int success, running, error, total;

        private Step(String name, String color, long startDate, long endDate, int success, int running, int error, int total) {
            this.name = name;
            this.color = color;
            this.startDate = new Date(startDate);
            this.endDate = new Date(endDate);
            this.success = success;
            this.running = running;
            this.error = error;
            this.total = total;
        }
    }
}