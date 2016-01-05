package mpj.baidu.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpj.baidu.utils.API;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class wordlibrary2
 */
@WebServlet("/wordlibrary2")
public class wordlibrary2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public wordlibrary2() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String data1="[1,1,1,1,0.9997863247863248,1,0.6421228118921922,0.9669642857142857,0.998390989541432,0.9960254372019078,0.9984411535463756,1,0.9948979591836735,0.958974358974359,0.9993887530562348,1,1,1,1]";
		String data2="[0.979906895316914,0.9545919236667234,0.9804961505560308,0.9799812590510265,0.9029507800217681,0.982911246247402,0.9621291448516579,0.910569970225436,0.9450888002899601,0.9892804937469547,0.9847813238770685,0.8704909819639278,0.9359814886232164,0.9407183725365543,0.9562874251497006,0.9858466289243438,0.9736205227492739,0.951627088830255,0.9607029533805224]";
		JSONArray label = JSONArray.fromObject(API.TIME);
		JSONObject json = new JSONObject();
		json.put("label", label);
		json.put("data1", data1);
		json.put("data2", data2);
		request.setAttribute("data",json);
		request.getRequestDispatcher("chart/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
