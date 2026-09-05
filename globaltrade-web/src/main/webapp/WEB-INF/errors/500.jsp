<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<div class="text-center p-5">
    <i class="bi bi-exclamation-triangle text-danger" style="font-size:4rem;"></i>
    <h1 class="display-4 text-danger mt-3">500</h1>
    <h4 class="mb-3">Internal Server Error</h4>
    <p class="text-muted">An unexpected error occurred. Our team has been notified.</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
        <i class="bi bi-house me-1"></i>Return to Dashboard</a>
</div>