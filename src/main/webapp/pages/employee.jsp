<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Legacy employee landing page — kept as a thin redirect to the new dashboard. --%>
<% response.sendRedirect(request.getContextPath() + "/employee-dashboard"); %>
