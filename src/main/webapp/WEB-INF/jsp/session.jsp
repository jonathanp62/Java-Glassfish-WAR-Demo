<%--
 (#)session.jsp    0.1.0   06/09/2026

 @author   Jonathan Parker
 @version  0.1.0
 @since    0.1.0

 MIT License

 Copyright (c) 2026 Jonathan M. Parker

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
--%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title><fmt:message key="jsp.session.title" /></title>
        <link rel="stylesheet" href="<c:url value='/static/css/styles.css' />" />
    </head>
    <body>
        <h2><fmt:message key="jsp.session.title" /></h2>

        <h3><fmt:message key="jsp.session.info" /></h3>
        <table>
            <tr>
                <th><fmt:message key="jsp.session.id" /></th>
                <td><c:out value="${requestScope.sessionId}" /></td>
            </tr>
            <tr>
                <th><fmt:message key="jsp.session.creationTime" /></th>
                <td><c:out value="${requestScope.creationTime}" /></td>
            </tr>
            <tr>
                <th><fmt:message key="jsp.session.lastAccessedTime" /></th>
                <td><c:out value="${requestScope.lastAccessedTime}" /></td>
            </tr>
            <tr>
                <th><fmt:message key="jsp.session.maxInactiveInterval" /></th>
                <td><c:out value="${requestScope.maxInactiveInterval}" /> <fmt:message key="jsp.session.seconds" /></td>
            </tr>
            <tr>
                <th><fmt:message key="jsp.session.isNew" /></th>
                <td><c:out value="${requestScope.isNew}" /></td>
            </tr>
        </table>

        <h3><fmt:message key="jsp.session.visitCount" />: <c:out value="${requestScope.visitCount}" /></h3>

        <h3><fmt:message key="jsp.session.username" /></h3>
        <c:choose>
            <c:when test="${not empty requestScope.username}">
                <p><fmt:message key="jsp.session.currentUser" />: <strong><c:out value="${requestScope.username}" /></strong></p>
            </c:when>
            <c:otherwise>
                <p><fmt:message key="jsp.session.noUsername" /></p>
            </c:otherwise>
        </c:choose>

        <form method="post" action="<c:url value='/servlet/session' />">
            <p>
                <label for="username"><fmt:message key="jsp.session.enterUsername" /></label><br />
                <input id="username" name="username" type="text" value="<c:out value='${requestScope.username}' />" />
            </p>
            <p>
                <button type="submit"><fmt:message key="jsp.session.storeUsername" /></button>
            </p>
        </form>

        <h3><fmt:message key="jsp.session.actions" /></h3>
        <p>
            <a href="<c:url value='/servlet/session?action=invalidate' />">
                <button type="button"><fmt:message key="jsp.session.invalidate" /></button>
            </a>
        </p>

        <h3><a href="<c:url value='/' />"><fmt:message key="jsp.session.home" /></a></h3>
    </body>
</html>
