package com.sunbeam.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sunbeam.daos.CandidateDao;
import com.sunbeam.daos.CandidateDaoImpl;
import com.sunbeam.daos.UserDao;
import com.sunbeam.daos.UserDaoImpl;
import com.sunbeam.pojos.Candidate;
import com.sunbeam.pojos.User;

public class VoteServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//		String candidateId = req.getParameter("candidate");
//		int id = Integer.parseInt(candidateId);
//		try(CandidateDao canDao = new CandidateDaoImpl()){
//			canDao.incrementVote(id);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServletException();
			
		HttpSession session = req.getSession();
				User user = (User) session.getAttribute("curuser");
				//int userId = user.getId();
				int userId = user != null ? user.getId() : 0;
				String message = "";
				
				if(user != null && user.getStatus() == 0) { // if user not yet voted
					String candidateId = req.getParameter("candidate");
					int id = Integer.parseInt(candidateId);
				try(CandidateDao candDao = new CandidateDaoImpl()) {
					candDao.incrementVote(id);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServletException(e);
					}
					message = "Your vote is registerd successfully. <br/><br/>";
					// mark user as voted in db
					try(UserDao userDao = new UserDaoImpl()) {
						userDao.updateStatus(userId, true);
					} catch (Exception e) {
					e.printStackTrace();
						throw new ServletException(e);
					}
				}
				else { // if user already voted
					message = "You have already voted. <br/><br/>";
		 		}
		
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Voted</title>");
		out.println("<body>");
		out.println("<center>");
		//out.println("<h3>Online Voting</h3>");
		
		//header content change
		ServletContext app = this.getServletContext();
		String appTitle = app.getInitParameter("AppTitle");
		out.printf("<h3>%s</h3>", appTitle);
		
		String userName="";
		Cookie[] arr = req.getCookies();
		if(arr != null) {
			for (Cookie c: arr) {
				if(c.getName().equals("uname")) {
						userName = c.getValue();
						break;
				}
			}
		}
		
		out.printf("Hello, %s<hr/>\n", userName);
		//out.println("Your vote is registred successfully. <br/> <br/>");
		out.println(message);
		out.println("<a href='logout'>Sign Out</a>");
		out.println("</center>");
		out.println("</body>");
		out.println("</head>");
		out.println("</html>");

	}
}
