package com.baeldung.servlets;

import com.baeldung.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import static com.baeldung.Constants.UPLOAD_DIRECTORY;

@WebServlet(
    name = "MultiPartServlet",
    urlPatterns = {"/multiPartServlet"}
)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1024, maxFileSize = 1024 * 1024 * 5 * 1024, maxRequestSize = 1024*1024)
public class MultipartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename"))
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
        return Constants.DEFAULT_FILENAME;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists())
            uploadDir.mkdir();

        try {
            String fileName = "";
	    String parts = "";

            for (Part part : request.getParts()) {    
                fileName = getFileName(part);

		parts += "FileName: " + fileName + "\n";	
                part.write(uploadPath + File.separator + fileName);
            }
	    
	    String param = "";
	    String params = "";

	    Enumeration e = request.getParameterNames();
	    while (e.hasMoreElements()) {
		param = e.nextElement().toString();
                params += param + ": " + request.getParameterValues(param)[0] + "\n";
	    }

            request.setAttribute("message", "Parameters: \n" + params + "\n" + "Parts a.k.a upladed files: \n" + parts);
        } catch (FileNotFoundException fne) {
            request.setAttribute("message", "There was an error: " + fne.getMessage());
        }
        getServletContext().getRequestDispatcher("/result.jsp").forward(request, response);
    }
}
