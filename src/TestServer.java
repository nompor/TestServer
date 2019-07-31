

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;


public class TestServer extends HttpServlet implements ServletContextListener {

	private static final String servletPath = "C:\\java_servlet_test";
	private static final String multipartTempPath = servletPath+"\\multipart";
	private static final String uploadPath = servletPath+"\\upload";


	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		try {
			process(arg0,arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		try {
			process(arg0,arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		System.out.println("---------------リクエスト処理の開始----------------");
		System.out.println(req.getMethod()+"を検知");
		if (ServletFileUpload.isMultipartContent(req)) {
			System.out.println("multipart/form-dataの検知");

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1426);
			factory.setRepository(new File(multipartTempPath)); //一時的に保存する際のディレクトリ

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(20 * 1024);
			upload.setFileSizeMax(10 * 1024);

			List<FileItem> items;
			try {
				//items = upload.parseRequest(req);
				items = upload.parseRequest(new ServletRequestContext(req));
			} catch (FileUploadException e) {
				throw new ServletException(e);
			}

			for (Object val : items) {
				FileItem item = (FileItem) val;
				String name = item.getFieldName();
				if (item.isFormField()) {
					String value = item.getString("UTF-8");
					System.out.println(name+"="+value);
				} else {
					String uploadPathName = uploadPath+"\\"+name;
					item.write(new File(uploadPathName));
					System.out.println(uploadPathName+"にファイルをアップロードしました。");
				}
			}
		} else {
			Map<String, String[]> map = req.getParameterMap();
			for ( Map.Entry<String, String[]> e : map.entrySet() ) {
				System.out.println(e.getKey()+"="+Arrays.toString(e.getValue()));
			}
		}
		String path = req.getRequestURI();
		if ( path.matches(".+?/test/download/.+?$") ) {
			res.setContentType("application/octet-stream");
			String[] spl = path.split("/");
			res.getOutputStream().write(Files.readAllBytes(Paths.get(uploadPath+"\\"+spl[spl.length-1])));
			System.out.println("ファイルダウンロード要求を検知");
		} else if (path.endsWith("html")) {
			res.setCharacterEncoding("UTF-8");
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			out.println("<html><body><div style='background-color:green;color:white;'>ああああ</div></body></html>");
			System.out.println("HTML要求を検知");
		}
		System.out.println("---------------リクエスト処理の終了----------------");
	}


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			Files.createDirectories(Paths.get(uploadPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Files.createDirectories(Paths.get(multipartTempPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
