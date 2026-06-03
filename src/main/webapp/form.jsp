<%--
 (#)form.jsp    0.1.0   06/03/2026

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
        <title><fmt:message key="form.title.jsp" /></title>
    </head>
    <body>
        <h2><fmt:message key="form.title.jsp" /></h2>

        <c:if test="${not empty requestScope.successMessage}">
            <p><strong><c:out value="${requestScope.successMessage}" /></strong></p>
        </c:if>

        <c:if test="${not empty requestScope.errors}">
            <h3><fmt:message key="form.validation.errors.jsp" /></h3>
            <ul>
                <c:forEach var="err" items="${requestScope.errors}">
                    <li><c:out value="${err}" /></li>
                </c:forEach>
            </ul>
        </c:if>

        <form method="post" action="<c:url value='/servlet/form' />">
            <p>
                <label for="name"><fmt:message key="form.name.jsp" /></label><br />
                <input id="name" name="name" type="text" value="<c:out value='${requestScope.name}' />" />
            </p>
            <p>
                <label for="email"><fmt:message key="form.email.jsp" /></label><br />
                <input id="email" name="email" type="text" value="<c:out value='${requestScope.email}' />" />
            </p>
            <p>
                <label for="comment"><fmt:message key="form.comment.jsp" /></label><br />
                <textarea id="comment" name="comment" rows="5" cols="40"><c:out value="${requestScope.comment}" /></textarea>
            </p>
            <p>
                <button type="submit"><fmt:message key="form.submit.jsp" /></button>
            </p>
        </form>

        <c:if test="${not empty requestScope.name or not empty requestScope.email or not empty requestScope.comment}">
            <%-- Escaping the output with JSTL --%>

            <h3><fmt:message key="form.submission.echo.jsp" /></h3>
            <p><fmt:message key="form.name.jsp" />: <c:out value="${requestScope.name}" /></p>
            <p><fmt:message key="form.email.jsp" />: <c:out value="${requestScope.email}" /></p>
            <p><fmt:message key="form.comment.jsp" />: <c:out value="${requestScope.comment}" /></p>
        </c:if>

        <p><a href="<c:url value='/' />"><fmt:message key="form.home.jsp" /></a></p>
    </body>
</html>
