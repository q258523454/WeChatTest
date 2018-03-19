package controller;
// Created by ZhangJian on 18/3/19.

import util.CheckUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class WXServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String signature = request.getParameter("signature");

        String timestamp = request.getParameter("timestamp");

        String nonce = request.getParameter("nonce");

        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();

        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {

        //如果校验成功，将得到的随机字符串原路返回

            out.print(echostr);

        }

    }
}
