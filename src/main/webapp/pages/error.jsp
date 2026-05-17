<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="java.io.PrintWriter, java.io.StringWriter" %>
<%
    // Pull any error info Tomcat attached.
    Throwable ex = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    if (ex == null) ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if (ex == null) ex = exception;

    Integer status = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String reqUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
    String userMsg = (String) request.getAttribute("errorMessage");

    String stack = "";
    if (ex != null) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        stack = sw.toString();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <style>
      .err-detail { background:#1f2937; color:#fca5a5; padding:14px; border-radius:8px;
                    font-family:ui-monospace,Menlo,monospace; font-size:12px;
                    white-space:pre-wrap; word-break:break-word; max-height:340px; overflow:auto; }
      .err-card { max-width: 780px; }
    </style>
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card err-card">
        <div class="brand">EMS</div>
        <h2>Something went wrong</h2>

        <div class="alert alert-error">
            <%= userMsg != null ? userMsg
                : (ex != null ? ex.getClass().getSimpleName() + ": " + ex.getMessage()
                              : "An unexpected error occurred.") %>
        </div>

        <% if (status != null || reqUri != null) { %>
            <p class="text-muted" style="font-size:12px;">
                <% if (status != null) { %>HTTP <%= status %><% } %>
                <% if (reqUri != null) { %> · <%= reqUri %><% } %>
            </p>
        <% } %>

        <% if (!stack.isEmpty()) { %>
            <details>
                <summary class="text-muted" style="cursor:pointer; font-size:13px;">Show technical details</summary>
                <div class="err-detail mt-2"><%= stack.replace("<","&lt;").replace(">","&gt;") %></div>
            </details>
        <% } %>

        <div class="footer-link">
            <a href="<%=request.getContextPath()%>/pages/login.jsp">Back to sign in</a>
            &nbsp;·&nbsp;
            <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a>
        </div>
    </div>
</div>
</body>
</html>
