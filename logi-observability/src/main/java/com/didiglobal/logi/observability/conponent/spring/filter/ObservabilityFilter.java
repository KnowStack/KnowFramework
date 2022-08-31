package com.didiglobal.logi.observability.conponent.spring.filter;

import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.common.constant.Constant;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 可观察拦截器 高优先级 优先处理
 */
@Component
@WebFilter(urlPatterns = "/", filterName = "observabilityFilter")
public class ObservabilityFilter implements Ordered, Filter {

    private static final TextMapPropagator TEXT_MAP_PROPAGATOR = Observability.getTextMapPropagator();
    private static final Tracer tracer = Observability.getTracer(ObservabilityFilter.class.getName());

    /*
     * extract the context from http headers
     */
    private static final TextMapGetter<HttpServletRequest> getter =
            new TextMapGetter<HttpServletRequest>() {
                @Override
                public Iterable<String> keys(HttpServletRequest carrier) {
                    List<String> iterable = new ArrayList<>();
                    Enumeration<String> enumeration = carrier.getHeaderNames();
                    while (enumeration.hasMoreElements()) {
                        String headerName = enumeration.nextElement();
                        iterable.add(headerName);
                    }
                    return iterable;
                }
                @Override
                public String get(HttpServletRequest carrier, String key) {
                    String headerValue = carrier.getHeader(key);
                    return headerValue == null ? StringUtils.EMPTY : headerValue;
                }
            };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Context context = TEXT_MAP_PROPAGATOR.extract(Context.current(), request, getter);
        String requestUri = request.getRequestURI();
        Span span = tracer.spanBuilder(
                String.format("%s.%s", this.getClass().getName(), "doFilter")
        ).setParent(context).setSpanKind(SpanKind.SERVER).startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Set the Semantic Convention
            span.setAttribute(Constant.COMPONENT, Constant.COMPONENT_HTTP);
            span.setAttribute(Constant.HTTP_METHOD, request.getMethod());
            /*
             One of the following is required:
             - http.scheme, http.host, http.target
             - http.scheme, http.server_name, net.host.port, http.target
             - http.scheme, net.host.name, net.host.port, http.target
             - http.url
            */
            span.setAttribute(Constant.HTTP_SCHEMA, Constant.COMPONENT_HTTP);
            span.setAttribute(Constant.HTTP_HOST, String.format("%s:%d", request.getLocalAddr(), request.getLocalPort()));
            span.setAttribute(Constant.HTTP_TARGET, requestUri);
            // Process the request
            filterChain.doFilter(servletRequest, servletResponse);
            span.setStatus(StatusCode.OK);
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR, ex.getMessage());
        } finally {
            // Close the span
            span.end();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}