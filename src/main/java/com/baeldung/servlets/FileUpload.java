package com.baeldung.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet(
    name = "FileUpload",
    urlPatterns = {"/FileUpload"}
)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1024, maxFileSize = 1024 * 1024 * 5 * 1024, maxRequestSize = 1024*1024)
public class FileUpload extends HttpServlet {

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename"))
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
        return "default.file";
    }

    protected void formatResponse(HttpServletResponse response, String message)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;

        try {
            out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FileUpload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println(message);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        formatResponse(response, "Servlet works!");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String uploadPath = getServletContext().getRealPath("") + File.separator + "upload";
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists())
            uploadDir.mkdir();

        String message = "";

        try {
            String param = "";
            String params = "";

            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements()) {
                param = e.nextElement().toString();
                params += param + ": " + request.getParameterValues(param)[0] + "\n";
            }

            String fileName = "";
            String parts = "";

            for (Part part : request.getParts()) {
                fileName = getFileName(part);

                parts += "FileName: " + fileName + "\n";
                part.write(uploadPath + File.separator + fileName);
            }

            message = "Parameters: \n" + params + "\n" + "Parts a.k.a uploaded files: \n" + parts;
        } catch (FileNotFoundException fne) {
            message = "There was an error: " + fne.getMessage();
        }

        formatResponse(response, message);
    }
}
