package com.exec.module;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileExtensionExtractor
 */
public class FileExtensionExtractor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String[] filenames;
	private PrintWriter out;
	private String userId;
	private int i;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileExtensionExtractor() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();
		userId = request.getParameter("uid");
		final Collection<File> text = new ArrayList<File>();
		addFilesRecursively(new File("D:/test/vijay/Text/"), text, "txt");
		out.println(text);
		final Collection<File> image = new ArrayList<File>();
		addFilesRecursively(new File("D:/test/vijay/images/"), image, "png",
				"jpg", "jpeg");
		out.println(image);
		i = 0;
		response.setContentType("text/html");

		for (i = 0; i < text.size(); i++) {
			response.setHeader("text", ((File) text.toArray()[i]).getName());
			response.addHeader("text", ((File) text.toArray()[i]).getName());

		}
		i = 0;
		response.setHeader("image", ((File) image.toArray()[i]).getName());

		for (i = 0; i < image.size(); i++) {

			response.addHeader("image", ((File) image.toArray()[i]).getName());

		}
	}

	public void addFilesRecursively(File file, Collection<File> all, String type) {
		final File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				if (type.equals(getExtension(child)))
					all.add(child);
				addFilesRecursively(child, all, type);
			}
		}
	}

	public void addFilesRecursively(File file, Collection<File> all,
			String type1, String type2, String type3) {
		final File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				if (type1.equals(getExtension(child))
						|| type2.equals(getExtension(child))
						|| type3.equals(getExtension(child)))
					all.add(child);
				addFilesRecursively(child, all, type1, type2, type3);
			}
		}
	}

	public String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
