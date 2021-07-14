package com.levon.auction.controller;

import com.levon.auction.model.connection.ConnectionPool;
import com.levon.auction.exception.DaoException;
import com.levon.auction.model.dao.UserDao;
import com.levon.auction.model.entity.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "controller", value = "/controller")
public class Controller extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        try {
            UserDao dao = new UserDao();


            List<User> contracts = dao.findAll();
            for (var contract: contracts) {
                out.println("<h1>" + contract + "</h1>");
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        out.println("</body></html>");
    }

    public void destroy() {
        ConnectionPool.getInstance().destroyPool();
    }
}